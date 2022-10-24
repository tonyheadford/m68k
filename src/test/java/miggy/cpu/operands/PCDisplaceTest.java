package miggy.cpu.operands;

import m68k.cpu.Size;
import miggy.BasicSetup;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertFalse;
import static miggy.SystemModel.*;

// $Revision: 21 $
public class PCDisplaceTest extends BasicSetup {

    @Test public void testInstruction() {
        MEM.poke(codebase + 0x00e4, 0x41faff1c, Size.Long);    //lea $-e4(pc),a0
        MEM.poke(codebase, 0x00c00000, Size.Long);
        CPU.setAddrRegister(0, 0x87654321);
        CPU.setPC(codebase + 0x00e4);
        CPU.setCCR((byte) 0);

        int time = CPU.execute();

        assertEquals("Check result", codebase + 2, CPU.getAddrRegister(0));
        assertFalse("Check X", CPU.isSet(CpuFlag.X));
        assertFalse("Check N", CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", CPU.isSet(CpuFlag.V));
        assertFalse("Check C", CPU.isSet(CpuFlag.C));
    }
}
