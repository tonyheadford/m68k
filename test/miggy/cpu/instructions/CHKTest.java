package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel.CpuFlag;

import static miggy.SystemModel.CPU;

// $Revision: 21 $
public class CHKTest extends BasicSetup {
    public CHKTest(String test) {
        super(test);
    }

    public void testNeg() {
        setInstructionAtPC(0x4181);    //chk d1,d0
        CPU.setPC(codebase);
        CPU.setDataRegister(0, 0xc321);
        CPU.setDataRegister(1, 0x5678);

        CPU.setCCR((byte) 0);

        int time = CPU.execute();

        assertTrue("Check CPU in supervisor mode", CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 6, CPU.getPC());

        assertFalse("Check X", CPU.isSet(CpuFlag.X));
        assertTrue("Check N", CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", CPU.isSet(CpuFlag.V));
        assertFalse("Check C", CPU.isSet(CpuFlag.C));
    }

    public void testGreater() {
        setInstructionAtPC(0x4181);    //chk d1,d0
        CPU.setPC(codebase);
        CPU.setDataRegister(0, 0x6321);
        CPU.setDataRegister(1, 0x5678);

        CPU.setCCR((byte) 0);

        int time = CPU.execute();

        assertTrue("Check CPU in supervisor mode", CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 6, CPU.getPC());

        assertFalse("Check X", CPU.isSet(CpuFlag.X));
        assertFalse("Check N", CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", CPU.isSet(CpuFlag.V));
        assertFalse("Check C", CPU.isSet(CpuFlag.C));
    }

    public void testNoException() {
        setInstructionAtPC(0x4181);    //chk d1,d0
        CPU.setDataRegister(0, 0x4321);
        CPU.setDataRegister(1, 0x5678);

        CPU.setCCR((byte) 0);

        int time = CPU.execute();

        assertFalse("Check CPU not in supervisor mode", CPU.isSupervisorMode());
        assertFalse("Check X", CPU.isSet(CpuFlag.X));
        assertFalse("Check N", CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", CPU.isSet(CpuFlag.V));
        assertFalse("Check C", CPU.isSet(CpuFlag.C));
    }
}
