package miggy.cpu.instructions.btst;

import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class BTSTTest extends BasicSetup {
    public BTSTTest(String test) {
        super(test);
    }

    public void testDyn() {
        setInstructionAtPC(0x0300);    //btst d1,d0
        SystemModel.CPU.setDataRegister(0, 0x0010);
        SystemModel.CPU.setDataRegister(1, 4);

        SystemModel.CPU.setCCR((byte) 4);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0x0010, SystemModel.CPU.getDataRegister(0));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testStatic() {
        setInstructionParamW(0x0800, 0x001f);    //btst #$1f,d0
        SystemModel.CPU.setDataRegister(0, 0);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check result", 0, SystemModel.CPU.getDataRegister(0));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testDyn2() {
        int val = 0x73;
        setInstructionParamW(0x013c, val);    //btst    D0, #$73
        for (int i = 0; i < 8; i++) {
            SystemModel.CPU.setPC(codebase);
            SystemModel.CPU.setDataRegister(0, i);
            SystemModel.CPU.setCCR((byte) 0);
            SystemModel.CPU.execute();
            assertEquals("Check result", i, SystemModel.CPU.getDataRegister(0));
            boolean zFlag = (val & (1 << i)) == 0;
            checkFlags(i, zFlag);
        }
    }

    private void checkFlags(int bit, boolean isZero) {
        assertEquals(bit + ", Check Z", SystemModel.CPU.isSet(CpuFlag.Z), isZero);
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}

