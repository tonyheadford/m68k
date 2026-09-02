package miggy.cpu.instructions;

import m68k.cpu.DisassembledInstruction;
import m68k.cpu.Instruction;
import m68k.cpu.InstructionHandler;
import m68k.cpu.TestRegistry;
import m68k.cpu.instructions.CHK;
import m68k.cpu.rules.AddressingMode.DataRegisterDirect;
import miggy.BasicSetup;
import miggy.SystemModel;
import miggy.SystemModel.CpuFlag;
import org.junit.jupiter.api.Test;

import static m68k.cpu.rules.AddressingMode.dataModes;
import static org.junit.jupiter.api.Assertions.*;

// $Revision: 21 $
class CHKTest extends BasicSetup {
    @Test
    void testNeg() {
        setInstruction(0x4181);    //chk d1,d0
        SystemModel.CPU.setDataRegister(0, 0xc321);
        SystemModel.CPU.setDataRegister(1, 0x5678);

        SystemModel.CPU.setCCR((byte) 0);

        SystemModel.CPU.execute();

        assertTrue(SystemModel.CPU.isSupervisorMode(), "Check CPU in supervisor mode");
        //vector number stored in vector addr for testing
        assertEquals(6, SystemModel.CPU.getPC(), "Check PC");

        assertFalse(SystemModel.CPU.isSet(CpuFlag.X), "Check X");
        assertTrue(SystemModel.CPU.isSet(CpuFlag.N), "Check N");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.Z), "Check Z");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.V), "Check V");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.C), "Check C");
    }

    @Test
    void testGreater() {
        setInstruction(0x4181);    //chk d1,d0
        SystemModel.CPU.setDataRegister(0, 0x6321);
        SystemModel.CPU.setDataRegister(1, 0x5678);

        SystemModel.CPU.setCCR((byte) 0);

        SystemModel.CPU.execute();

        assertTrue(SystemModel.CPU.isSupervisorMode(), "Check CPU in supervisor mode");
        //vector number stored in vector addr for testing
        assertEquals(6, SystemModel.CPU.getPC(), "Check PC");

        assertFalse(SystemModel.CPU.isSet(CpuFlag.X), "Check X");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.N), "Check N");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.Z), "Check Z");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.V), "Check V");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.C), "Check C");
    }

    @Test
    void testNoException() {
        setInstruction(0x4181);    //chk d1,d0
        SystemModel.CPU.setDataRegister(0, 0x4321);
        SystemModel.CPU.setDataRegister(1, 0x5678);

        SystemModel.CPU.setCCR((byte) 0);

        SystemModel.CPU.execute();

        assertFalse(SystemModel.CPU.isSupervisorMode(), "Check CPU not in supervisor mode");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.X), "Check X");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.N), "Check N");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.Z), "Check Z");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.V), "Check V");
        assertFalse(SystemModel.CPU.isSet(CpuFlag.C), "Check C");
    }

    @Test
    void disassemble_returnsCorrectDisassembledInstruction() {
        int opcode = 0x4181;
        setInstruction(opcode); // chk d1,d0
        Instruction instruction = SystemModel.CPU.getInstructionAt(codebase);

        DisassembledInstruction result = instruction.disassemble(codebase, opcode);

        assertEquals("chk", result.instruction);
        assertEquals("d1", result.op1.operand);
        assertEquals("d0", result.op2.operand);
    }

    @Test
    void register_onCommonInstance_registersCorrectNumberOfVariants() {
        TestRegistry registry = new TestRegistry();
        InstructionHandler instance = new CHK(SystemModel.CPU);
        int sourceModes = DataRegisterDirect.all().size();
        int destinationModes = dataModes().size();
        int variants = sourceModes * destinationModes;

        instance.register(registry);

        assertEquals(variants, registry.size());
    }
}
