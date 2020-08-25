package miggy.cpu.instructions.bitshift;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class ROXLTest extends BasicSetup {
    public ROXLTest(String test) {
        super(test);
    }

    public void testByte() {
        setInstruction(0xe330);    //roxl.b d1,d0
        SystemModel.CPU.setDataRegister(0, 0x876543e8);
        SystemModel.CPU.setDataRegister(1, 4);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x87654387, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testWord() {
        setInstruction(0xe150);    //roxl.w #8,d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x87652121, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testLong() {
        setInstruction(0xe3b0);    //roxl.l d1,d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setDataRegister(1, 18);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0c850eca, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testMem() {
        setInstruction(0xe5d0);    //roxl (a0)
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0eca4321, SystemModel.MEM.peek(32, Size.Long));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}
