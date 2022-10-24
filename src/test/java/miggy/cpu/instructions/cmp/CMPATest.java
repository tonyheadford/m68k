package miggy.cpu.instructions.cmp;

import m68k.cpu.Size;
import m68k.util.TestCpuUtil;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertFalse;

// $Revision: 21 $
public class CMPATest extends BasicSetup {

    @Test public void testWord() {
        setInstructionAtPC(0xb0c9);    //cmp.w a1,a0
        SystemModel.CPU.setAddrRegister(0, 0x87654321);
        SystemModel.CPU.setAddrRegister(1, 0x87658321);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        TestCpuUtil.assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        TestCpuUtil.assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testLong() {
        setInstructionAtPC(0xb1c9);    //cmp.l a1, a0
        SystemModel.CPU.setAddrRegister(0, 0x87654321);
        SystemModel.CPU.setAddrRegister(1, 0xcc00cc00);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        TestCpuUtil.assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        TestCpuUtil.assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testMem() {
        setInstructionAtPC(0xb2d0);    //cmp (a0),d0
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.CPU.setAddrRegister(1, 0x87654321);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        TestCpuUtil.assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        TestCpuUtil.assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}

