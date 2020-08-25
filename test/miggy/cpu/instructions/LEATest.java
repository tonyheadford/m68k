package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class LEATest extends BasicSetup {
    public LEATest(String test) {
        super(test);
    }

    public void testInstruction() {
        setInstructionParamW(0x41f8, 0x1000);    //lea #$1000,a0
        SystemModel.CPU.setAddrRegister(0, 0x87654321);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x00001000, SystemModel.CPU.getAddrRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
