package m68k.cpu.instructions;

import miggy.BasicSetup;
import org.junit.jupiter.api.Assertions;

import static m68k.util.TestCpuUtil.*;
import static miggy.SystemModel.CPU;
import static miggy.SystemModel.CpuFlag;

/**
 * ${FILE}
 * <p>
 * Check Flags for shift/rotate instructions when shift value = 0
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class BitShiftTest extends BasicSetup {

    private int destReg = 0;
    private int srcReg = 1;
    private int shiftOrRotateValue = 0;

    /**
     * Shift count of zero:
     * <p>
     * X - unaffected
     * V - cleared
     * C - cleared
     */
    public void testLsl() {
        int opcode = 0xE328; //lsl.b d1,d0
        int d0 = 0x07654321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0);
    }

    public void testLsr() {
        int opcode = 0xE228; //lsr.b d1,d0
        int d0 = 0x07654321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0);
    }

    /**
     * Shift count of zero:
     * <p>
     * X - unaffected
     * V - cleared
     * C - cleared
     */
    public void testAsl() {
        int opcode = 0xE320; //asl.b d1,d0
        int d0 = 0x4321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0);
    }

    public void testAsr() {
        int opcode = 0xE220; //asr.b d1,d0
        int d0 = 0x4321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0);
    }

    /**
     * Shift count of zero:
     * <p>
     * X - unaffected
     * V - cleared
     * C - cleared
     */
    public void testRol() {
        int opcode = 0xE338; //rol.b d1,d0
        int d0 = 0x4321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0);
    }

    public void testRor() {
        int opcode = 0xE238; //ror.b d1,d0
        int d0 = 0x4321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0);
    }

    /**
     * Shift count of zero:
     * <p>
     * X - unaffected
     * V - cleared
     * C - set to the value of the extend bit
     */
    public void testRoxl() {
        int opcode = 0xE330; //roxl.b d1,d0
        int d0 = 0x4321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0, true);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0, true);
    }

    public void testRoxr() {
        int opcode = 0xE230; //roxr.b d1,d0
        int d0 = 0x4321;
        testInstInternal(opcode, CpuFlag.C | CpuFlag.X | CpuFlag.V, d0, true);
        testInstInternal(opcode, CpuFlag.C | CpuFlag.V, d0, true);
    }

    private void testInstInternal(int opcode, int flagState, int d0) {
        testInstInternal(opcode, flagState, d0, false);
    }

    private void testInstInternal(int opcode, int flagState, int d0, boolean isRox) {
        testOpcodeInternal(opcode, flagState, d0, isRox); //byte
        testOpcodeInternal(opcode + 0x40, flagState, d0, isRox); //word
        testOpcodeInternal(opcode + 0x80, flagState, d0, isRox); //long
    }

    private void testOpcodeInternal(int opcode, int flagState, int d0, boolean isRox) {
        setUp();
        setInstructionAtPC(opcode);
        CPU.setPC(codebase);
        CPU.setDataRegister(destReg, d0);
        CPU.setDataRegister(srcReg, shiftOrRotateValue);
        CPU.setCCR(0);
        CPU.setFlags(flagState);

        CPU.execute();
        assertEquals("Check result", d0, CPU.getDataRegister(destReg));
        assertFlagStates(flagState, isRox);
    }

    private static void assertFlagStates(int beforeState, boolean isRox) {
        switch (beforeState) {
            case CpuFlag.C | CpuFlag.X | CpuFlag.V:
                assertTrue("Check X", CPU.isSet(CpuFlag.X));
                break;
            case CpuFlag.C | CpuFlag.V:
                assertFalse("Check X", CPU.isSet(CpuFlag.X));
                break;
            default:
                Assertions.fail("Unknown flag combination: " + beforeState);
                break;
        }
        assertFalse("Check V", CPU.isSet(CpuFlag.V));
        if (isRox) {
            Assertions.assertEquals(CPU.isSet(CpuFlag.X),
                    CPU.isSet(CpuFlag.C), "Check C");
        } else {
            assertFalse("Check C", CPU.isSet(CpuFlag.C));
        }
        assertFalse("Check Z", CPU.isSet(CpuFlag.Z));
        assertFalse("Check N", CPU.isSet(CpuFlag.N));
    }


}
