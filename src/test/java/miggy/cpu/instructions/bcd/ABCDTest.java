package miggy.cpu.instructions.bcd;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.*;

// $Revision: 21 $
public class ABCDTest extends BasicSetup {

    @Test public void testReg() {
        setInstructionAtPC(0xc101);    //abcd d1,d0
        SystemModel.CPU.setDataRegister(0, 0x0099);
        SystemModel.CPU.setDataRegister(1, 0x0001);
        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }

    @Test public void testMem() {
        setInstructionAtPC(0xc109);    //abcd -(a1),-(a0)
        SystemModel.CPU.setAddrRegister(0, codebase + 100);
        SystemModel.CPU.setAddrRegister(1, codebase + 108);
        SystemModel.MEM.poke(codebase + 98, 0x0099, Size.Word);
        SystemModel.MEM.poke(codebase + 106, 0x001, Size.Word);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0, SystemModel.MEM.peek(codebase + 98, Size.Word));
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));

        SystemModel.CPU.setPC(codebase);
        time = SystemModel.CPU.execute();
        assertEquals("Check result 2", 0x0100, SystemModel.MEM.peek(codebase + 98, Size.Word));
        assertFalse("Check X 2", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N 2", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z 2", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V 2", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C 2", SystemModel.CPU.isSet(CpuFlag.C));
    }
}
