package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class LINKTest extends BasicSetup {
    public LINKTest(String test) {
        super(test);
    }

    public void testInstruction() {
        setInstructionParamW(0x4e56, 0xfff8);    //link a6,#-8
        SystemModel.CPU.setAddrRegister(6, 0x87654321);

        SystemModel.CPU.setCCR((byte) 0);
        int stack = SystemModel.CPU.getAddrRegister(7);
        int time = SystemModel.CPU.execute();

        assertEquals("Check stack", stack - 12, SystemModel.CPU.getAddrRegister(7));
        assertEquals("Check A6", stack - 4, SystemModel.CPU.getAddrRegister(6));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
