package m68k.cpu;

import m68k.cpu.operand.Operand;
import m68k.memory.AddressSpace;

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
public interface Cpu {
	int C_FLAG_BITS = 0;
	int V_FLAG_BITS = 1;
	int Z_FLAG_BITS = 2;
	int N_FLAG_BITS = 3;
	int X_FLAG_BITS = 4;
	int C_FLAG = 1 << C_FLAG_BITS;
	int V_FLAG = 1 << V_FLAG_BITS;
	int Z_FLAG = 1 << Z_FLAG_BITS;
	int N_FLAG = 1 << N_FLAG_BITS;
	int X_FLAG = 1 << X_FLAG_BITS;
	int INTERRUPT_FLAGS_MASK = 0x0700;
	int SUPERVISOR_FLAG = 0x2000;
	int TRACE_FLAG = 0x8000;

	int CCR_MASK = 0x1F;
	int SR_MASK = 0xE700 | CCR_MASK; //0xe71f

	//24 bits
	int PC_MASK = 0xFF_FFFF;

	int NUM_OPCODES = 0x1_0000;

	void setAddressSpace(AddressSpace memory);

	void reset();

	void resetExternal();

	void stop();

	int execute();
	
	// data registers
	int getDataRegisterByte(int reg);
	int getDataRegisterByteSigned(int reg);
	int getDataRegisterWord(int reg);
	int getDataRegisterWordSigned(int reg);
	int getDataRegisterLong(int reg);
	void setDataRegisterByte(int reg, int value);
	void setDataRegisterWord(int reg, int value);
	void setDataRegisterLong(int reg, int value);
	// address registers
	int getAddrRegisterByte(int reg);
	int getAddrRegisterByteSigned(int reg);
	int getAddrRegisterWord(int reg);
	int getAddrRegisterWordSigned(int reg);
	int getAddrRegisterLong(int reg);
	void setAddrRegisterByte(int reg, int value);
	void setAddrRegisterWord(int reg, int value);
	void setAddrRegisterLong(int reg, int value);
	//memory interface
	int readMemoryByte(int addr);
	int readMemoryByteSigned(int addr);
	int readMemoryWord(int addr);
	int readMemoryWordSigned(int addr);
	int readMemoryLong(int addr);
	void writeMemoryByte(int addr, int value);
	void writeMemoryWord(int addr, int value);
	void writeMemoryLong(int addr, int value);
	//addr reg helpers
	void incrementAddrRegister(int reg, int numBytes);
	void decrementAddrRegister(int reg, int numBytes);
	
	// PC reg
	int getPC();
	void setPC(int address);
	// pc fetches - for reading data following instructions and incrementing the PC afterwards
	int fetchPCWord();
	int fetchPCWordSigned();
	int fetchPCLong();
	// status reg
	boolean isSupervisorMode();
	int getCCRegister();
	int getSR();
	void setCCRegister(int value);
	void setSR(int value);
	void setSR2(int value);
	//flags
	void setFlags(int flags);
	void clrFlags(int flags);
	boolean isFlagSet(int flag);
	void calcFlags(InstructionType type, int s, int d, int r, Size sz);
	void calcFlagsParam(InstructionType type, int s, int d, int r, int extraParam, Size sz);
	boolean testCC(int cc);
	int getOpcode();
	int getPrefetchWord();

	// stacks
	int getUSP();
	void setUSP(int address);
	int getSSP();
	void setSSP(int address);
	void pushWord(int value);
	void pushLong(int value);
	int popWord();
	int popLong();
	
	// exceptions & interrupts
	void raiseException(int vector);
	void raiseSRException();
	void raiseInterrupt(int priority);
	int getInterruptLevel();

	Instruction getInstruction();

	//source EA
	Operand resolveSrcEA(int mode, int reg, Size sz);
	// destination EA
	Operand resolveDstEA(int mode, int reg, Size sz);

	// disassembling
	Instruction getInstructionAt(int address);
	Instruction getInstructionFor(int opcode);
	DisassembledOperand disassembleSrcEA(int address, int mode, int reg, Size sz);
	DisassembledOperand disassembleDstEA(int address, int mode, int reg, Size sz);
}
