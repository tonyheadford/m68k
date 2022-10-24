package miggy.cpu.instructions;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class DIVSTest extends BasicSetup {

    @Test public void testPos() {
        setInstructionAtPC(0x81c1);    //divs d1,d0

        SystemModel.CPU.setDataRegister(0, 0x8765);
        SystemModel.CPU.setDataRegister(1, 0x0003);
        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x00022d21, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testNeg() {
        setInstructionAtPC(0x81c1);    //divs d1,d0

        SystemModel.CPU.setDataRegister(0, 0xffff8765);
        SystemModel.CPU.setDataRegister(1, 0x0033);
        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0xffecfda3, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testDivByZero() {
        setInstructionAtPC(0x81c1);    //divs d1,d0

        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0);
        int time = SystemModel.CPU.execute();

        assertTrue("Check CPU in supervisor mode", SystemModel.CPU.isSupervisorMode());
        //vector number stored in vector addr for testing
        assertEquals("Check PC", 5, SystemModel.CPU.getPC());
        //unaffected
        assertEquals("Check unaffected destination", 0x12345678, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
