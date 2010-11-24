package m68k.memory;

import java.nio.ByteBuffer;

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
public class MemorySpace implements MemoryBus
{
	private ByteBuffer buffer;

	public MemorySpace(int sizeKb)
	{
		buffer = ByteBuffer.allocateDirect(sizeKb * 1024);
	}

	public int readByte(int addr)
	{
		int v = buffer.get(addr);
		return v & 0x00ff;
	}

	public int readWord(int addr)
	{
		int v =  buffer.getShort(addr);
		return v & 0x0000ffff;
	}

	public int readLong(int addr)
	{
		return buffer.getInt(addr);
	}

	public void writeByte(int addr, int value)
	{
		buffer.put(addr, (byte)(value & 0x00ff));
	}

	public void writeWord(int addr, int value)
	{
		buffer.putShort(addr, (short)(value & 0x0000ffff));
	}

	public void writeLong(int addr, int value)
	{
		buffer.putInt(addr, value);
	}

	public int size()
	{
		return buffer.capacity();
	}
}
