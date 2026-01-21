package miggy.cpu.instructions.move;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class MOVE_2_SRTest extends BasicSetup {
    public MOVE_2_SRTest(String test) {
        super(test);
    }

    public void testMove() {
        setInstruction(0x46c0);    //move d0,sr
        SystemModel.CPU.setDataRegister(0, 0x2015);

        SystemModel.CPU.setSR((short) 0x2000);    //set supervisor bit

        int time = SystemModel.CPU.execute();

        assertEquals("Check SR", (short) 0x2015, SystemModel.CPU.getSR());
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testMoveException() {
        setInstruction(0x46c0);    //move d0,sr
        SystemModel.CPU.setDataRegister(0, 0x2015);

        SystemModel.CPU.setSR((short) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check SR", 0x2000, SystemModel.CPU.getSR()); //supervisor bit set
        assertEquals("Check PC", 8, SystemModel.CPU.getPC());    //privilege violation exception
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}