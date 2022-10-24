package miggy.cpu.instructions.move;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class MOVE_F_SRTest extends BasicSetup {

    @Test public void testMove() {
        setInstructionAtPC(0x40c0);    //move sr,d0

        SystemModel.CPU.setSR((short) 0x000f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check D0", (short) 0x000f, SystemModel.CPU.getSR());
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
