package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class STOPTest extends BasicSetup {

    @Test public void testReturn() {
        setInstructionParamW(0x4e72, 0x201f);    //stop

        SystemModel.CPU.setCCR((byte) 0);
        SystemModel.CPU.setSupervisorMode(true);

        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        assertEquals("Check SR set", 0x201f, SystemModel.CPU.getSR());
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testPrivViolation1() {
        setInstructionParamW(0x4e72, 0x201f);    //stop

        SystemModel.CPU.setCCR((byte) 0);
        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 8, SystemModel.CPU.getPC());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testPrivViolation2() {
        setInstructionParamW(0x4e72, 0x001f);    //stop

        SystemModel.CPU.setCCR((byte) 0);
        SystemModel.CPU.setSupervisorMode(true);
        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 8, SystemModel.CPU.getPC());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
