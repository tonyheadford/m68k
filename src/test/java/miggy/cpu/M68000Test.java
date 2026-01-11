package miggy.cpu;

import junit.framework.TestCase;
import m68k.cpu.Instruction;
import m68k.cpu.Size;
import miggy.SystemModel;
import miggy.TestCpu;
import miggy.memory.TestMem;
import org.junit.Before;

/*
//  Miggy - Java Amiga MachineCore
//  Copyright (c) 2008, Tony Headford
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
//  following conditions are met:
//
//    o  Redistributions of source code must retain the above copyright notice, this list of conditions and the
//       following disclaimer.
//    o  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
//       following disclaimer in the documentation and/or other materials provided with the distribution.
//    o  Neither the name of the Miggy Project nor the names of its contributors may be used to endorse or promote
//       products derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
//  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// $Revision: 21 $
*/
public class M68000Test extends TestCase {
    protected TestCpu cpu;

    public M68000Test(String test) {
        super(test);
    }

    @Before
    protected void setUp() {
        //set up memory
        SystemModel.MEM = TestMem.create(512);
        SystemModel.CPU = new TestCpu(SystemModel.MEM);
        cpu = SystemModel.CPU;
    }

    @Override
    protected void tearDown() {
        cpu = null;
    }

    public void testDataRegisters() {
        //basic set and get
        for (int n = 0; n < 8; n++) {
            cpu.setDataRegister(n, (n * 2) + 1000);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getDataRegister(n);
            assertEquals("Check Basic", (n * 2) + 1000, val);
        }
        //check for out of bounds
        try {
            cpu.setDataRegister(8, 100);
            fail("Cannot set data register larger than seven");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        //check for out of bounds
        try {
            cpu.setDataRegister(-8, 100);
            fail("Cannot set data register less than zero");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        // check bytes
        for (int n = 0; n < 8; n++) {
            cpu.setDataRegister(n, (n * 2), Size.Byte);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getDataRegister(n);
            assertEquals("Check Bytes", ((n * 2) + 1000 & 0xffffff00) + (n * 2), val);
        }
        // check words
        for (int n = 0; n < 8; n++) {
            cpu.setDataRegister(n, (n * 2), Size.Word);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getDataRegister(n);
            assertEquals("Check Words", ((n * 2) + 1000 & 0xffff0000) + (n * 2), val);
        }
        // check longs
        for (int n = 0; n < 8; n++) {
            cpu.setDataRegister(n, (n * 2), Size.Long);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getDataRegister(n);
            assertEquals("Check Longs", (n * 2), val);
        }
    }

    public void testAddrRegisters() {
        //basic set and get
        for (int n = 0; n < 8; n++) {
            cpu.setAddrRegister(n, (n * 2) + 1000);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getAddrRegister(n);
            assertEquals("Check Basic", (n * 2) + 1000, val);
        }
        //check for out of bounds
        try {
            cpu.setAddrRegister(8, 100);
            fail("Cannot set addr register larger than seven");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        //check for out of bounds
        try {
            cpu.setAddrRegister(-8, 100);
            fail("Cannot set data register less than zero");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
        // check bytes
        for (int n = 0; n < 8; n++) {
            cpu.setAddrRegister(n, (n * 2), Size.Byte);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getAddrRegister(n);
            assertEquals("Check Bytes", ((n * 2) + 1000 & 0xffffff00) + (n * 2), val);
        }
        // check words
        for (int n = 0; n < 8; n++) {
            cpu.setAddrRegister(n, (n * 2), Size.Word);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getAddrRegister(n);
            assertEquals("Check Words", ((n * 2) + 1000 & 0xffff0000) + (n * 2), val);
        }
        // check longs
        for (int n = 0; n < 8; n++) {
            cpu.setAddrRegister(n, (n * 2), Size.Long);
        }
        for (int n = 0; n < 8; n++) {
            int val = cpu.getAddrRegister(n);
            assertEquals("Check Longs", (n * 2), val);
        }
    }

    public void testPC() {
        //set up memory
        SystemModel.MEM = TestMem.create(512);
        SystemModel.CPU = new TestCpu(SystemModel.MEM);
        cpu = SystemModel.CPU;

        SystemModel.MEM.poke(100, (short) 0x1234, Size.Word);
        SystemModel.MEM.poke(102, (short) 0x5678, Size.Word);
        SystemModel.MEM.poke(104, (short) 0x5051, Size.Word);

        cpu.setPC(100);
        int v = cpu.fetch(Size.Word);
        assertEquals("PC Read 1", 0x1234, v);
        v = cpu.fetch(Size.Word);
        assertEquals("PC Read 2", 0x5678, v);
        v = cpu.fetch(Size.Word);
        assertEquals("PC Read 3", 0x5051, v);

        assertEquals("PC increments", 106, cpu.getPC());

        cpu.setPC(100);
        v = cpu.fetch(Size.Long);
        assertEquals("PC Read 4", 0x12345678, v);
        assertEquals("PC increment long", 104, cpu.getPC());

        SystemModel.MEM = null;
    }

    public void testGetInstruction() {
        Instruction i = cpu.getInstructionFor(0);
        assertEquals("ORI Test", "m68k.cpu.instructions.ORI$1", i.getClass().getName());
        i = cpu.getInstructionFor(0xa);
        assertNotNull("Null test", i);
        assertEquals("UNKNOWN Test", "m68k.cpu.instructions.UNKNOWN", i.getClass().getName());
        //	System.out.println(i.getClass().getName());
    }
}
