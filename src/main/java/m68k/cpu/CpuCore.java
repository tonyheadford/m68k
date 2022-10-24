package m68k.cpu;

import m68k.cpu.operand.Operand;
import m68k.cpu.operand.Operands.*;
import m68k.memory.AddressSpace;

import static m68k.cpu.CpuUtils.signExtendByte;
import static m68k.cpu.CpuUtils.signExtendWord;

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
public abstract class CpuCore implements Cpu
{
	protected AddressSpace memory;
	protected int[] data_regs;
	protected int[] addr_regs;
	protected int reg_pc;
	protected int reg_sr;
	protected int reg_usp;
	protected int reg_ssp;

	//prefetch related
	protected int ir;
	protected Operand[] srcHandlers;
	protected Operand[] dstHandlers;
	protected Operand srcEAHandler;
	protected Operand dstEAHandler;
	protected int currentInstructionAddress;
	protected int opcode;
	protected Instruction instruction;
	protected StringBuilder disasmBuffer;

	public CpuCore()
	{
		data_regs = new int[8];
		addr_regs = new int[8];

		srcEAHandler = null;
		dstEAHandler = null;

		memory = null;

		disasmBuffer = new StringBuilder(64);
		initEAHandlers();
	}

	public void setAddressSpace(AddressSpace as)
	{
		this.memory = as;
	}

	public void reset()
	{
		//NOTE: called during initialization
		reg_ssp = memory.readLong(0);
		addr_regs[7] = reg_ssp;
		reg_pc = memory.readLong(4);
		//supervisor mode, interrupts enabled
		reg_sr = 0x2700;
		prefetch();
	}

	@Override
	public void resetExternal() {
		//NOTE: this has to be sent to all external devices - called by RESET instruction
	}

	public void stop()
	{
		//TODO: called by STOP instruction - should halt cpu and wait for interrupt
	}

	public int getDataRegisterByte(int reg)
	{
		return data_regs[reg] & 0x00ff;
	}
	public int getDataRegisterByteSigned(int reg)
	{
		return signExtendByte(data_regs[reg]);
	}
	public int getDataRegisterWord(int reg)
	{
		return data_regs[reg] & 0x0000ffff;
	}
	public int getDataRegisterWordSigned(int reg)
	{
		return signExtendWord(data_regs[reg]);
	}
	public int getDataRegisterLong(int reg)
	{
		return data_regs[reg];
	}
	public void setDataRegisterByte(int reg, int value)
	{
		data_regs[reg] = (data_regs[reg] & 0xffffff00) | (value & 0x00ff);
	}
	public void setDataRegisterWord(int reg, int value)
	{
		data_regs[reg] = (data_regs[reg] & 0xffff0000) | (value & 0x0000ffff);
	}
	public void setDataRegisterLong(int reg, int value)
	{
		data_regs[reg] = value;
	}
	// address registers
	public int getAddrRegisterByte(int reg)
	{
		return addr_regs[reg] & 0x00ff;
	}
	public int getAddrRegisterByteSigned(int reg)
	{
		return signExtendByte(addr_regs[reg]);
	}
	public int getAddrRegisterWord(int reg)
	{
		return addr_regs[reg] & 0x0000ffff;
	}
	public int getAddrRegisterWordSigned(int reg)
	{
		return signExtendWord(addr_regs[reg]);
	}
	public int getAddrRegisterLong(int reg)
	{
		return addr_regs[reg];
	}
	public void setAddrRegisterByte(int reg, int value)
	{
		addr_regs[reg] = (addr_regs[reg] & 0xffffff00) | (value & 0x00ff);
		if(reg == 7)
		{
			if(isSupervisorMode())
			{
				reg_ssp = addr_regs[reg];
			}
			else
			{
				reg_usp = addr_regs[reg];
			}
		}
	}
	public void setAddrRegisterWord(int reg, int value)
	{
		addr_regs[reg] = (addr_regs[reg] & 0xffff0000) | (value & 0x0000ffff);
		if(reg == 7)
		{
			if(isSupervisorMode())
			{
				reg_ssp = addr_regs[reg];
			}
			else
			{
				reg_usp = addr_regs[reg];
			}
		}
	}
	public void setAddrRegisterLong(int reg, int value)
	{
		addr_regs[reg] = value;
		if(reg == 7)
		{
			if(isSupervisorMode())
			{
				reg_ssp = value;
			}
			else
			{
				reg_usp = value;
			}
		}
	}


