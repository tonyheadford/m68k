package miggy.cpu.instructions.move;

import m68k.cpu.Cpu;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class MOVE_2_CCRTest extends BasicSetup {
    public MOVE_2_CCRTest(String test) {
        super(test);
    }

    public void testMove() {
        setInstruction(0x44c0);    //move d0,ccr
        SystemModel.CPU.setDataRegister(0, 0x0015);

        SystemModel.CPU.setSR(0xff00);

        int time = SystemModel.CPU.execute();

        assertEquals("Check SR", 0xff15 & Cpu.SR_MASK, SystemModel.CPU.getSR());
        assertTrue("Check X", SystemModel.CPU.isSet(CpuFlag.X));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
    }
}