package m68k.cpu.instructions;

import junit.framework.Assert;
import junit.framework.TestCase;
import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class AddressRegisterPreDecOperandTest extends TestCase {

    AddressSpace bus;
    Cpu cpu;

    Map<Integer, Integer> wordWrites = new LinkedHashMap<>();

    public void setUp() {
        //create 1kb of memory for the cpu
        bus = new MemorySpace(1){
            @Override
            public void writeWord(int addr, int value) {
                wordWrites.put(addr, value);
                super.writeWord(addr, value);
            }
        };

        cpu = new MC68000();
        cpu.setAddressSpace(bus);
        cpu.reset();
        cpu.setAddrRegisterLong(7, 0x200);
        wordWrites.clear();
    }

    public void testLswWrittenFirst(){
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

        Assert.assertEquals(cpu.getAddrRegisterLong(6), firstWordPos);

        long res = bus.readLong(firstWordPos);
        Assert.assertEquals(res, value);

        Assert.assertEquals(wordWrites.size(), 2);

        Iterator<Map.Entry<Integer, Integer>> i = wordWrites.entrySet().iterator();
        Map.Entry<Integer, Integer> first = i.next();
        Assert.assertEquals(secondWordPos, first.getKey().intValue());
        Assert.assertEquals(lsw, first.getValue().intValue());

        Map.Entry<Integer, Integer> second = i.next();
        Assert.assertEquals(firstWordPos, second.getKey().intValue());
        Assert.assertEquals(msw, second.getValue().intValue());

    }
}
