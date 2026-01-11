package miggy.cpu.instructions.move;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class MOVEATest extends BasicSetup {
    public MOVEATest(String test) {
        super(test);
    }

    public void testWord() {
        setInstruction(0x3040);    //movea.w d0, a0
        SystemModel.CPU.setDataRegister(0, 0x8004);
        SystemModel.CPU.setAddrRegister(0, 0xc7c7c7c7);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0xffff8004, SystemModel.CPU.getAddrRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testLong() {
        setInstruction(0x2040);    //movea.l d0, a0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setAddrRegister(0, 0xc7c7c7c7);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x87654321, SystemModel.CPU.getAddrRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
