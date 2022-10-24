package miggy.cpu.instructions;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertTrue;

// $Revision: 21 $
public class RTRTest extends BasicSetup {

    @Test public void testReturn() {
        setInstructionAtPC(0x4e77);    //rtr

        SystemModel.CPU.setCCR((byte) 0);
        SystemModel.CPU.push(codebase + 100, Size.Long);
        SystemModel.CPU.push(0x341f, Size.Word);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC restored", codebase + 100, SystemModel.CPU.getPC());
        assertEquals("Check CCR restored, SR unaffected", 0x001f, SystemModel.CPU.getSR());
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
