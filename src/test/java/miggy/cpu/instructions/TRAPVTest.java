package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class TRAPVTest extends BasicSetup {

    @Test public void testTrap() {
        setInstructionAtPC(0x4e76);    //trapv

        SystemModel.CPU.setCCR((byte) 2);    //set v
        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 7, SystemModel.CPU.getPC());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testNoTrap() {
        setInstructionAtPC(0x4e76);    //trapv

        SystemModel.CPU.setCCR((byte) 0);    //v not set
        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase + 2, SystemModel.CPU.getPC());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
