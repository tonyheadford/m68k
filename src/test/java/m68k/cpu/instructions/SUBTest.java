package m68k.cpu.instructions;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2018
 * <p>
 */

import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SUBTest {
    AddressSpace bus;
    Cpu cpu;

    @BeforeEach public void setUp() {
        bus = new MemorySpace(1);    //create 1kb of memory for the cpu
        cpu = new MC68000();
        cpu.setAddressSpace(bus);
        cpu.reset();
        cpu.setAddrRegisterLong(7, 0x200);
    }

    @Test
    public void testSUB_byte_zeroFlag() {
        bus.writeWord(4, 0x9402);    // sub.b d2,d2
        testSUB_byte_zeroFlag(cpu, true, 0xFFFF_FF80, 0xFFFF_FF00);
    }

    @Test public void testSUBQ_byte_zeroFlag() {
        bus.writeWord(4, 0x5502);    // subi.b #2,d2
//        bus.writeWord(6, 2);
        testSUB_byte_zeroFlag(cpu, true, 0x0001_0102, 0x0001_0100);
    }

    @Test public void testSUBI_byte_zeroFlag() {
        bus.writeWord(4, 0x0402);    // subi.b #2,d2
        bus.writeWord(6, 2);
        testSUB_byte_zeroFlag(cpu, true, 0x0001_0102, 0x0001_0100);
    }

    private void testSUB_byte_zeroFlag(Cpu cpu, boolean expectedZFlag, long d2_pre, long d2_post) {
        cpu.setPC(4);
        cpu.setDataRegisterLong(2, (int) d2_pre);
        cpu.execute();
        Assertions.assertEquals(d2_post, cpu.getDataRegisterLong(2));
        Assertions.assertEquals(0x00, cpu.getDataRegisterByte(2));
        Assertions.assertEquals(expectedZFlag, cpu.isFlagSet(Cpu.Z_FLAG));
    }
}
