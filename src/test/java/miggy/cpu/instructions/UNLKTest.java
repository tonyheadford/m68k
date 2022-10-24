package miggy.cpu.instructions;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertFalse;

// $Revision: 21 $
public class UNLKTest extends BasicSetup {

    @Test public void testInstruction() {
        setInstructionAtPC(0x4e5e);    //unlk a6
        int stack = SystemModel.CPU.getAddrRegister(7);
        SystemModel.CPU.push(0x87654321, Size.Long);
        SystemModel.CPU.setAddrRegister(6, stack - 4);

        SystemModel.CPU.setCCR((byte) 0);
        int time = SystemModel.CPU.execute();

        assertEquals("Check stack", stack, SystemModel.CPU.getAddrRegister(7));
        assertEquals("Check A6", 0x87654321, SystemModel.CPU.getAddrRegister(6));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
