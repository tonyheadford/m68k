package m68k.cpu.instructions;

import m68k.cpu.*;
import m68k.cpu.operand.Operand;
import m68k.cpu.operand.OperandTiming;
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

public class DIVU implements InstructionHandler
{
	public static boolean ACCURATE_DIV_TIMING;

	protected final Cpu cpu;

	public DIVU(Cpu cpu)
	{
		this.cpu = cpu;
	}

	public void register(InstructionSet is)
	{
		int base = 0x80c0;
		Instruction i = new Instruction() {
			public int execute(int opcode)
			{
				return divu(opcode);
			}
			public DisassembledInstruction disassemble(int address, int opcode)
			{
				return disassembleOp(address, opcode, Size.Word);
			}
		};

		for(int ea_mode = 0; ea_mode < 8; ea_mode++)
		{
			if(ea_mode == 1)
				continue;

			for(int ea_reg = 0; ea_reg < 8; ea_reg++)
			{
				if(ea_mode == 7 && ea_reg > 4)
					break;

				for(int r = 0; r < 8; r++)
				{
					is.addInstruction(base + (r << 9) + (ea_mode << 3) + ea_reg, i);
				}
			}
		}
	}

	protected final int divu(int opcode)
	{
		Operand op = cpu.resolveSrcEA((opcode >> 3) & 0x07, (opcode & 0x07), Size.Word);

		int s = op.getWord();
		int reg = (opcode >> 9) & 0x07;
		int d = cpu.getDataRegisterLong(reg);
		int time;

		if(s == 0)
		{
			//divide by zero exception
			cpu.raiseException(5);
			time = 38;
		}
		else
		{
			long dl = d & 0x0ffffffffL;

			long quot = dl / (long)s;

			if(quot > 0x0000ffffL)
			{
				//Overflow
				cpu.setFlags(Cpu.V_FLAG);
				cpu.setFlags(Cpu.N_FLAG);   // the n flag seems to be set every time an overflow is generated
				cpu.clrFlags(Cpu.C_FLAG);
			}
			else
			{
				int remain = (int)((dl % s) & 0xffff);
				int result = ((int)quot & 0x0000ffff) | (remain << 16);
				cpu.setDataRegisterLong(reg, result);

				if((quot & 0x8000) != 0)
				{
					cpu.setFlags(Cpu.N_FLAG);
				}
				else
				{
					cpu.clrFlags(Cpu.N_FLAG);
				}

				if(quot == 0)
				{
					cpu.setFlags(Cpu.Z_FLAG);
				}
				else
				{
					cpu.clrFlags(Cpu.Z_FLAG);
				}

				cpu.clrFlags((Cpu.V_FLAG | Cpu.C_FLAG));
			}
			//worst case but less than 10% difference between best and worst cases
			time = ACCURATE_DIV_TIMING ? getDivu68kCycles(dl, s) : 140;
		}

		return time + OperandTiming.getOperandTiming(op, Size.Word);
	}

/*
 * Compute exact number of CPU cycles taken
 * by DIVU and DIVS on a 68000 processor.
 *
 * Copyright (c) 2005 by Jorge Cwik, pasti@fxatari.com
 */
private static int getDivu68kCycles(long dividend, int divisor) {
	int mcycles;
	long hdivisor;
	int i;

	if (divisor == 0)
		return 0;

	// Overflow
	if ((dividend >> 16) >= divisor)
		return (mcycles = 5) * 2;

	mcycles = 38;
	hdivisor = ((long) divisor) << 16;

	for (i = 0; i < 15; i++) {
		long temp;
		temp = dividend;

		dividend <<= 1;

		// If carry from shift
		if (temp < 0) {
			dividend -= hdivisor;
		} else {
			mcycles += 2;
			if (dividend >= hdivisor) {
				dividend -= hdivisor;
				mcycles--;
			}
		}
	}

	return mcycles * 2;
}

	protected final DisassembledInstruction disassembleOp(int address, int opcode, Size sz)
	{
		DisassembledOperand src = cpu.disassembleSrcEA(address + 2, (opcode >> 3) & 0x07, (opcode & 0x07), sz);
		DisassembledOperand dst = new DisassembledOperand("d" + ((opcode >> 9) & 0x07));

		return new DisassembledInstruction(address, opcode, "divu", src, dst);
	}
}
