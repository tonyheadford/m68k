package miggy.cpu.instructions.sub;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class SUBXTest extends BasicSetup {
    public SUBXTest(String test) {
        super(test);
    }

    public void testByte() {
        setInstruction(0x9101);    //subx.b d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0x78);

        SystemModel.CPU.setCCR((byte) 0x1f);    //all set

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123456ff, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testWord() {
        setInstruction(0x9141);    //subx.w d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xaa78);

        SystemModel.CPU.setCCR((byte) 0);    //all clr

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x1234ac00, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testLong() {
        setInstruction(0x9181);    //subx.l d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0x87654321);

        SystemModel.CPU.setCCR((byte) 0x1f);    //all set

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x8acf1356, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    public void testBytePreDec() {
        int addr = 0x800;
        int a2Exp = addr - 2;
        SystemModel.MEM.poke(a2Exp, 0x12345678, Size.Long);
        setInstruction(0x950a);   //subx.b  -(A2), -(A2)
        SystemModel.CPU.setAddrRegister(2, addr);
        SystemModel.CPU.setCCR((byte) 0x1f);

        SystemModel.CPU.execute();

        assertEquals("Check result", a2Exp, SystemModel.CPU.getAddrRegisterLong(2));
        assertEquals((0x12 - 0x34 - 1) & 0xFF, SystemModel.MEM.peek(a2Exp, Size.Byte));
    }

    public void testWordPreDec() {
        int addr = 0x800;
        int anExp = addr - 4;
        SystemModel.MEM.poke(anExp, 0x12345678, Size.Long);
        setInstruction(0x9b4d);   //subx.w  -(A5), -(A5)
        SystemModel.CPU.setAddrRegister(5, addr);
        SystemModel.CPU.setCCR((byte) 0x1f);

        SystemModel.CPU.execute();

        assertEquals("Check result", anExp, SystemModel.CPU.getAddrRegisterLong(5));
        assertEquals((0x1234 - 0x5678 - 1) & 0xFFFF, SystemModel.MEM.peek(anExp, Size.Word));
    }

    public void testLongPreDec() {
        int addr = 0x800;
        int anExp = addr - 8;
        SystemModel.MEM.poke(anExp, 0x12345678, Size.Long);
        SystemModel.MEM.poke(anExp + 4, 0x87654321, Size.Long);
        setInstruction(0x9d8e);   //subx.l  -(A6), -(A6)
        SystemModel.CPU.setAddrRegister(6, addr);
        SystemModel.CPU.setCCR((byte) 0x1f);

        SystemModel.CPU.execute();

        assertEquals("Check result", anExp, SystemModel.CPU.getAddrRegisterLong(6));
        assertEquals((0x12345678 - 0x87654321 - 1) & 0xFFFF_FFFF, SystemModel.MEM.peek(anExp, Size.Long));
    }
}
