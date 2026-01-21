package m68k.cpu.instructions;

import junit.framework.TestCase;
import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;

/**
 * Federico Berti
 * <p>
 * Copyright 2021
 */
public class SP_ByteOperationTest extends TestCase {

    AddressSpace bus;
    Cpu cpu;
    int stack = 0x200;

    public void setUp() {
        bus = new MemorySpace(1);    //create 1kb of memory for the cpu
        cpu = new MC68000();
        cpu.setAddressSpace(bus);
        cpu.reset();
        cpu.setAddrRegisterLong(7, stack);
    }

    public void testSpBytePostInc() {
        int opcode = 0xbf08; //cmpm.b  (A0)+, (A7)+
        cpu.writeMemoryWord(0, opcode);
        cpu.setAddrRegisterLong(7, stack);
        cpu.setAddrRegisterLong(0, 0x300);
        int spExp = stack + 2;

        cpu.execute();

        assertEquals(spExp, cpu.getAddrRegisterLong(7));
    }

    public void testSpBytePreDec() {
        int opcode = 0xdf08; //addx.b  -(A0), -(A7)
        cpu.writeMemoryWord(0, opcode);
        cpu.setAddrRegisterLong(7, stack);
        cpu.setAddrRegisterLong(0, 0x300);
        int spExp = stack - 2;

        cpu.execute();

        assertEquals(spExp, cpu.getAddrRegisterLong(7));
    }
}
