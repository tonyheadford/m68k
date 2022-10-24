package miggy.cpu.instructions.bcc;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertFalse;

// $Revision: 21 $
public class BxxTest extends BasicSetup {

    @Test public void testBCC_Branch() {
        SystemModel.MEM.poke(codebase + 6, 0x64f8, Size.Word);    //bcc begin
        SystemModel.CPU.setPC(codebase + 6);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase, SystemModel.CPU.getPC());
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testBCC_Branch_w() {
        SystemModel.MEM.poke(codebase + 6, 0x6400, Size.Word);    //bcc.w begin
        SystemModel.MEM.poke(codebase + 8, 0xfff8, Size.Word);    //bcc.w begin

        SystemModel.CPU.setPC(codebase + 6);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase, SystemModel.CPU.getPC());
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testBCS_NoBranch() {
        SystemModel.MEM.poke(codebase + 6, 0x65f8, Size.Word);    //bcs begin

        SystemModel.CPU.setPC(codebase + 6);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase + 8, SystemModel.CPU.getPC());
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testBCS_NoBranch_w() {
        SystemModel.MEM.poke(codebase + 6, 0x6500, Size.Word);    //bcs.w begin
        SystemModel.MEM.poke(codebase + 8, 0xfff8, Size.Word);

        SystemModel.CPU.setPC(codebase + 6);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase + 10, SystemModel.CPU.getPC());
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testBSR() {
        SystemModel.MEM.poke(codebase + 6, 0x6100, Size.Word);    //bsr
        SystemModel.MEM.poke(codebase + 8, 0x0004, Size.Word);

        SystemModel.CPU.setPC(codebase + 6);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase + 0x0c, SystemModel.CPU.getPC());
        assertEquals("Check A7", 0x7fc, SystemModel.CPU.getAddrRegister(7));
        assertEquals("Check Stack Top", codebase + 0x0a, SystemModel.MEM.peek(0x7fc, Size.Long));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    @Test public void testBRA() {
        SystemModel.MEM.poke(codebase + 6, 0x6000, Size.Word);    //bra
        SystemModel.MEM.poke(codebase + 8, 0x0004, Size.Word);
        SystemModel.CPU.setPC(codebase + 6);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check PC", codebase + 0x0c, SystemModel.CPU.getPC());
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

}