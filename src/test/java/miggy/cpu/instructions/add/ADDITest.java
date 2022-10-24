package miggy.cpu.instructions.add;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class ADDITest extends BasicSetup {
    @Test
    public void testByte() {
        setInstructionParamW(0x0600, 0x0078);    //addi.b #$78, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123456F0, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testWord() {
        setInstructionParamW(0x0640, 0xaa78);    //addi.w #$aa78, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123400F0, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testLong() {
        //test: changed to poke
        setInstructionParamL(0x0680, 0xf8765432);    //addi.l #$f8765432, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0aaaaaaa, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}
