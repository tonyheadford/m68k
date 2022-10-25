package m68k.clktest;


import com.google.gson.Gson;
import m68k.cpu.DisassembledInstruction;
import m68k.cpu.Instruction;
import m68k.cpu.MC68000;
import m68k.util.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class Clk68DecodingTest {

    private static String path = "src/test/resources/";
    private static final String file = "68000.official.json.zip";

    /**
     * Known issues
     * - extra ADD.B ,SUB.B, MOVE.B decoding, should be invalid (harmless)
     * - extra EORI, ANDI, ORI decoding, should be invalid (harmless)
     * - SCC vs S*, BCC vs B*
     *
     */
    private static final int knownMismatches = 20472;

    private static final boolean verbose = false;

    @Test
    public void testDecoding(){
        Path p = Paths.get(path, file);
        Assertions.assertNotNull(p);
        File f = p.toFile();
        Gson a = new Gson();
        MC68000 mc68000 = new MC68000();
        System.out.println(f.getAbsolutePath());
        String s = new String(FileUtil.readBinaryFile(Paths.get(f.getAbsolutePath()), "json"));
        Map<String, String> m = a.fromJson(s, Map.class);
        StringBuilder sb = new StringBuilder(40);

        int count= 0 ;

        for (Map.Entry<String, String> e : m.entrySet()) {
            sb.setLength(0);
            int opcode = Integer.parseInt(e.getKey(), 16);
            String clk = e.getValue().split(" ")[0].toUpperCase();
            Instruction inst = mc68000.getInstructionFor(opcode);
            String head68 = getDisasmSafe(inst, opcode);
            boolean isClkNone = "NONE".equalsIgnoreCase(clk);
            boolean illegal = isClkNone && "????".equals(head68);
//            System.out.println(Integer.toHexString(opcode));
            if (illegal) {
                continue;
            }
            //ignore line-a/f
            if ((opcode >>> 12) == 0xA || (opcode >>> 12) == 0xF) {
                continue;
            }
            if (isClkNone) {
                if(verbose) System.out.println(Integer.toHexString(opcode) + "," + e.getValue() + "," + head68);
                count++;
            } else if (!clk.substring(0,2).equals(head68.substring(0, 2))) {
                if(verbose) System.out.println(Integer.toHexString(opcode) + "," + e.getValue() + "," + head68);
                count++;
            }
        }
        Assertions.assertEquals(knownMismatches, count );
    }

    private String getDisasmSafe(Instruction inst, int opcode){
        String disasm;
        try {
            DisassembledInstruction dis = inst.disassemble(0, opcode);
            disasm = dis.instruction.toUpperCase();
        } catch (Exception e){
            if(verbose) System.err.println(Integer.toHexString(opcode) + "," + inst.getClass().getSimpleName() + "," + "ERROR");
            disasm = "ERROR: " + inst;
        }
        return disasm;
    }
}
