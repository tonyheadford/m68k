package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class MULUTest extends BasicSetup {
    public MULUTest(String test) {
        super(test);
    }

    public void testPos() {
        setInstruction(0xc0c1);    //mulu d1,d0

        SystemModel.CPU.setDataRegister(0, 0x7765);
        SystemModel.CPU.setDataRegister(1, 0x0345);
        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x01865d39, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testNeg() {
        setInstruction(0xc0c1);    //mulu d1,d0

        SystemModel.CPU.setDataRegister(0, 0xffff8765);
        SystemModel.CPU.setDataRegister(1, 0x0033);
        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x001af91f, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}

