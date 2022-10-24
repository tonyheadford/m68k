package miggy.cpu;

import m68k.cpu.DisassembledInstruction;
import m68k.cpu.Instruction;
import m68k.cpu.MC68000;
import m68k.cpu.Size;
import m68k.util.TestCpuUtil;
import miggy.memory.TestMem;
import org.junit.jupiter.api.Test;

import static m68k.util.TestCpuUtil.assertFalse;


/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class DisassembledInstructionTest {

    @Test public void testDisasmOutputAlignment() {
        MC68000 cpu = new MC68000();
        TestMem tm = new TestMem(0x100);
        cpu.setAddressSpace(tm);
        int limit = 56;

        tm.poke(0, 0x4e71, Size.Word); //NOP
        tm.poke(2, 0x322a_0010, Size.Long); //322a 0010                move.w   $0010(a2),d1
        tm.poke(6, 0x0c2a_0002, Size.Long); //0c2a 0002 0021           cmpi.b   #$02,$0021(a2) [NEW]
        tm.poke(10, 0x0021, Size.Word);
        tm.poke(12, 0x4279, Size.Word); //4279 00ff160c           clr.w    $00ff160c
        tm.poke(14, 0x00ff160c, Size.Long);
        tm.poke(18, 0x66f2, Size.Word); //66f2                    bne.s    $00ff1542 [NEW]
        tm.poke(20, 0x5268_0004, Size.Long); //5268 0004                addq.w   #1,$0004(a0) [NEW]
        tm.poke(24, 0x6100_151c , Size.Long);//6100 151c               bsr.w    $00ff15e0
        tm.poke(28, 0x287c , Size.Word);//287c ffffffc0            movea.l  #$ffffffc0,a4 [NEW]
        tm.poke(30, 0xffffffc0 , Size.Long);
        tm.poke(34, 0x23fc , Size.Word);//23fc 00000000 00a15128   move.l   #$00000000,$00a15128 [NEW]
        tm.poke(36, 0 , Size.Long);
        tm.poke(40, 0x00a15128 , Size.Long);
        tm.poke(44, 0x41fa , Size.Word);//41fa f06c               lea      $f06c(pc),a0 [NEW]
        tm.poke(46, 0xf06c , Size.Word);
        tm.poke(48, 0x4eba , Size.Word);//4eba f3c4               jsr      $f3c4(pc) [NEW]
        tm.poke(50, 0xf3c4 , Size.Word);
        tm.poke(52, 0xf3c4 , Size.Word); //line-f
        tm.poke(54, 0xa3c4 , Size.Word); //line-a

        StringBuilder longFmt = new StringBuilder();
        StringBuilder shortFmt = new StringBuilder();
        for (int i = 0; i < limit;) {
            StringBuilder b = new StringBuilder();
            Instruction inst = cpu.getInstructionAt(i);
            int opcode = tm.peek(i, Size.Word);
            DisassembledInstruction di = inst.disassemble(i, opcode);
            i += di.size();
            di.formatInstruction(b);
            di.shortFormat(shortFmt);
            longFmt.append(b).append("\n");
            shortFmt.append("\n");
            String str = b.toString();
            assertFalse(Character.isLetterOrDigit(str.charAt(33)));
            assertFalse(Character.isLetterOrDigit(str.charAt(34)));
            TestCpuUtil.assertTrue(Character.isLetterOrDigit(str.charAt(35)));
            TestCpuUtil.assertTrue(Character.isLetterOrDigit(str.charAt(36)));

        }
        System.out.println(longFmt);
        System.out.println(shortFmt);
    }
}
