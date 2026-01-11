package miggy.cpu.instructions;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class RTETest extends BasicSetup {
    public RTETest(String test) {
        super(test);
    }

    public void testReturn() {
        setInstruction(0x4e73);    //rte

        SystemModel.CPU.setCCR((byte) 0);
        SystemModel.CPU.setSupervisorMode(true);
        SystemModel.CPU.push(codebase + 100, Size.Long);
        SystemModel.CPU.push(0x001f, Size.Word);

        int time = SystemModel.CPU.execute();

        assertFalse("Check CPU not in supervisor mode", SystemModel.CPU.isSupervisorMode());
        assertEquals("Check PC restored", codebase + 100, SystemModel.CPU.getPC());
        assertEquals("Check SR restored", 0x001f, SystemModel.CPU.getSR());
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testPrivViolation() {
        setInstruction(0x4e73);    //rte

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
}
