package miggy.cpu.instructions.bclr;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class BCLRTest extends BasicSetup {

    @Test public void testDyn() {
        setInstructionAtPC(0x0380);    //bclr d1,d0
        SystemModel.CPU.setDataRegister(0, 0);
        SystemModel.CPU.setDataRegister(1, 4);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testStatic() {
        testStaticInternal(0x1f, 14);
    }

    @Test public void testStatic2() {
        testStaticInternal(0xc, 12);
    }

    private void testStaticInternal(int val, int expTime) {
        setInstructionParamW(0x0880, val);    //bclr #$val,d0
        SystemModel.CPU.setDataRegister(0, 0x00008000);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 32768, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertEquals("Timing Error", expTime, time);
    }
}
