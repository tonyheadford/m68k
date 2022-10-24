package miggy.cpu.instructions.dbcc;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class DBxxTest extends BasicSetup {

    @Test public void testDBEQ_Branch() {
        SystemModel.MEM.poke(0, 0x7003, Size.Word);    //moveq #3, d0
        SystemModel.MEM.poke(2, 0x7200, Size.Word);    //moveq #0, d1
        SystemModel.MEM.poke(4, 0x5281, Size.Word);    //addq.l #1,d1 - loop
        SystemModel.MEM.poke(6, 0x0c01, Size.Word);    //cmpi.b #3,d1
        SystemModel.MEM.poke(8, 0x0003, Size.Word);    //
        SystemModel.MEM.poke(10, 0x57c8, Size.Word);//dbeq d0,loop
        SystemModel.MEM.poke(12, 0xfff8, Size.Word);    //
        SystemModel.MEM.poke(14, 0x7000, Size.Word);    //moveq #0, d0

        SystemModel.CPU.setDataRegister(0, 3);
        SystemModel.CPU.setDataRegister(1, 1);
        SystemModel.CPU.setPC(10);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", 4, SystemModel.CPU.getPC());
        assertEquals("Check D0", 2, SystemModel.CPU.getDataRegister(0));
        assertEquals("Check D1", 1, SystemModel.CPU.getDataRegister(1));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testDBEQ_Exit_Condition() {
        SystemModel.MEM.poke(0, 0x7003, Size.Word);    //moveq #3, d0
        SystemModel.MEM.poke(2, 0x7200, Size.Word);    //moveq #0, d1
        SystemModel.MEM.poke(4, 0x5281, Size.Word);    //addq.l #1,d1 - loop
        SystemModel.MEM.poke(6, 0x0c01, Size.Word);    //cmpi.b #3,d1
        SystemModel.MEM.poke(8, 0x0003, Size.Word);    //
        SystemModel.MEM.poke(10, 0x57c8, Size.Word);//dbeq d0,loop
        SystemModel.MEM.poke(12, 0xfff8, Size.Word);    //
        SystemModel.MEM.poke(14, 0x7000, Size.Word);    //moveq #0, d0

        SystemModel.CPU.setDataRegister(0, 3);
        SystemModel.CPU.setDataRegister(1, 1);
        SystemModel.CPU.setPC(10);
        SystemModel.CPU.setCCR((byte) 4);    //Z set

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", 14, SystemModel.CPU.getPC());
        assertEquals("Check D0", 3, SystemModel.CPU.getDataRegister(0));
        assertEquals("Check D1", 1, SystemModel.CPU.getDataRegister(1));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testDBEQ_Exit_Counter() {
        SystemModel.MEM.poke(0, 0x7003, Size.Word);    //moveq #3, d0
        SystemModel.MEM.poke(2, (short) 0x7200, Size.Word);    //moveq #0, d1
        SystemModel.MEM.poke(4, (short) 0x5281, Size.Word);    //addq.l #1,d1 - loop
        SystemModel.MEM.poke(6, (short) 0x0c01, Size.Word);    //cmpi.b #3,d1
        SystemModel.MEM.poke(8, (short) 0x0003, Size.Word);    //
        SystemModel.MEM.poke(10, (short) 0x57c8, Size.Word);//dbeq d0,loop
        SystemModel.MEM.poke(12, (short) 0xfff8, Size.Word);    //
        SystemModel.MEM.poke(14, (short) 0x7000, Size.Word);    //moveq #0, d0

        SystemModel.CPU.setDataRegister(0, 0);
        SystemModel.CPU.setDataRegister(1, 1);
        SystemModel.CPU.setPC(10);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", 14, SystemModel.CPU.getPC());
        assertEquals("Check D0", 0x0000ffff, SystemModel.CPU.getDataRegister(0));
        assertEquals("Check D1", 1, SystemModel.CPU.getDataRegister(1));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}