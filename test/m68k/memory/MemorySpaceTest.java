package m68k.memory;

import junit.framework.TestCase;

/*
//  M68k - Java Amiga MachineCore
//  Copyright (c) 2008-2010, Tony Headford
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
//  following conditions are met:
//
//    o  Redistributions of source code must retain the above copyright notice, this list of conditions and the
//       following disclaimer.
//    o  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
//       following disclaimer in the documentation and/or other materials provided with the distribution.
//    o  Neither the name of the M68k Project nor the names of its contributors may be used to endorse or promote
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
*/
public class MemorySpaceTest extends TestCase
{
	public void testCreate()
	{
		AddressSpace bus = new MemorySpace(4);
		assertEquals(4096, bus.size());

		bus = new MemorySpace(32);
		assertEquals(32768, bus.size());
	}

	public void testMemory()
	{
		AddressSpace bus = new MemorySpace(1);
		bus.writeByte(4, 0x55);
		assertEquals(0x55000000, bus.readLong(4));
		assertEquals(0x5500, bus.readWord(4));
		assertEquals(0x55, bus.readByte(4));

		bus.writeWord(4, 0x1234);
		assertEquals(0x12340000, bus.readLong(4));
		assertEquals(0x1234, bus.readWord(4));
		assertEquals(0x12, bus.readByte(4));
		assertEquals(0x34, bus.readByte(5));

		bus.writeLong(4, 0x98765432);
		assertEquals(0x98765432, bus.readLong(4));
		assertEquals(0x9876, bus.readWord(4));
		assertEquals(0x5432, bus.readWord(6));
		assertEquals(0x98, bus.readByte(4));
		assertEquals(0x76, bus.readByte(5));
		assertEquals(0x54, bus.readByte(6));
		assertEquals(0x32, bus.readByte(7));
	}
}
