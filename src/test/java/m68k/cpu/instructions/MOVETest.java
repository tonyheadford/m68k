package m68k.cpu.instructions;

import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import m68k.util.TestCpuUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;


/**
 * Federico Berti
 * <p>
 * Copyright 2021
 */
public class MOVETest {
    AddressSpace bus;
    Cpu cpu;
    int stack = 0x231;

    @BeforeEach
    public void setUp() {
        bus = new MemorySpace(1);    //create 1kb of memory for the cpu
        cpu = new MC68000();
        cpu.setAddressSpace(bus);
        cpu.reset();
        cpu.setAddrRegisterLong(7, stack);
    }

    @Test
    public void testMoveByteA7() {
        int opcode = 0x1F0F; //move.b	a7,-(a7)
        TestCpuUtil.writeCodeAndSetPc(cpu, bus, 0, opcode);
        cpu.setAddrRegisterLong(0, stack);
        cpu.execute();
        //a7 is decremented by 2 instead of 1
        assertEquals("Check for a7", stack & 0xFF, cpu.readMemoryByte(stack - 2));
        TestCpuUtil.assertNotEquals("Check for a7", stack & 0xFF, cpu.readMemoryByte(stack - 1));
    }

    @Test public void testMoveByte() {
        int opcode = 0x1108; //move.b	a0,-(a0)
        TestCpuUtil.writeCodeAndSetPc(cpu, bus, 0, opcode);
        cpu.setAddrRegisterLong(0, stack);
        cpu.execute();
        assertEquals("Check for a0", stack & 0xFF, cpu.readMemoryByte(stack - 1));
    }

    @Test public void testMoveWord() {
        int opcode = 0x3108; //move.w	a0,-(a0)
        TestCpuUtil.writeCodeAndSetPc(cpu, bus, 0, opcode);
        cpu.setAddrRegisterLong(0, stack);
        cpu.execute();
        assertEquals("Check for a0", stack, cpu.readMemoryWord(stack - 2));
    }

    @Test public void testMoveLong() {
        int opcode = 0x22c9; //move.l	a1,(a1)+
        TestCpuUtil.writeCodeAndSetPc(cpu, bus, 0, opcode);
        cpu.setAddrRegisterLong(1, stack);
        cpu.execute();
        assertEquals("Check for a1", stack, cpu.readMemoryLong(stack));
    }

}
