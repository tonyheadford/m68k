package m68k.util;

import m68k.cpu.Cpu;
import m68k.memory.AddressSpace;
import org.junit.jupiter.api.Assertions;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class TestCpuUtil {

    public static void writeCodeAndSetPc(Cpu cpu, AddressSpace bus, int pc, int... opcodes){
        int pos = pc;
        for (int opc : opcodes) {
            bus.writeWord(pos, opc);
            pos += 2;
        }
        cpu.setPC(pc);
    }

    /*
      Adapt Junit3 style asserts to Junit5
     */
    public static void assertEquals(int exp, int actual){
        Assertions.assertEquals(exp, actual);
    }

    public static void assertEquals(String str, int exp, int actual){
        Assertions.assertEquals(exp, actual, str);
    }

    public static void assertEquals(String str, String exp, String actual){
        Assertions.assertEquals(exp, actual, str);
    }

    public static void assertNotEquals(String str, int exp, int actual){
        Assertions.assertNotEquals(exp, actual, str);
    }

    public static void assertTrue(boolean actual){
        Assertions.assertTrue(actual);
    }

    public static void assertTrue(String str, boolean actual){
        Assertions.assertTrue(actual, str);
    }

    public static void assertFalse(boolean actual){
        Assertions.assertFalse(actual);
    }

    public static void assertFalse(String str, boolean actual){
        Assertions.assertFalse(actual, str);
    }

    public static void assertNotNull(String str, Object actual){
        Assertions.assertNotNull(actual, str);
    }
}
