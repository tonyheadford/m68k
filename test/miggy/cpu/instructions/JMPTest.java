package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class JMPTest extends BasicSetup {
    public JMPTest(String test) {
        super(test);
    }

    public void testInstruction() {
        setInstructionParamL(0x4ed0, 0x00200000);

        SystemModel.CPU.setAddrRegister(0, 2);
        SystemModel.CPU.setCCR((byte) 0);
        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", 0x0002, SystemModel.CPU.getPC());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
