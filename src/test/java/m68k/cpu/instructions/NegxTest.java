package m68k.cpu.instructions;

import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import m68k.util.TestCpuUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ${FILE}
 * <p>
 * Check NEGX
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class NegxTest {

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
    public void testNegx01() {
        negxInternal();
    }

    @Test public void testNegx02() {
        cpu.setFlags(Cpu.Z_FLAG);
        negxInternal();
    }

    private void negxInternal() {
        int opcode = 0x4000; //negx.b d0
        TestCpuUtil.writeCodeAndSetPc(cpu, bus, 0, opcode);
        cpu.setDataRegisterLong(0, 0x000000FF);
        cpu.setFlags(Cpu.X_FLAG);
        boolean zFlag = cpu.isFlagSet(Cpu.Z_FLAG);

        cpu.execute();

        assertEquals(0, cpu.getAddrRegisterLong(0));
        assertEquals(zFlag, cpu.isFlagSet(Cpu.Z_FLAG)); //result = 0, z unchanged
    }
}
