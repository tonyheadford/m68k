package m68k.cpu.instructions;


import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.writeCodeAndSetPc;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2020
 */
public class MOVEMTest {

    AddressSpace bus;
    Cpu cpu;
    int stack = 0x200;

    @BeforeEach
    public void setUp() {
        bus = new MemorySpace(1);    //create 1kb of memory for the cpu
        cpu = new MC68000();
        cpu.setAddressSpace(bus);
        cpu.reset();
        cpu.setAddrRegisterLong(7, stack);
    }

    @Test
    public void testR2MWordPreDec() {
        //movem.w	a0,-(a0)
        writeCodeAndSetPc(cpu, bus, 0, 0x48a0, 0x0080);
        cpu.setAddrRegisterLong(0, stack);
        cpu.execute();
        assertEquals("Check for a0", stack, cpu.readMemoryWord(stack - 2));
    }

    @Test public void testR2MLongPreDec() {
        //movem.l	a0,-(a0)
        writeCodeAndSetPc(cpu, bus, 0, 0x48e0, 0x0080);
        cpu.setAddrRegisterLong(0, stack);
        cpu.execute();
        assertEquals("Check for a0", stack, cpu.readMemoryLong(stack - 4));
    }
}
