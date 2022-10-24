package miggy.cpu.instructions.add;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class ADDXTest extends BasicSetup {

    @Test
    public void testByte() {
        setInstructionAtPC(0xd101);    //addx.b d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0x78);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123456F1, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testWord() {
        setInstructionAtPC(0xd141);    //addx.w d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xaa78);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x123400F1, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testLong() {
        setInstructionAtPC(0xd181);    //addx.l d1, d0
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xf8765432);
        SystemModel.CPU.setCCR((byte) 0x1f);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0aaaaaab, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testBytePreDec() {
        int addr = 0x800;
        int a2Exp = addr - 2;
        SystemModel.MEM.poke(a2Exp, 0x12345678, Size.Long);
        setInstructionAtPC(0xd50a);   //addx.b  -(A2), -(A2)
        SystemModel.CPU.setAddrRegister(2, addr);
        SystemModel.CPU.setCCR((byte) 0x1f);

        SystemModel.CPU.execute();

        assertEquals("Check result", a2Exp, SystemModel.CPU.getAddrRegisterLong(2));
        assertEquals(0x12 + 0x34 + 1, SystemModel.MEM.peek(a2Exp, Size.Byte));
    }

    @Test public void testWordPreDec() {
        int addr = 0x800;
        int anExp = addr - 4;
        SystemModel.MEM.poke(anExp, 0x12345678, Size.Long);
        setInstructionAtPC(0xdb4d);   //addx.w  -(A5), -(A5)
        SystemModel.CPU.setAddrRegister(5, addr);
        SystemModel.CPU.setCCR((byte) 0x1f);

        SystemModel.CPU.execute();

        assertEquals("Check result", anExp, SystemModel.CPU.getAddrRegisterLong(5));
        assertEquals(0x1234 + 0x5678 + 1, SystemModel.MEM.peek(anExp, Size.Word));
    }

    @Test public void testLongPreDec() {
        int addr = 0x800;
        int anExp = addr - 8;
        SystemModel.MEM.poke(anExp, 0x12345678, Size.Long);
        SystemModel.MEM.poke(anExp + 4, 0x87654321, Size.Long);
        setInstructionAtPC(0xdd8e);   //addx.l  -(A6), -(A6)
        SystemModel.CPU.setAddrRegister(6, addr);
        SystemModel.CPU.setCCR((byte) 0x1f);

        SystemModel.CPU.execute();

        assertEquals("Check result", anExp, SystemModel.CPU.getAddrRegisterLong(6));
        assertEquals(0x87654321 + 0x12345678 + 1, SystemModel.MEM.peek(anExp, Size.Long)); // 0x9999999A
    }
}
