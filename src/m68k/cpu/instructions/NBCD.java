package m68k.cpu.instructions;

import m68k.cpu.*;

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
public class NBCD implements InstructionHandler
{
	protected final Cpu cpu;

	public NBCD(Cpu cpu)
	{
		this.cpu = cpu;
	}

	public final void register(InstructionSet is)
	{
		int base = 0x4800;
		Instruction i = new Instruction() {
			public int execute(int opcode)
			{
				return nbcd(opcode);
			}
			public DisassembledInstruction disassemble(int address, int opcode)
			{
				return disassembleOp(address, opcode, Size.Byte);
			}
		};

		for(int ea_mode = 0; ea_mode < 8; ea_mode++)
		{
			if(ea_mode == 1)
				continue;

			for(int ea_reg = 0; ea_reg < 8; ea_reg++)
			{
				if(ea_mode == 7 && ea_reg > 1)
					break;
				is.addInstruction(base + (ea_mode << 3) + ea_reg, i);
			}
		}
	}

	protected final int nbcd(int opcode)
	{
		int mode = (opcode >> 3) & 0x07;
		int reg = (opcode & 0x07);
		Operand op = cpu.resolveDstEA(mode, reg, Size.Byte);
		int s = op.getByte();

		int x = (cpu.isFlagSet(Cpu.X_FLAG) ? 1 : 0);
		int c;

		int lo = 10 - (s & 0x0f) - x;
		if(lo < 10)
		{
			c = 1;
		}
		else
		{
			lo = 0;
			c = 0;
		}

		int hi = 10 - ((s >> 4) & 0x0f) - c;
		if(hi < 10)
		{
			c = 1;
		}
		else
		{
			c = 0;
			hi = 0;
		}

		int result = (hi << 4) + lo;

		if(c != 0)
		{
			cpu.setFlags(Cpu.X_FLAG | Cpu.C_FLAG);
		}
		else
		{
			cpu.clrFlags(Cpu.X_FLAG | Cpu.C_FLAG);
		}

		if(result != 0)
		{
			cpu.clrFlags(Cpu.Z_FLAG);
		}

		op.setByte(result);

		return (op.isRegisterMode() ? 6 : 8);
	}

	protected final DisassembledInstruction disassembleOp(int address, int opcode, Size sz)
	{
		DisassembledOperand op = cpu.disassembleDstEA(address + 2, (opcode >> 3) & 0x07, (opcode & 0x07), sz);
		return new DisassembledInstruction(address, opcode, "nbcd", op);
	}
}
