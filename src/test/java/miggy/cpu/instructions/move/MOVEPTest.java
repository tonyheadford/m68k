package miggy.cpu.instructions.move;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertEquals;
import static m68k.util.TestCpuUtil.assertFalse;

// $Revision: 21 $
public class MOVEPTest extends BasicSetup {

    @Test public void testWordToMem() {
        setInstructionParamW(0x0188, 0x0000);    //movep.w d0,(a0)
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        SystemModel.CPU.setAddrRegister(0, codebase + 100);
        SystemModel.MEM.poke(codebase + 100, 0xffffffff, Size.Long);
        SystemModel.MEM.poke(codebase + 104, 0xffffffff, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        int val = SystemModel.MEM.peek(codebase + 100, Size.Long);
        assertEquals("Check result", 0x43ff21ff, val);
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testWordToReg() {
        setInstructionParamW(0x0308, 0x0000);    //movep.w (a0),d1
        SystemModel.CPU.setAddrRegister(0, codebase + 100);
        SystemModel.CPU.setDataRegister(1, 0xc0c0c0c0);
        SystemModel.MEM.poke(codebase + 100, 0x43ff21ff, Size.Long);
        SystemModel.MEM.poke(codebase + 104, 0xffffffff, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0xc0c04321, SystemModel.CPU.getDataRegister(1));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testLongToMem() {
        setInstructionParamW(0x01c8, 0x0000);    //movep.l d0,(a0)
        SystemModel.CPU.setDataRegister(0, 0x87654321);
        //odd address
        SystemModel.CPU.setAddrRegister(0, codebase + 101);
        SystemModel.MEM.poke(codebase + 100, 0xffffffff, Size.Long);
        SystemModel.MEM.poke(codebase + 104, 0xffffffff, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        int val = SystemModel.MEM.peek(codebase + 100, Size.Long);
        assertEquals("Check result 1", 0xff87ff65, val);
        val = SystemModel.MEM.peek(codebase + 104, Size.Long);
        assertEquals("Check result 2", 0xff43ff21, val);
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testLongToReg() {
        setInstructionParamW(0x0348, 0x0000);    //movep.l (a0),d1
        //odd address
        SystemModel.CPU.setAddrRegister(0, codebase + 101);
        SystemModel.CPU.setDataRegister(1, 0xc0c0c0c0);
        SystemModel.MEM.poke(codebase + 100, 0xff87ff65, Size.Long);
        SystemModel.MEM.poke(codebase + 104, 0xff43ff21, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x87654321, SystemModel.CPU.getDataRegister(1));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}

