package miggy.cpu.instructions.bitshift;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class LSRTest extends BasicSetup {

    @Test public void testByte() {
        setInstructionAtPC(0xe228);    //lsr.b d1,d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setDataRegister(1, 4);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x87654302, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testWord() {
        setInstructionAtPC(0xe048);    //lsr.w #8,d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x87650043, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testLong() {
        setInstructionAtPC(0xe2a8);    //asr.l d1,d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setDataRegister(1, 18);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x21d9, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testMem() {
        setInstructionAtPC(0xe2d0);    //lsr (a0)
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x43b24321, SystemModel.MEM.peek(32, Size.Long));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}