	public int getPC()
	{
		return reg_pc;
	}

	public void setPC(int address)
	{
		reg_pc = address;
		prefetch();
	}

	// pc fetches - for reading data following instructions and incrementing the PC afterwards

	public int prefetch(){
		ir = readMemoryWord(reg_pc);
		return ir;
	}
	public int fetchPCWord()
	{
		int value = readMemoryWord(reg_pc);
		reg_pc += 2;
		return value;
	}

	public int fetchPCWordSigned()
	{
		int value = readMemoryWordSigned(reg_pc);
		reg_pc += 2;
		return value;
	}

	public int fetchPCLong()
	{
		int value = readMemoryLong(reg_pc);
		reg_pc += 4;
		return value;
	}

	// status reg
	public int getCCRegister()
	{
		return reg_sr & 0x00ff;
	}

	public int getSR()
	{
		return reg_sr;
	}

	public void setCCRegister(int value)
	{
		reg_sr = (reg_sr & 0xff00) | (value & CCR_MASK);
	}

	public void setSR(int value)
	{
		//check for supervisor bit change
		if(((reg_sr & SUPERVISOR_FLAG) ^ (value & SUPERVISOR_FLAG)) != 0)
		{
			//if changing via this method don't push/pop sr and pc - this is only called by andi/eori/ori

			if ((value & SUPERVISOR_FLAG) != 0) {
				reg_usp = addr_regs[7];
				addr_regs[7] = reg_ssp;
			} else {
				//switch stacks
				reg_ssp = addr_regs[7];
				addr_regs[7] = reg_usp;
			}
		}
		reg_sr = value & SR_MASK;
	}

	/**
	 * Set the SR when coming from an RTE.
	 * We might already have been IN supervisor mode when the exception was caused (eg a Trap called in supervisor mode),
	 * so we must check the S bit we get back from the stack and possibly STAY in supervisor mode even after the RTE.
	 *
	 * @param value
	 */
	public void setSR2(int value) {
		// old value of SR, this will be in supermode
		reg_sr = value & SR_MASK;                            // new value of SR, could be user mode or super mode
		if ((reg_sr & SUPERVISOR_FLAG) == 0)    // we changed back to user mode,change stack pointer
		{
			reg_ssp = addr_regs[7];                // keep supervisor stack pointer
			addr_regs[7] = reg_usp;            // get user stack pointer
		}
	}

	public void setFlags(int flags)
	{
		//only set CC flags so clear top byte
		reg_sr |= (flags & 0x00ff);
	}

	public void clrFlags(int flags)
	{
		//we need to invert the flags and then AND them with the SR to clear
		reg_sr &= ~(flags & 0x00ff);
	}

	public boolean isFlagSet(int flag)
	{
		return ((reg_sr & flag) == flag);
	}

	public void calcFlags(InstructionType type, int src, int dst, int result, Size sz)
	{
		calcFlagsParam(type, src, dst, result, 0, sz);
	}

