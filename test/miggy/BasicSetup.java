package miggy;

import junit.framework.TestCase;
import m68k.cpu.Size;
import miggy.memory.TestMem;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

// $Revision: 21 $
@Ignore
public class BasicSetup extends TestCase {
    protected int codebase;

    public BasicSetup(String test) {
        super(test);
    }

    @Before
    protected void setUp() {
        SystemModel.MEM = TestMem.create(2048);
        SystemModel.CPU = new TestCpu(SystemModel.MEM);

        //test vectors just containing the vector number
        for (int v = 0; v < 256; v++) {
            int addr = v << 2;
            //test: changed to poke
            SystemModel.MEM.poke(addr, v, Size.Long);
        }

        SystemModel.CPU.setAddrRegisterWord(7, 2048);    //set up the stack
        SystemModel.CPU.setUSP(0x0800);
        SystemModel.CPU.setSSP(0x0780);
        codebase = 0x0400;
        SystemModel.CPU.setPC(codebase);
    }

    protected void setInstructionAtPC(int opcode) {
        SystemModel.MEM.poke(codebase, opcode, Size.Word);
        SystemModel.CPU.setPC(codebase);
    }

    protected void setInstructionParamW(int opcode, int param) {
        SystemModel.MEM.poke(codebase, opcode, Size.Word);
        SystemModel.MEM.poke(codebase + 2, param, Size.Word);
        SystemModel.CPU.setPC(codebase);
    }

    protected void setInstructionParamL(int opcode, int param) {
        SystemModel.MEM.poke(codebase, opcode, Size.Word);
        SystemModel.MEM.poke(codebase + 2, param, Size.Long);
        SystemModel.CPU.setPC(codebase);
    }

    @After
    protected void tearDown() {
        SystemModel.CPU = null;
        SystemModel.MEM = null;
    }
}
