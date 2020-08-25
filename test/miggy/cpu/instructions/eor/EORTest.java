package miggy.cpu.instructions.eor;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class EORTest extends BasicSetup {
    public EORTest(String test) {
        super(test);
    }

    public void testByte() {
        setInstruction(0xb300);    //eor.b d1, d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setDataRegister(1, 0xc7c7c7c7);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x876543e6, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testWord() {
        setInstruction(0xb340);    //eor.w d1, d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setDataRegister(1, 0xc0c0c0c0);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x876583e1, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testLong() {
        setInstruction(0xb380);    //eor.l d1, d0
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setDataRegister(1, 0xc0c0c0c0);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x47a583e1, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testMem() {
        setInstruction(0xb150);    //eor d0,(a0)
        SystemModel.CPU.setDataRegister(0, 0xc0c0c0c0);
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x47a54321, SystemModel.MEM.peek(32, Size.Long));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}