	public void calcFlagsParam(InstructionType type, int src, int dst, int result, int extraParam, Size sz)
	{
		boolean Sm = (src & sz.msb()) != 0;
		boolean Dm = (dst & sz.msb()) != 0;
		boolean Rm = (result & sz.msb()) != 0;
		boolean Zm = (result & sz.mask()) == 0;
//		boolean Zm = result == 0;

		switch (type) {
			case ADD:    //ADD, ADDI, ADDQ
			{
				if ((Sm && Dm && !Rm) || (!Sm && !Dm && Rm)) {
					reg_sr |= V_FLAG;
				} else
				{
					reg_sr &= ~(V_FLAG);
				}

				if((Sm && Dm) || (!Rm && Dm) || (Sm && !Rm))
				{
					reg_sr |= (C_FLAG | X_FLAG);
				}
				else
				{
					reg_sr &= ~(C_FLAG | X_FLAG);
				}

				if(Zm)
				{
					reg_sr |= Z_FLAG;
				}
				else
				{
					reg_sr &= ~(Z_FLAG);
				}

				if(Rm)
				{
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}
				break;
			}

			case ADDX:
			{
				if((Sm && Dm && !Rm) || (!Sm && !Dm && Rm))
				{
					reg_sr |= V_FLAG;
				}
				else
				{
					reg_sr &= ~(V_FLAG);
				}

				if ((Sm && Dm) || (!Rm && Dm) || (Sm && !Rm)) {
					reg_sr |= (C_FLAG | X_FLAG);
				} else {
					reg_sr &= ~(C_FLAG | X_FLAG);
				}

				if (!Zm) {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				} else {
					reg_sr &= ~(N_FLAG);
				}
				break;
			}

			case ASL:
			{
				//params are different here!
				if(src != 0)	// shift count
                {
					if (dst != 0)    // last bit out
					{
						reg_sr |= (C_FLAG | X_FLAG);
					} else {
						reg_sr &= ~(C_FLAG | X_FLAG);
					}
				} else {
					reg_sr &= ~(C_FLAG);
				}

				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}

				if(extraParam != 0)	// msb changed
				{
					reg_sr |= V_FLAG;
				}
				else
				{
					reg_sr &= ~V_FLAG;
				}

				break;
			}

			case ASR:
			{
				//params are different here!
				if(src != 0)	// shift count
                {
					if (dst != 0)    // last bit out
					{
						reg_sr |= (C_FLAG | X_FLAG);
					} else {
						reg_sr &= ~(C_FLAG | X_FLAG);
					}
				} else {
					reg_sr &= ~(C_FLAG);
				}

				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}

				// always cleared
				reg_sr &= ~V_FLAG;

				break;
			}

			case CMP:	// CMP, CMPA, CMPI CMPM
			{
				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if ((!Sm && Dm && !Rm) || (Sm && !Dm && Rm)) {
					reg_sr |= V_FLAG;
				}
				else
				{
					reg_sr &= ~(V_FLAG);
				}

				if((Sm && !Dm) || (Rm && !Dm) || (Sm && Rm))
				{
					reg_sr |= C_FLAG;
				}
				else
				{
					reg_sr &= ~(C_FLAG);
				}

				if(Rm)
				{
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}
				break;
			}
			case LSL:
			case LSR:
			case ROXL:
			case ROXR:
			{
				if(src > 0)	//shift count
                {
					if (dst != 0)    //last bit out
					{
						reg_sr |= (C_FLAG | X_FLAG);
					} else {
						reg_sr &= ~(C_FLAG | X_FLAG);
					}
				} else {
					reg_sr &= ~(C_FLAG);
				}

				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}

				reg_sr &= ~(V_FLAG);

