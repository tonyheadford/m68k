package miggy.cpu.instructions.move;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertFalse;

// $Revision: 21 $
public class MOVEMTest extends BasicSetup {

    @Test public void testR2MWord() {
        setInstructionParamW(0x48a7, 0xe090);    //movem.w	d0-d2/a0/a3,-(a7)
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xaaaaaaaa);
        SystemModel.CPU.setDataRegister(2, 0xc0c0c0c0);
        SystemModel.CPU.setAddrRegister(0, 0x87654321);
        SystemModel.CPU.setAddrRegister(3, 0x55555555);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        int stack = SystemModel.CPU.getAddrRegister(7);
        assertEquals("Check for d0", (short) 0x5678, (short) SystemModel.MEM.peek(stack, Size.Word));
        assertEquals("Check for d1", (short) 0xaaaa, (short) SystemModel.MEM.peek(stack + 2, Size.Word));
        assertEquals("Check for d2", (short) 0xc0c0, (short) SystemModel.MEM.peek(stack + 4, Size.Word));
        assertEquals("Check for a0", (short) 0x4321, (short) SystemModel.MEM.peek(stack + 6, Size.Word));
        assertEquals("Check for a3", (short) 0x5555, (short) SystemModel.MEM.peek(stack + 8, Size.Word));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testR2MLong() {
        setInstructionParamW(0x48d1, 0x0907);    //movem.l	d0-d2/a0/a3,(a1)
        SystemModel.CPU.setDataRegister(0, 0x12345678);
        SystemModel.CPU.setDataRegister(1, 0xaaaaaaaa);
        SystemModel.CPU.setDataRegister(2, 0xc0c0c0c0);
        SystemModel.CPU.setAddrRegister(0, 0x87654321);
        SystemModel.CPU.setAddrRegister(3, 0x55555555);
        SystemModel.CPU.setAddrRegister(1, codebase + 100);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        int base = SystemModel.CPU.getAddrRegister(1);
        assertEquals("Check A0 hasn't changed", codebase + 100, base);
        assertEquals("Check for d0", 0x12345678, SystemModel.MEM.peek(base, Size.Long));
        assertEquals("Check for d1", 0xaaaaaaaa, SystemModel.MEM.peek(base + 4, Size.Long));
        assertEquals("Check for d2", 0xc0c0c0c0, SystemModel.MEM.peek(base + 8, Size.Long));
        assertEquals("Check for a0", 0x87654321, SystemModel.MEM.peek(base + 12, Size.Long));
        assertEquals("Check for a3", 0x55555555, SystemModel.MEM.peek(base + 16, Size.Long));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testM2RWord() {
        setInstructionParamW(0x4c9f, 0x0907);    //movem.w	(a7)+,d0-d2/a0/a3

        SystemModel.CPU.push(0x5555, Size.Word);
        SystemModel.CPU.push(0x4321, Size.Word);
        SystemModel.CPU.push(0xc0c0, Size.Word);
        SystemModel.CPU.push(0xaaaa, Size.Word);
        SystemModel.CPU.push(0x5678, Size.Word);

        SystemModel.CPU.setDataRegister(0, 0x12340c0c);
        SystemModel.CPU.setDataRegister(1, 0xaaaa0c0c);
        SystemModel.CPU.setDataRegister(2, 0xc0c00c0c);
        SystemModel.CPU.setAddrRegister(0, 0x87650c0c);
        SystemModel.CPU.setAddrRegister(3, 0x55550c0c);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check for d0", 0x00005678, SystemModel.CPU.getDataRegister(0));
        assertEquals("Check for d1", 0xffffaaaa, SystemModel.CPU.getDataRegister(1));
        assertEquals("Check for d2", 0xffffc0c0, SystemModel.CPU.getDataRegister(2));
        assertEquals("Check for a0", 0x00004321, SystemModel.CPU.getAddrRegister(0));
        assertEquals("Check for a3", 0x00005555, SystemModel.CPU.getAddrRegister(3));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testM2RLong() {
        setInstructionParamW(0x4cd1, 0x0907);    //movem.l	(a1),d0-d2/a0/a3
        int base = codebase + 100;
        SystemModel.CPU.setAddrRegister(1, base);
        SystemModel.MEM.poke(base, 0x12345678, Size.Long);
        SystemModel.MEM.poke(base + 4, 0xaaaaaaaa, Size.Long);
        SystemModel.MEM.poke(base + 8, 0xc0c0c0c0, Size.Long);
        SystemModel.MEM.poke(base + 12, 0x87654321, Size.Long);
        SystemModel.MEM.poke(base + 16, 0x55555555, Size.Long);


        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check for d0", 0x12345678, SystemModel.CPU.getDataRegister(0));
        assertEquals("Check for d1", 0xaaaaaaaa, SystemModel.CPU.getDataRegister(1));
        assertEquals("Check for d2", 0xc0c0c0c0, SystemModel.CPU.getDataRegister(2));
        assertEquals("Check for a0", 0x87654321, SystemModel.CPU.getAddrRegister(0));
        assertEquals("Check for a3", 0x55555555, SystemModel.CPU.getAddrRegister(3));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
