package miggy.memory;

import m68k.cpu.Size;
import m68k.memory.MemorySpace;

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
public class TestMem extends MemorySpace {

    public TestMem(int size) {
        super(size);
    }

    public static TestMem create(int size) {

        TestMem tm = new TestMem(size);
        return tm;
    }

    public void reset() {
        //reset memory and ROM
    }

    public int peek(int address, Size size) {
        int result;

        switch (size) {
            case Byte: {
                result = this.readByte(address);
                break;
            }
            case Word: {
                result = this.readWord(address);
                break;
            }
            case Long: {
                result = this.readLong(address);
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid data size specified");
        }

        return (int) (result & size.mask());
    }

    public void poke(int address, int value, Size size) {
        switch (size) {
            case Byte: {
                this.writeByte(address, value);
                break;
            }
            case Word: {
                this.writeWord(address, value);
                break;
            }
            case Long: {
                this.writeLong(address, value);
                break;
            }
        }
    }
}