				break;
			}
			case AND:
			case EOR:
			case MOVE:
			case NOT:
			case OR: {
				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}

				reg_sr &= ~(V_FLAG | C_FLAG);
				break;
			}
			case NEG: {
				if (Sm && Rm) {
					reg_sr |= V_FLAG;
				} else {
					reg_sr &= ~(V_FLAG);
				}

				if (Zm) {
					reg_sr |= Z_FLAG;
					reg_sr &= ~(X_FLAG | C_FLAG);
				} else {
					reg_sr &= ~(Z_FLAG);
					reg_sr |= (X_FLAG | C_FLAG);
				}
				if (Rm)
				{
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}
				break;
			}
			case NEGX:
			{
				if(Sm && Rm)
				{
					reg_sr |= V_FLAG;
				}
				else
				{
					reg_sr &= ~(V_FLAG);
				}
				if (Sm || Rm) {
					reg_sr |= (X_FLAG | C_FLAG);
				} else {
					reg_sr &= ~(X_FLAG | C_FLAG);
				}
				if (!Zm) {
					reg_sr &= ~(Z_FLAG);
				}
				if (Rm) {
					reg_sr |= N_FLAG;
				} else {
					reg_sr &= ~(N_FLAG);
				}
				break;
			}
			case ROL:
			case ROR:
			{
				if(src > 0)	//shift count
                {
					if (dst != 0)    //last bit out
					{
						reg_sr |= C_FLAG;
					} else {
						reg_sr &= ~(C_FLAG);
					}
				} else {
					reg_sr &= ~(C_FLAG);
				}

				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}

				reg_sr &= ~(V_FLAG);

				break;
			}

			case SUB: {
				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if ((!Sm && Dm && !Rm) || (Sm && !Dm && Rm)) {
					reg_sr |= V_FLAG;
				}
				else
				{
					reg_sr &= ~(V_FLAG);
				}

				if((Sm && !Dm) || (Rm && !Dm) || (Sm && Rm))
				{
					reg_sr |= (C_FLAG | X_FLAG);
				}
				else
				{
					reg_sr &= ~(C_FLAG | X_FLAG);
				}

				if(Rm)
				{
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}
				break;
			}

			case SUBX: {
				if (!Zm) {
					reg_sr &= ~(Z_FLAG);
				}

				if ((!Sm && Dm && !Rm) || (Sm && !Dm && Rm)) {
					reg_sr |= V_FLAG;
				} else {
					reg_sr &= ~(V_FLAG);
				}

				if((Sm && !Dm) || (Rm && !Dm) || (Sm && Rm))
				{
					reg_sr |= (C_FLAG | X_FLAG);
				}
				else
				{
					reg_sr &= ~(C_FLAG | X_FLAG);
				}

				if(Rm)
				{
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}
				break;
			}
                            
			// swap also affects the SR
			case SWAP: {
				if (Zm) {
					reg_sr |= Z_FLAG;
				} else {
					reg_sr &= ~(Z_FLAG);
				}

				if (Rm) {
					reg_sr |= N_FLAG;
				}
				else
				{
					reg_sr &= ~(N_FLAG);
				}
				reg_sr &= ~(V_FLAG);            // these are always set to 0
				reg_sr &= ~(C_FLAG);
				break;
			}
			default:
			{
				throw new IllegalArgumentException("No flags handled for " + type);
			}
		}
	}

	public boolean testCC(int cc)
	{
		int ccr = reg_sr & 0x001f;

		switch(cc)
		{
			case 0:		// T
			{
				return true;
			}
			case 1:		// F
			{
				return false;
			}
			case 2:		//HI:
			{
				return ((ccr & (C_FLAG | Z_FLAG)) == 0);
			}
			case 3:		//LS:
			{
				return ((ccr & (C_FLAG | Z_FLAG)) != 0);
			}
			case 4:		//CC:
			{
				return ((ccr & C_FLAG) == 0);
			}
			case 5:		//CS:
			{
				return ((ccr & C_FLAG) != 0);
			}
			case 6:		//NE:
			{
				return ((ccr & Z_FLAG) == 0);
			}
			case 7:		//EQ:
			{
				return ((ccr & Z_FLAG) != 0);
			}
			case 8:		//VC:
			{
				return ((ccr & V_FLAG) == 0);
			}
			case 9:		//VS:
			{
				return ((ccr & V_FLAG) != 0);
			}
			case 10:	//PL:
			{
				return ((ccr & N_FLAG) == 0);
			}
			case 11:	//MI:
			{
				return ((ccr & N_FLAG) != 0);
			}
			case 12:	//GE:
			{
				int v = ccr & (N_FLAG | V_FLAG);
				return (v == 0 || v == (N_FLAG | V_FLAG));
			}
			case 13:	//LT:
			{
				int v = ccr & (N_FLAG | V_FLAG);
				return (v == N_FLAG || v == V_FLAG);
			}
			case 14:	//GT:
			{
				int v = ccr & (N_FLAG | V_FLAG | Z_FLAG);
				return (v == 0 || v == (N_FLAG | V_FLAG));
			}
			case 15:	//LE:
			{
				int v = ccr & (N_FLAG | V_FLAG | Z_FLAG);
				return ((v & Z_FLAG) != 0 || (v == N_FLAG) || (v == V_FLAG));
			}
		}
		throw new IllegalArgumentException("Invalid Condition Code value!");
	}

	public boolean isSupervisorMode()
	{
		return (reg_sr & SUPERVISOR_FLAG) == SUPERVISOR_FLAG;
	}

	public void pushWord(int value)
	{
		// do we need to keep the usp or ssp up to date ?
		addr_regs[7] -= 2;
		writeMemoryWord(addr_regs[7], value);
	}

	public void pushLong(int value)
	{
		// do we need to keep the usp or ssp up to date ?
		addr_regs[7] -= 4;
		writeMemoryLong(addr_regs[7], value);
	}

	public int popWord()
	{
		// do we need to keep the usp or ssp up to date ?
		int val = readMemoryWord(addr_regs[7]);
		addr_regs[7] += 2;
		return val;
	}

	public int popLong()
	{
		// do we need to keep the usp or ssp up to date ?
		int val = readMemoryLong(addr_regs[7]);
		addr_regs[7] += 4;
		return val;
	}

	public int getUSP()
	{
		return reg_usp;
	}

	public void setUSP(int address)
	{
		reg_usp = address;
		if(!isSupervisorMode())
			addr_regs[7] = reg_usp;
	}

	public int getSSP()
	{
		return reg_ssp;
	}

	public void setSSP(int address)
	{
		reg_ssp = address;

		if(isSupervisorMode())
			addr_regs[7] = reg_ssp;
	}

	public void setSupervisorMode(boolean enable)
	{
		if(enable)
		{
			int old_sr = reg_sr;
			if ((reg_sr & SUPERVISOR_FLAG) == 0) // were we in supervisor mode already?....
			{
				reg_sr |= SUPERVISOR_FLAG;      // ...no, so set supervisor bit
				reg_usp = addr_regs[7];         // and change stack pointers
				addr_regs[7] = reg_ssp;
			}

			//save pc and status regs
			pushLong(reg_pc);
			pushWord(old_sr);
		} else {
			//switch back to user mode
			if ((reg_sr & SUPERVISOR_FLAG) != 0) {
				//restore PC and status regs
				setSR(popWord());
				reg_pc = popLong();

				//switch stacks
				reg_ssp = addr_regs[7];
				addr_regs[7] = reg_usp;
			}
		}
	}

	public void raiseException(int vector)
	{
		int address = (vector & 0x00ff) << 2;

		// don't call setSupervisorMode, do it directly
		int old_sr = reg_sr;	// SR BEFORE the exception

		if ((reg_sr & SUPERVISOR_FLAG) == 0)	// were we in supervisor mode already?....
		{
			reg_sr |= SUPERVISOR_FLAG;	// ...no, so set supervisor bit
			reg_usp = addr_regs[7];		// and change stack pointers
			addr_regs[7] = reg_ssp;
		}

		//save pc and status regs
		pushLong(reg_pc);
		pushWord(old_sr);
		reg_sr &= ~(TRACE_FLAG);		// exceptions unset the trace flag

		int xaddress = readMemoryLong(address);
		if(xaddress == 0)
		{
			//interrupt vector is uninitialised
			//raise a uninitialised interrupt vector exception instead
			//vector 15 == 0x003c
			xaddress = readMemoryLong(0x003c);
			//if this is zero as well the CPU should halt
			if(xaddress == 0)
			{
				throw new CpuException("Interrupt vector not set for uninitialised interrupt vector while trapping uninitialised vector " + vector);
			}
		}

		reg_pc = xaddress;
	}

	public void raiseSRException()
	{
		//always a privilege violation - vector 8
		int address = 32;

		//switch to supervisor mode
		int old_sr = reg_sr;

		if((reg_sr & SUPERVISOR_FLAG) == 0)
		{
			reg_sr |= SUPERVISOR_FLAG;	//set supervisor bit
			//switch stacks
			reg_usp = addr_regs[7];
			addr_regs[7] = reg_ssp;
		}

		//subtly different in that the address of the instruction is pushed rather than the address of the next instruction
		//save pc and status regs - operands fetched in supervisor mode so PC at current address
		pushLong(currentInstructionAddress);
		pushWord(old_sr);

		//todo: handle special exception cases & build stack info

		int xaddress = readMemoryLong(address);
		if(xaddress == 0)
		{
			//interrupt vector is uninitialised
			//raise a uninitialised interrupt vector exception instead
			//vector 15 == 0x003c
			xaddress = readMemoryLong(0x003c);
			//if this is zero as well the CPU should halt
			if(xaddress == 0)
			{
				throw new CpuException("Interrupt vector not set for uninitialised interrupt vector while trapping uninitialised vector 8");
			}
		}

		reg_pc = xaddress;
	}


	public int getInterruptLevel()
	{
		return (reg_sr >> 8) & 0x07;
	}

	protected void setInterruptLevel(int level)
	{
		reg_sr &= ~(INTERRUPT_FLAGS_MASK);
		reg_sr |= (level & 0x07) << 8;
	}

	public void raiseInterrupt(int priority)
	{
		if(priority == 0)
			return;

		priority &= 0x07;

		//is it higher than the current interrupt mask ?
    	if(priority >  getInterruptLevel())
		{
			//make it an autovectored interrupt
			raiseException(priority + 24);
			setInterruptLevel(priority);
		}
	}

	@Override
	public int getOpcode(){
		return opcode;
	}

	@Override
	public Instruction getInstruction(){
		return instruction;
	}

	//memory interface
	public int readMemoryByte(int addr)
	{
		return memory.readByte(addr);
	}
	public int readMemoryByteSigned(int addr)
	{
		return signExtendByte(memory.readByte(addr));
	}
	public int readMemoryWord(int addr)
	{
		return memory.readWord(addr);
	}
	public int readMemoryWordSigned(int addr)
	{
		return signExtendWord(memory.readWord(addr));
	}
	public int readMemoryLong(int addr)
	{
		return memory.readLong(addr);
	}
	public void writeMemoryByte(int addr, int value)
	{
		memory.writeByte(addr, value);
	}
	public void writeMemoryWord(int addr, int value)
	{
		memory.writeWord(addr, value);
	}
	public void writeMemoryLong(int addr, int value)
	{
		memory.writeLong(addr, value);
	}

	public Operand resolveSrcEA(int mode, int reg, Size size)
	{
		if(mode < 7)
			srcEAHandler = srcHandlers[mode];
		else
			srcEAHandler = srcHandlers[mode + reg];

		srcEAHandler.init(reg, size);
		return srcEAHandler;
	}

	// destination EA
	public Operand resolveDstEA(int mode, int reg, Size size)
	{
		if(mode < 7)
			dstEAHandler = dstHandlers[mode];
		else
			dstEAHandler = dstHandlers[mode + reg];

		dstEAHandler.init(reg, size);
		return dstEAHandler;
	}

	public DisassembledOperand disassembleSrcEA(int address, int mode, int reg, Size sz)
	{
		return disassembleEA(address, mode, reg, sz, true);
	}

	public DisassembledOperand disassembleDstEA(int address, int mode, int reg, Size sz)
	{
		return disassembleEA(address, mode, reg, sz, false);
	}

	protected DisassembledOperand disassembleEA(int address, int mode, int reg, Size sz, boolean is_src)
	{
		int bytes_read = 0;
		int mem = 0;
		disasmBuffer.delete(0, disasmBuffer.length());

		switch(mode)
		{
			case 0:
			{
				disasmBuffer.append("d").append(reg);
				break;
			}
			case 1:
			{
				disasmBuffer.append("a").append(reg);
				break;
			}
			case 2:
			{
				disasmBuffer.append("(a").append(reg).append(")");
				break;
			}
			case 3:
			{
				disasmBuffer.append("(a").append(reg).append(")+");
				break;
			}
			case 4:
			{
				disasmBuffer.append("-(a").append(reg).append(")");
				break;
			}
			case 5:
			{
				mem = readMemoryWordSigned(address);
				disasmBuffer.append(String.format("$%04x",(short)mem)).append("(a").append(reg).append(")");
				bytes_read = 2;
				break;
			}
			case 6:
			{
				mem = readMemoryWord(address);
				int dis = signExtendByte(mem);
				disasmBuffer.append(String.format("$%02x",(byte)dis)).append("(a").append(reg).append(",");
				disasmBuffer.append(((mem & 0x8000) != 0 ? "a" : "d")).append((mem >> 12) & 0x07).append(((mem & 0x0800) != 0 ? ".l" : ".w")).append(")");
				bytes_read = 2;
				break;
			}
			case 7:
			{
				switch(reg)
				{
					case 0:
					{
						mem = readMemoryWord(address);
						disasmBuffer.append(String.format("$%04x", mem));
						bytes_read = 2;
						break;
					}
					case 1:
					{
						mem = readMemoryLong(address);
						disasmBuffer.append(String.format("$%08x", mem));
						bytes_read = 4;
						break;
					}
					case 2:
					{
						mem = readMemoryWordSigned(address);
						disasmBuffer.append(String.format("$%04x(pc)",(short)mem));
						bytes_read = 2;
						break;
					}
					case 3:
					{
						mem = readMemoryWord(address);
						int dis = signExtendByte(mem);
						disasmBuffer.append(String.format("$%02x(pc,", (byte)dis));
						disasmBuffer.append(((mem & 0x8000) != 0 ? "a" : "d")).append((mem >> 12) & 0x07).append(((mem & 0x0800) != 0 ? ".l" : ".w")).append(")");
						bytes_read = 2;
						break;
					}
					case 4:
					{
						if(is_src)
						{
							if(sz == Size.Long)
							{
								mem = readMemoryLong(address);
								bytes_read = 4;
								disasmBuffer.append(String.format("#$%08x", mem));
							}
							else
							{
								mem = readMemoryWord(address);
								bytes_read = 2;
								disasmBuffer.append(String.format("#$%04x", mem));

								if(sz == Size.Byte)
								{
									mem &= 0x00ff;
								}
							}
						}
						else
						{
							if(sz == Size.Byte)
							{
								disasmBuffer.append("ccr");
							}
							else
							{
								disasmBuffer.append("sr");
							}
						}
						break;
					}
					default:
					{
						throw new IllegalArgumentException("Invalid reg specified for mode 7: " + reg);
					}
				}
				break;
			}
			default:
			{
				throw new IllegalArgumentException("Invalid mode specified: " + mode);
			}
		}
		return new DisassembledOperand(disasmBuffer.toString(), bytes_read, mem);
	}

	//effective address handling
	public void incrementAddrRegister(int reg, int numBytes) {
		if (reg == 7 && numBytes == 1) {
			numBytes = 2;
		}
		addr_regs[reg] += numBytes;
	}

	public void decrementAddrRegister(int reg, int numBytes) {
		if (reg == 7 && numBytes == 1) {
			numBytes = 2;
		}
		addr_regs[reg] -= numBytes;
	}

	protected void initEAHandlers()
	{
		srcHandlers = new Operand[12];
		dstHandlers = new Operand[12];

		srcHandlers[0] = new DataRegisterOperand(this);
		srcHandlers[1] = new AddressRegisterOperand(this);
		srcHandlers[2] = new AddressRegisterIndirectOperand(this);
		srcHandlers[3] = new AddressRegisterPostIncOperand(this);
		srcHandlers[4] = new AddressRegisterPreDecOperand(this);
		srcHandlers[5] = new AddressRegisterWithDisplacementOperand(this);
		srcHandlers[6] = new AddressRegisterWithIndexOperand(this);
		srcHandlers[7] = new AbsoluteShortOperand(this);
		srcHandlers[8] = new AbsoluteLongOperand(this);
		srcHandlers[9] = new PCWithDisplacementOperand(this);
		srcHandlers[10] = new PCWithIndexOperand(this);
		srcHandlers[11] = new ImmediateOperand(this);

		dstHandlers[0] = new DataRegisterOperand(this);
		dstHandlers[1] = new AddressRegisterOperand(this);
		dstHandlers[2] = new AddressRegisterIndirectOperand(this);
		dstHandlers[3] = new AddressRegisterPostIncOperand(this);
		dstHandlers[4] = new AddressRegisterPreDecOperand(this);
		dstHandlers[5] = new AddressRegisterWithDisplacementOperand(this);
		dstHandlers[6] = new AddressRegisterWithIndexOperand(this);
		dstHandlers[7] = new AbsoluteShortOperand(this);
		dstHandlers[8] = new AbsoluteLongOperand(this);
		dstHandlers[9] = new PCWithDisplacementOperand(this);
		dstHandlers[10] = new PCWithIndexOperand(this);
		dstHandlers[11] = new StatusRegisterOperand(this);
	}
}
