package miggy.cpu.instructions;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class PEATest extends BasicSetup {
    public PEATest(String test) {
        super(test);
    }

    public void testInstruction() {
        setInstructionAtPC(0x4850);    //pea (a0)
        SystemModel.CPU.setAddrRegister(0, 0x00002000);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check pushed value", 0x00002000, SystemModel.MEM.peek(SystemModel.CPU.getAddrRegister(7), Size.Long));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
