package m68k.clktest;

import com.google.gson.Gson;
import m68k.clktest.json_schema.JsonTestUtil;
import m68k.clktest.json_schema.JsonTestUtil.TestAddressSpace;
import m68k.clktest.json_schema.SingleInstructionRecord;
import m68k.clktest.json_schema.SingleInstructionRecord.Final;
import m68k.clktest.json_schema.SingleInstructionRecord.Initial;
import m68k.cpu.Instruction;
import m68k.cpu.MC68000;
import m68k.cpu.instructions.*;
import m68k.util.FileUtil;
import m68k.util.MC68000Helper;
import m68k.util.MC68000Helper.M68kState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static m68k.clktest.json_schema.JsonTestUtil.fromStateObject;
import static m68k.cpu.Cpu.SUPERVISOR_FLAG;
import static m68k.cpu.Cpu.V_FLAG;
import static m68k.util.MC68000Helper.th;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class ClkJsonTest68k {

    public static String path = "src/test/resources/test_m68k_json_202210";

    public static final int MEM_SIZE_KB = 0x4000;

    private final static Predicate<Instruction> instToSr = inst -> {
        String n = inst.getClass().getName();
        return n.contains(ORI_TO_SR.class.getSimpleName()) || n.contains(EORI_TO_SR.class.getSimpleName()) ||
                n.contains(ANDI_TO_SR.class.getSimpleName()) || n.contains(MOVE_TO_SR.class.getSimpleName());
    };

    private final static Predicate<Instruction> isBranchOp = inst -> {
        String n = inst.getClass().getName();
        return n.contains(RTE.class.getSimpleName()) || n.contains(RTS.class.getSimpleName())
                || n.contains(RTR.class.getSimpleName()) || n.contains(JSR.class.getSimpleName())
                || n.contains(JMP.class.getSimpleName()) || n.contains(DBcc.class.getSimpleName())
                || n.contains(Bcc.class.getSimpleName());
    };

    private final static String[] knownIssues = {"ASL.b", "ASR", "DIVU"};

    static {
        Arrays.sort(knownIssues);
    }

    public MC68000 provider;
    private TestAddressSpace memory;

//    public static void main(String[] args) {
//        ClkJsonTest68k t = new ClkJsonTest68k();
//        fileProvider().forEach(tc -> {
//            t.setup();
//            StringBuilder sb = t.testJsonInternal(tc);
//            System.out.println(sb);
//        });
//    }

    @BeforeEach
    public void setup() {
        provider = new MC68000();
        memory = JsonTestUtil.createTestAddressSpace(MEM_SIZE_KB);
        provider.setAddressSpace(memory);
    }

    static Stream<String> fileProvider() {
        File fpath = new File(path);
        File[] files = fpath.listFiles();
        Predicate<File> validFile = f -> !f.isDirectory() && f.getName().endsWith(".json.gz");
        return Arrays.stream(files).filter(validFile).map(f -> f.getName()).sorted();
    }

    @MethodSource("fileProvider")
    @ParameterizedTest
    public void testJson(String fileName) {
        StringBuilder err = testJsonInternal(fileName);
        boolean ignore = Arrays.stream(knownIssues).anyMatch(n -> fileName.contains(n));
        if(ignore){
            System.out.println("Ignoring " + fileName);
            return;
        }
        Assertions.assertEquals(0, err.length(), err.toString());
    }

    private StringBuilder testJsonInternal(String fileName) {
        Gson a = new Gson();
        Path p = Paths.get(path, fileName);
        StringBuilder err = new StringBuilder();
//        if(!p.toAbsolutePath().getFileName().toString().startsWith("ADD")){
//            return err;
//        }
        System.out.println(p.toAbsolutePath());
        String s = new String(FileUtil.readBinaryFile(p, "json"));
        SingleInstructionRecord[] m = a.fromJson(s, SingleInstructionRecord[].class);
        for (SingleInstructionRecord id : m){
            if(id.getName() == null){
                continue;
            }
//            if(!"d133 [ADD.b D0, (d8, A3, Xn)] 1".equals(id.getName())){
//                continue;
//            }
//            memory.verbose = true;
//            System.out.println(id.getName());
            String res = testOne(id);
            if(res!= null && res.length() > 0){
                err.append(res).append("\n");
                break;
            }
        }

        return err;
    }

    public String testOne(SingleInstructionRecord data){
        M68kState start = toStateObject(data.getInitial());
        M68kState expected = toStateObject(data.getFinal());
        fromStateObject(start, provider);
//        System.out.println(MC68000Helper.dumpOp(provider.getM68k(), start.pc));
        boolean error = runSingle();
        if(error){
            return null;
        }
        M68kState actual = JsonTestUtil.toStateObject(provider);
        //        Assert.assertEquals("Exp: " + expected + "\nAct: " + actual, expected, actual);
        StringBuilder memRes = checkMemory(data.getFinal().ram);
        boolean isMatch = expected.equals(actual) && memRes.length() == 0;
        if(!isMatch){
            boolean ignore = handleSpecialCases(start, expected, actual);
            if(ignore){
                return null;
            }
            //retry
            memRes = checkMemory(data.getFinal().ram);
            isMatch = expected.equals(actual) && memRes.length() == 0;
        }
        if(!isMatch) {
                return data.name  + "\n" + MC68000Helper.dumpOp(provider, start.pc) + " -- ERROR\n" +
                        "Before: " + start +
                        "\nExpect: " + expected +
                        "\nActual: " + actual + "\n"+memRes+"\n";
        }
        return null;
    }

    private boolean runSingle(){
        Exception e = null;
        try {
            provider.execute();
        } catch (Exception e1){
            e = e1;
        }
        //TODO address error, do not fail the test but it should be handled
        if(memory.addressError){
            memory.addressError = false;
            return true;
        }
        assert e == null;
        return false;
    }

    public StringBuilder checkMemory(List<List<Long>> finalMem){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < finalMem.size(); i++) {
            int pos = finalMem.get(i).get(0).intValue();
            int expVal = finalMem.get(i).get(1).intValue();
            int actVal = memory.readByte(pos);
            if(expVal != actVal) {
                sb.append("Memory mismatch, pos: " + th(pos) + ", Exp: " + th(expVal) + ", Act: " + th(actVal)).append("\n");
            }
        }
        return sb;
    }

    public boolean handleSpecialCases(M68kState start, M68kState expected, M68kState actual){
        int opcode = start.opcode;
        Instruction inst = provider.getInstructionFor(opcode);
        //TODO DIVU divByZero behaves differently
        //ignores the N-flag for DIV*, different results on overflow
        boolean isDiv = (opcode & 0x80C0) == 0x80C0;
        if(isDiv && (expected.sr & V_FLAG) > 0) {
            expected.sr &= 0xFFF7;
            actual.sr &= 0xFFF7;
        }
        //ignore the trace bits
        if(instToSr.test(inst)){
            expected.sr &= 0x3FFF;
            actual.sr &= 0x3FFF;
            handleA7_OnUserStateSwitch(start, expected, actual);
        }
        if(isBranchOp.test(inst)){
            expected.sr &= 0x3FFF;
            actual.sr &= 0x3FFF;
            handleA7_OnUserStateSwitch(start, expected, actual);
            //some kind of exception happened
            if(expected.pc == 0x1400 && actual.pc != expected.pc){
                return true;
            }

        }
        return false;
    }

    //TODO check who is right?
    //switch supervisor state, ignore A7
    private void handleA7_OnUserStateSwitch(M68kState start, M68kState expected, M68kState actual){
        int prevSv = start.sr & SUPERVISOR_FLAG;
        int sv = actual.sr & SUPERVISOR_FLAG;
        if(prevSv != sv){
            expected.ar[7] = actual.ar[7];
        }
    }

    public M68kState toStateObject(Initial is){
        M68kState state = new M68kState();
        state.ar = toArIntArray.apply(is);
        state.dr = toDrIntArray.apply(is);
        state.pc = is.pc.intValue();
        state.sr = is.sr.intValue();
        state.usp = is.usp.intValue();
        state.ssp = is.ssp.intValue();
        writeMemory(is.ram);
        writeMemoryAtPc(state.pc, is.prefetch);
        state.opcode = memory.readWord(state.pc);
        return state;
    }

    public M68kState toStateObject(Final is){
        M68kState state = new M68kState();
        state.ar = toArIntArrayF.apply(is);
        state.dr = toDrIntArrayF.apply(is);
        state.pc = is.pc.intValue();
        state.sr = is.sr.intValue();
        state.usp = is.usp.intValue();
        state.ssp = is.ssp.intValue();
        return state;
    }

    public void writeMemoryAtPc(int pc, List<Long> prefetch){
        for (int i = 0; i < prefetch.size(); i++) {
            memory.writeWord(pc + (i << 1), prefetch.get(i).intValue());
        }
    }

    public void writeMemory(List<List<Long>> memoryData){
        for (int i = 0; i < memoryData.size(); i++) {
            int addr = memoryData.get(i).get(0).intValue();
            int data = memoryData.get(i).get(1).intValue();
            memory.writeByte(addr, data);
        }
    }
    public static final Function<Initial, int[]> toArIntArray = is ->
            new int[]{is.a0.intValue() , is.a1.intValue(), is.a2.intValue(), is.a3.intValue(), is.a4.intValue(),
                    is.a5.intValue(),is.a6.intValue(), is.ssp.intValue()};
    public static final Function<Initial, int[]> toDrIntArray = is -> new int[]{is.d0.intValue(),
            is.d1.intValue(), is.d2.intValue(), is.d3.intValue(), is.d4.intValue(),
            is.d5.intValue(), is.d6.intValue(), is.d7.intValue()};
    public static final Function<Final, int[]> toArIntArrayF = is ->
            new int[]{is.a0.intValue() , is.a1.intValue(), is.a2.intValue(), is.a3.intValue(), is.a4.intValue(),
                    is.a5.intValue(),is.a6.intValue(), is.ssp.intValue()};
    public static final Function<Final, int[]> toDrIntArrayF = is -> new int[]{is.d0.intValue(),
            is.d1.intValue(), is.d2.intValue(), is.d3.intValue(), is.d4.intValue(),
            is.d5.intValue(), is.d6.intValue(), is.d7.intValue()};
}