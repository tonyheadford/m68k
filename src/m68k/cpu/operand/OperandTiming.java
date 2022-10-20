package m68k.cpu.operand;

import m68k.cpu.Cpu;
import m68k.cpu.Size;
import m68k.cpu.operand.Operands.*;
import m68k.cpu.timing.M68kCycles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class OperandTiming {

    public static final Map<Class, Integer> operandIndexMap;

    static {
        Map<Class, Integer> m = new HashMap<>();
        m.put(DataRegisterOperand.class, 0);
        m.put(AddressRegisterOperand.class, 1);
        m.put(AddressRegisterIndirectOperand.class, 2);
        m.put(AddressRegisterPostIncOperand.class, 3);
        m.put(AddressRegisterPreDecOperand.class, 4);
        m.put(AddressRegisterWithDisplacementOperand.class, 5);
        m.put(AddressRegisterWithIndexOperand.class, 6);
        m.put(AbsoluteShortOperand.class, 7);
        m.put(AbsoluteLongOperand.class, 8);
        m.put(PCWithDisplacementOperand.class, 9);
        m.put(PCWithIndexOperand.class, 10);
        m.put(ImmediateOperand.class, 11);
        m.put(StatusRegisterOperand.class, 12);
        operandIndexMap = Collections.unmodifiableMap(m);
    }
    public static final int[][] operandTimingTable = {
            {0, 0, 0}, //DataRegisterOperand
            {0, 0, 0}, //AddressRegisterOperand
            {4, 4, 8}, //AddressRegisterIndirectOperand
            {4, 4, 8}, //AddressRegisterPostIncOperand

            {6, 6, 10}, //AddressRegisterPreDecOperand
            {8, 8, 12}, //AddressRegisterWithDisplacementOperand
            {10, 10, 14}, //AddressRegisterWithIndexOperand
            {8, 8, 12}, //AbsoluteShortOperand

            {12, 12, 16}, //AbsoluteLongOperand
            {8, 8, 12}, //PCWithDisplacementOperand
            {10, 10, 14}, //PCWithIndexOperand
            {4, 4, 8}, //ImmediateOperand
            {0, 0, 0}, //StatusRegisterOperand
    };

    public static int getOperandTiming(Operand op, Size size){
        return operandTimingTable[op.index()][size.ordinal()];
    }

    public static void compareTiming(Cpu cpu, int pc,  int timing) {
        int timingNew = M68kCycles.getTimingByOpcode(cpu.getOpcode());
        if (timingNew > 0 && timing != timingNew) {
            String s = cpu.getInstruction().disassemble(pc, cpu.getOpcode()).toString();
            System.out.println(s + " [" + cpu.getInstruction() + ",old: " + timing + ",new: " + timingNew + "]");
        }
    }
}
