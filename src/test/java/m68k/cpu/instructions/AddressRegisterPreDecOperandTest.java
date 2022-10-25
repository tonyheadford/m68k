package m68k.cpu.instructions;

import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static m68k.util.TestCpuUtil.assertEquals;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class AddressRegisterPreDecOperandTest {

    AddressSpace bus;
    Cpu cpu;

    Map<Integer, Integer> wordWrites = new LinkedHashMap<>();

    @BeforeEach public void setUp() {
        //create 1kb of memory for the cpu
        bus = new MemorySpace(1) {
            @Override
            public void writeWord(int addr, int value) {
                wordWrites.put(addr, value);
                super.writeWord(addr, value);
            }

            @Override
            public void writeLong(int addr, int value) {
                wordWrites.put(addr, (value >> 16) & 0xFFFF);
                wordWrites.put(addr + 2, value & 0xFFFF);
                super.writeLong(addr, value);
            }
        };

        cpu = new MC68000();
        cpu.setAddressSpace(bus);
        cpu.reset();
        cpu.setAddrRegisterLong(7, 0x200);
        wordWrites.clear();
    }

    @Test
    public void testLswWrittenFirst_MOVE() {
        int lsw = 0x2222;
        int msw = 0x1111;
        int value = msw << 16 | lsw;
        int firstWordPos = 0x100;
        int secondWordPos = 0x102;
        int thirdWordPos = 0x104;

        bus.writeWord(4, 0x2d01);    //2d01 move.l   d1,-(a6)
        cpu.setPC(4);
        cpu.setDataRegisterLong(1, value);
        cpu.setAddrRegisterLong(6, thirdWordPos);

        wordWrites.clear();
        cpu.execute();

        assertEquals(cpu.getAddrRegisterLong(6), firstWordPos);

        long res = bus.readLong(firstWordPos);
        Assertions.assertEquals(res, value);

        assertEquals(wordWrites.size(), 2);

        Iterator<Map.Entry<Integer, Integer>> i = wordWrites.entrySet().iterator();
        Map.Entry<Integer, Integer> first = i.next();
        assertEquals(secondWordPos, first.getKey().intValue());
        assertEquals(lsw, first.getValue().intValue());

        Map.Entry<Integer, Integer> second = i.next();
        assertEquals(firstWordPos, second.getKey().intValue());
        assertEquals(msw, second.getValue().intValue());
    }

    @Test public void testLswWrittenFirst_MOVEM() {
        int lsw = 0x2222;
        int msw = 0x1111;
        int value = msw << 16 | lsw;
        int startPos = 0x104;
        int endPos = startPos - 8; // 2 longs
        int valuePos = startPos - 4;

        bus.writeLong(4, 0x48e1_8100);    //48e1 8100                movem.l  d0/d7,-(a1)
        cpu.setPC(4);
        cpu.setDataRegisterLong(7, value);
        cpu.setAddrRegisterLong(1, startPos);

        wordWrites.clear();
        cpu.execute();

        assertEquals(cpu.getAddrRegisterLong(1), endPos);

        long res = bus.readLong(valuePos);
        Assertions.assertEquals(res, value);

        assertEquals(wordWrites.size(), 4);

        int firstWordPos = startPos - 2;
        Iterator<Map.Entry<Integer, Integer>> i = wordWrites.entrySet().iterator();
        Map.Entry<Integer, Integer> first = i.next();
        assertEquals(firstWordPos, first.getKey().intValue());
        assertEquals(lsw, first.getValue().intValue());

        int secondWordPos = firstWordPos - 2;
        Map.Entry<Integer, Integer> second = i.next();
        assertEquals(secondWordPos, second.getKey().intValue());
        assertEquals(msw, second.getValue().intValue());
    }
}