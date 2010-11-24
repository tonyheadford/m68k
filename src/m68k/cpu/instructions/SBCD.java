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
public class SBCD implements InstructionHandler
{
	protected final Cpu cpu;

	public SBCD(Cpu cpu)
	{
		this.cpu = cpu;
	}

	public final void register(InstructionSet is)
	{
		int base;
		Instruction i;

		for(int f = 0; f < 2; f++)
		{
			if(f == 0)
			{
				// data reg mode
				base = 0x8100;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return sbcd_dr(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, true);
					}
				};
			}
			else
			{
				// addr reg mode
				base = 0x8108;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return sbcd_ar(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, false);
					}
				};
			}

			for(int d = 0; d < 8; d++)
			{
				for(int s = 0; s < 8; s++)
				{
					is.addInstruction(base + (d << 9) + s, i);
				}
			}
		}
	}

	protected final int sbcd_dr(int opcode)
	{
		int sreg = (opcode & 0x07);
		int dreg = (opcode >> 9) & 0x07;
		int s = cpu.getDataRegisterByte(sreg);
		int d = cpu.getDataRegisterByte(dreg);

		int result = doCalc(s, d);
		cpu.setDataRegisterByte(dreg, result);
		return 6;
	}

	protected final int sbcd_ar(int opcode)
	{
		int sreg = (opcode & 0x07);
		int dreg = (opcode >> 9) & 0x07;
		cpu.decrementAddrRegister(sreg, 1);
		cpu.decrementAddrRegister(dreg, 1);
		int s = cpu.readMemoryByte(cpu.getAddrRegisterLong(sreg));
		int d = cpu.readMemoryByte(cpu.getAddrRegisterLong(dreg));

		int result = doCalc(s, d);
		cpu.writeMemoryByte(cpu.getAddrRegisterLong(dreg), result);
		return 18;
	}

	protected final int doCalc(int s, int d)
	{
		int x = (cpu.isFlagSet(Cpu.X_FLAG) ? 1 : 0);
		int c;

		int lo = (d & 0x0f) - (s & 0x0f) - x;
		if(lo < 0)
		{
			lo += 10;
			c = 1;
		}
		else
		{
			c = 0;
		}

		int hi = ((d >> 4) & 0x0f) - ((s >> 4) & 0x0f) - c;
		if(hi < 0)
		{
			hi += 10;
			c = 1;
		}
		else
		{
			c = 0;
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

		return result;
	}

	protected final DisassembledInstruction disassembleOp(int address, int opcode, boolean data_reg_mode)
	{
		DisassembledOperand src;
		DisassembledOperand dst;

		if(data_reg_mode)
		{
			src = new DisassembledOperand("d" + (opcode & 0x07));
			dst = new DisassembledOperand("d" + ((opcode >> 9) & 0x07));
		}
		else
		{
			src = new DisassembledOperand("-(a" + (opcode & 0x07) + ")");
			dst = new DisassembledOperand("-(a" + ((opcode >> 9) & 0x07) + ")");
		}
		return new DisassembledInstruction(address, opcode, "sbcd", src, dst);
	}
}
