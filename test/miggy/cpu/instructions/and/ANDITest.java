package miggy.cpu.instructions.and;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class ANDITest extends BasicSetup {
    public ANDITest(String test) {
        super(test);
    }

    public void testByte() {
        setInstructionParamW(0x0200, 0x000f);    //andi.b #$0f, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x12345608, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testWord() {
        setInstructionParamW(0x0240, 0xf00f);    //andi.w #$f00f, d0
        SystemModel.CPU.setDataRegister(0, 0x1234c678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x1234c008, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testLong() {
        setInstructionParamL(0x0280, 1);    //andi.l #1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}
