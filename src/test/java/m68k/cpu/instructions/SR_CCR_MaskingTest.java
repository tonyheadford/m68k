package m68k.cpu.instructions;

import junit.framework.TestCase;
import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import org.junit.Assert;

/**
 * Federico Berti
 * <p>
 * Copyright 2021
 */
public class SR_CCR_MaskingTest extends TestCase {

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

    public void testCCR(){
        cpu.setCCRegister(0xFF);
        Assert.assertEquals(0xFF & Cpu.CCR_MASK, cpu.getCCRegister());
        cpu.setSR(0x27FF);
        Assert.assertEquals(0x27FF & Cpu.CCR_MASK, cpu.getCCRegister());
    }

    public void testSR(){
        cpu.setSR(0xFFFF);
        Assert.assertEquals(0xFFFF & Cpu.SR_MASK, cpu.getSR());
        cpu.setCCRegister(0xFF);
        int sr = cpu.getSR();
        Assert.assertEquals(sr, cpu.getSR());
    }
}
