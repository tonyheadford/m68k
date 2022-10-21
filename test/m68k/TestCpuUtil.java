package m68k;

import m68k.cpu.Cpu;
import m68k.memory.AddressSpace;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class TestCpuUtil {

    public static void writeCodeAndSetPc(Cpu cpu, AddressSpace bus, int pc, int... opcodes){
        int pos = pc;
        for (int opc : opcodes) {
            bus.writeWord(pos, opc);
            pos += 2;
        }
        cpu.setPC(pc);
    }
}
