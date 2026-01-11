package miggy.cpu.instructions.bcd;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class NBCDTest extends BasicSetup {
    public NBCDTest(String test) {
        super(test);
    }

    public void testReg() {
        setInstruction(0x4800);    //nbcd d0
        SystemModel.CPU.setDataRegister(0, 0x0099);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 1, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testMem() {
        setInstruction(0x4820);    //nbcd -(a0)
        SystemModel.CPU.setAddrRegister(0, codebase + 100);
        SystemModel.MEM.poke(codebase + 98, 0x0099, Size.Word);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0001, SystemModel.MEM.peek(codebase + 98, Size.Word));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}