package miggy.cpu.instructions.add;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertTrue;

// $Revision: 21 $
public class ADDATest extends BasicSetup {

    @Test public void testWord() {
        //test: changed to poke
        setInstructionAtPC(0xd0c0);    //adda.w d0, a0
        SystemModel.CPU.setDataRegister(0, 0xc234);
        SystemModel.CPU.setAddrRegister(0, 0x56785678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x567818ac, SystemModel.CPU.getAddrRegister(0));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testLong() {
        setInstructionAtPC(0xd1c0);    //adda.l d0, a0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setAddrRegister(0, 0x56785678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x68acacf0, SystemModel.CPU.getAddrRegister(0));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}
