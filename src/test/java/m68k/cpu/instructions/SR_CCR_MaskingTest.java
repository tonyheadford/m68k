package m68k.cpu.instructions;


import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import m68k.util.TestCpuUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Federico Berti
 * <p>
 * Copyright 2021
 */
public class SR_CCR_MaskingTest {

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
    public void testCCR(){
        cpu.setCCRegister(0xFF);
        TestCpuUtil.assertEquals(0xFF & Cpu.CCR_MASK, cpu.getCCRegister());
        cpu.setSR(0x27FF);
        TestCpuUtil.assertEquals(0x27FF & Cpu.CCR_MASK, cpu.getCCRegister());
    }

    @Test public void testSR(){
        cpu.setSR(0xFFFF);
        TestCpuUtil.assertEquals(0xFFFF & Cpu.SR_MASK, cpu.getSR());
        cpu.setCCRegister(0xFF);
        int sr = cpu.getSR();
        TestCpuUtil.assertEquals(sr, cpu.getSR());
    }
}
