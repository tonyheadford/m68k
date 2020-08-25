package miggy.cpu.instructions.add;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class ADDXTest extends BasicSetup {
    public ADDXTest(String test) {
        super(test);
    }

    public void testByte() {
        setInstruction(0xd101);    //addx.b d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0x78);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123456F1, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testWord() {
        setInstruction(0xd141);    //addx.w d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xaa78);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123400F1, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testLong() {
        setInstruction(0xd181);    //addx.l d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xf8765432);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0aaaaaab, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}
