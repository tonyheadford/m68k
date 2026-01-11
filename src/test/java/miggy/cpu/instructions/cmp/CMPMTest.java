package miggy.cpu.instructions.cmp;

import m68k.cpu.Size;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;

// $Revision: 21 $
public class CMPMTest extends BasicSetup {
    public CMPMTest(String test) {
        super(test);
    }

    public void testByte() {
        setInstruction(0xb308);    //cmpm.b (a0)+, (a1)+
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);
        SystemModel.CPU.setAddrRegister(1, 40);
        SystemModel.MEM.poke(40, 0x87654321, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check A0", 33, SystemModel.CPU.getAddrRegister(0));
        assertEquals("Check A1", 41, SystemModel.CPU.getAddrRegister(1));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertTrue("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testWord() {
        setInstruction(0xb348);    //cmpm.w (a0)+,(a1)+
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);
        SystemModel.CPU.setAddrRegister(1, 40);
        SystemModel.MEM.poke(40, 0x12345678, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check A0", 34, SystemModel.CPU.getAddrRegister(0));
        assertEquals("Check A1", 42, SystemModel.CPU.getAddrRegister(1));
        assertTrue("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertTrue("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertTrue("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }

    public void testLong() {
        setInstruction(0xb388);    //cmpm.l (a0)+,(a1)+
        SystemModel.CPU.setAddrRegister(0, 32);
        SystemModel.MEM.poke(32, 0x87654321, Size.Long);
        SystemModel.CPU.setAddrRegister(1, 40);
        SystemModel.MEM.poke(40, 0xcc00cc00, Size.Long);

        SystemModel.CPU.setCCR((byte) 0);

        int time = SystemModel.CPU.execute();

        assertEquals("Check A0", 36, SystemModel.CPU.getAddrRegister(0));
        assertEquals("Check A1", 44, SystemModel.CPU.getAddrRegister(1));
        assertFalse("Check V", SystemModel.CPU.isSet(CpuFlag.V));
        assertFalse("Check N", SystemModel.CPU.isSet(CpuFlag.N));
        assertFalse("Check Z", SystemModel.CPU.isSet(CpuFlag.Z));
        assertFalse("Check C", SystemModel.CPU.isSet(CpuFlag.C));
        assertFalse("Check X", SystemModel.CPU.isSet(CpuFlag.X));
    }
}
