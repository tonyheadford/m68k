package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class CHKTest extends BasicSetup {
    public CHKTest(String test) {
        super(test);
    }

    public void testNeg() {
        setInstruction(0x4181);    //chk d1,d0
        SystemModel.CPU.setDataRegister(0, 0xc321);
        SystemModel.CPU.setDataRegister(1, 0x5678);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 6, SystemModel.CPU.getPC());

        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testGreater() {
        setInstruction(0x4181);    //chk d1,d0
        SystemModel.CPU.setDataRegister(0, 0x6321);
        SystemModel.CPU.setDataRegister(1, 0x5678);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 6, SystemModel.CPU.getPC());

        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testNoException() {
        setInstruction(0x4181);    //chk d1,d0
        SystemModel.CPU.setDataRegister(0, 0x4321);
        SystemModel.CPU.setDataRegister(1, 0x5678);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertFalse("Check CPU not in supervisor mode", SystemModel.CPU.isSupervisorMode());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
