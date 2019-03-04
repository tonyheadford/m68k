package miggy.cpu.instructions.sub;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class SUBXTest extends BasicSetup {
    public SUBXTest(String test) {
        super(test);
    }

    public void testByte() {
        setInstruction(0x9101);    //subx.b d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0x78);

        SystemModel.CPU.setCCR((byte) 0x1f);    //all set

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123456ff, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testWord() {
        setInstruction(0x9141);    //subx.w d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xaa78);

        SystemModel.CPU.setCCR((byte) 0);    //all clr

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x1234ac00, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testLong() {
        setInstruction(0x9181);    //subx.l d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0x87654321);

        SystemModel.CPU.setCCR((byte) 0x1f);    //all set

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x8acf1356, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
