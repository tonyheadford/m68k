package m68k.clktest.json_schema;

import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.cpu.Size;
import m68k.memory.MemorySpace;
import m68k.util.MC68000Helper;

import java.util.stream.IntStream;

import static m68k.util.MC68000Helper.th;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class JsonTestUtil {

    public static MC68000Helper.M68kState toStateObject(MC68000 m68k){
        final MC68000Helper.M68kState state = new MC68000Helper.M68kState();
        IntStream.range(0, 8).forEach(i -> state.ar[i] = m68k.getAddrRegisterLong(i));
        IntStream.range(0, 8).forEach(i -> state.dr[i] = m68k.getDataRegisterLong(i));
        state.pc = m68k.getPC();
        state.sr = m68k.getSR();
        state.ssp = m68k.getSSP();
        state.usp = m68k.getUSP();
        boolean isSupervisor = m68k.isFlagSet(Cpu.SUPERVISOR_FLAG);
        //sync between a7 and ssp happens on mode switching
        if(isSupervisor && m68k.getSSP() != state.ar[7]){
            state.ssp = state.ar[7];
        } else if(!isSupervisor && m68k.getUSP() != state.ar[7]){
            state.usp = state.ar[7];
        }
        return state;
    }

    public static void fromStateObject(MC68000Helper.M68kState state, MC68000 m68k){
        IntStream.range(0, 8).forEach(i -> m68k.setAddrRegisterLong(i, state.ar[i]));
        IntStream.range(0, 8).forEach(i -> m68k.setDataRegisterLong(i, state.dr[i]));
        m68k.setPC(state.pc);
        m68k.setSR(state.sr);
        m68k.setSSP(state.ssp);
        m68k.setUSP(state.usp);
    }

    public static class TestAddressSpace extends MemorySpace {

        public boolean addressError, verbose;
        public final int mask;

        public TestAddressSpace(int sizeKb) {
            super(sizeKb);
            mask = size() - 1;
        }

        private boolean checkAlignment(int address, Size size) {
            if (size != Size.Byte && (address & 1) == 1) {
                addressError = true;
                if(verbose) System.out.println("AE,"+ th(address) + "," + size);
            }
            return addressError;
        }

        @Override
        public int readByte(int addr) {
            int res = super.readByte(addr & mask);
            if(verbose) System.out.println("R,"+ th(addr) + "," + th(res) + "," + Size.Byte);
            return res;
        }

        @Override
        public int readWord(int addr) {
            checkAlignment(addr, Size.Word);
            if(addressError){
                return 1; //avoid divby0 in DIVU
            }
            int res = super.readWord(addr & mask);
            if(verbose) System.out.println("R,"+ th(addr) + "," + th(res) + "," + Size.Word);
            return res;
        }

        @Override
        public int readLong(int addr) {
            checkAlignment(addr, Size.Long);
            if(addressError){
                return 1; //avoid divby0 in DIVU
            }
            int res = super.readLong(addr & mask);
            if(verbose) System.out.println("R,"+ th(addr) + "," + th(res) + "," + Size.Long);
            return res;
        }

        @Override
        public void writeWord(int addr, int value) {
            if(verbose) System.out.println("W,"+ th(addr) + "," + th(value) + "," + Size.Word);
            checkAlignment(addr, Size.Word);
            if(!addressError) {
                super.writeWord(addr & mask, value);
            }
        }

        @Override
        public void writeLong(int addr, int value) {
            if(verbose) System.out.println("W,"+ th(addr) + "," + th(value) + "," + Size.Long);
            checkAlignment(addr, Size.Long);
            if(!addressError) {
                super.writeLong(addr & mask, value);
            }
        }

        @Override
        public void writeByte(int addr, int value) {
            if(verbose) System.out.println("W,"+ th(addr) + "," + th(value) + "," + Size.Byte);
            super.writeByte(addr & mask, value);
        }
    }

    public static TestAddressSpace createTestAddressSpace(int sizeKb){
        return new TestAddressSpace(sizeKb);
    }
}
