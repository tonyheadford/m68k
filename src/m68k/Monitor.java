package m68k;

import m68k.cpu.Cpu;
import m68k.cpu.DisassembledInstruction;
import m68k.cpu.Instruction;
import m68k.cpu.MC68000;
import m68k.memory.MemoryBus;
import m68k.memory.MemorySpace;

import java.io.*;

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
public class Monitor implements Runnable
{
	private final Cpu cpu;
	private final MemoryBus bus;
	private boolean running;
	private StringBuilder buffer;
	BufferedReader reader;
	PrintWriter writer;
	boolean showBytes;

	public Monitor(Cpu cpu, MemoryBus bus)
	{
		this.cpu = cpu;
		this.bus = bus;
		buffer = new StringBuilder(128);
		showBytes = false;
	}

	public static void main(String[] args)
	{
		MemoryBus bus = new MemorySpace(512);	// 512kb of memory
		Cpu cpu = new MC68000(bus);

		Monitor monitor = new Monitor(cpu,bus);
		monitor.run();
	}

	public void run()
	{
		Console con = System.console();
		if(con != null)
		{
			writer = con.writer();
			reader = new BufferedReader(con.reader());
		}
		else
		{
			writer = new PrintWriter(System.out);
			reader = new BufferedReader(new InputStreamReader(System.in));
		}
		
		running = true;
		while(running)
		{
			dumpInfo();
			try
			{
				writer.print("> ");
				writer.flush();
				handleCommand(reader.readLine());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected void handleCommand(String line)
	{
		String[] tokens = line.split(" ");
		String cmd = tokens[0].toLowerCase();

		if(cmd.equals("q"))
		{
			running = false;
		}
		else if(cmd.equals("pc"))
		{
			handlePC(tokens);
		}
		else if(cmd.equals("d"))
		{
			handleDisassemble(tokens);
		}
		else if(cmd.startsWith("d"))
		{
			handleDataRegs(tokens);
		}
		else if(cmd.startsWith("a"))
		{
			handleAddrRegs(tokens);
		}
		else if(cmd.equals("sr"))
		{
			handleSR(tokens);
		}
		else if(cmd.startsWith("ccr"))
		{
			handleCCR(tokens);
		}
//		else if(cmd.startsWith("usp"))
//		{
//			handleUSP(tokens);
//		}
//		else if(cmd.startsWith("ssp"))
//		{
//			handleSSP(tokens);
//		}
		else if(cmd.equals("ml"))
		{
			handleMemLong(tokens);
		}
		else if(cmd.equals("mw"))
		{
			handleMemWord(tokens);
		}
		else if(cmd.equals("mb"))
		{
			handleMemByte(tokens);
		}
		else if(cmd.equals("m"))
		{
			handleMemDump(tokens);
		}
		else if(cmd.equals("s"))
		{
			handleStep(tokens);
		}
		else if(cmd.equals("load"))
		{
			handleLoad(tokens);
		}
	}

	protected void handleDataRegs(String[] tokens)
	{
		String reg = tokens[0].trim();
		if(reg.length() != 2)
		{
			writer.println("Bad identifier [" + reg + "]");
			return;
		}
		int r = reg.charAt(1) - '0';
		if(r < 0 || r > 7)
		{
			writer.println("Bad identifier [" + reg + "]");
			return;
		}

		if(tokens.length == 2)
		{
			//set a value
			int value;
			try
			{
				value = parseInt(tokens[1]);
			}
			catch(NumberFormatException e)
			{
				writer.println("Bad value [" + tokens[1] + "]");
				return;
			}
			cpu.setDataRegisterLong(r, value);
		}
		else
		{
			writer.printf("D%d: %08x\n", r, cpu.getDataRegisterLong(r));
		}
	}

	protected void handleAddrRegs(String[] tokens)
	{
		String reg = tokens[0].trim();
		if(reg.length() != 2)
		{
			writer.println("Bad identifier [" + reg + "]");
			return;
		}
		int r = reg.charAt(1) - '0';
		if(r < 0 || r > 7)
		{
			writer.println("Bad identifier [" + reg + "]");
			return;
		}

		if(tokens.length == 2)
		{
			//set a value
			int value;
			try
			{
				value = parseInt(tokens[1]);
			}
			catch(NumberFormatException e)
			{
				writer.println("Bad value [" + tokens[1] + "]");
				return;
			}
			cpu.setAddrRegisterLong(r, value);
		}
		else
		{
			writer.printf("A%d: %08x\n", r, cpu.getAddrRegisterLong(r));
		}
	}

	protected void handleStep(String[] tokens)
	{
		int time = cpu.execute();
		writer.printf("[Execute took %d ticks]\n", time);
	}

	protected void handleMemDump(String[] tokens)
	{
		if(tokens.length != 2)
		{
			writer.println("usage: m <start address>");
			return;
		}
		String address = tokens[1];
		try
		{
			int addr = parseInt(address);
			StringBuilder sb = new StringBuilder(80);
			StringBuilder asc = new StringBuilder(16);

			for(int y = 0; y < 8; y++)
			{
				sb.append(String.format("%08x", addr)).append("  ");
				for(int x = 0; x < 16; x++)
				{
					int b = cpu.readMemoryByte(addr);
					sb.append(String.format("%02x ", b));
					asc.append(getPrintable(b));
					addr++;
				}
				sb.append("    ").append(asc);
				writer.println(sb.toString());
				sb.delete(0, sb.length());
				asc.delete(0, asc.length());
			}
		}
		catch(NumberFormatException e)
		{
			writer.println("Unknown address [" + address + "]");
		}
	}

	protected void handleDisassemble(String[] tokens)
	{
		int start;
		int num_instructions = 8;

		if(tokens.length > 2)
		{
			try
			{
				num_instructions = parseInt(tokens[2]);
			}
			catch(NumberFormatException e)
			{
				writer.println("Invalid instruction count: " + tokens[2]);
				return;
			}
		}

		if(tokens.length > 1)
		{
			String address = tokens[1];
			try
			{
				start = parseInt(address);
			}
			catch(NumberFormatException e)
			{
				writer.println("Unknown address [" + address + "]");
				return;
			}
		}
		else
		{
			start = cpu.getPC();
		}

		int count = 0;
		StringBuilder buffer = new StringBuilder(80);

		while(start < bus.size() && count < num_instructions)
		{
			buffer.delete(0, buffer.length());
			int opcode = cpu.readMemoryWord(start);

			Instruction i = cpu.getInstructionFor(opcode);
			DisassembledInstruction di = i.disassemble(start, opcode);
			if(showBytes)
			{
				di.formatInstruction(buffer);
			}
			else
			{
				di.shortFormat(buffer);
			}
			writer.println(buffer.toString());
			start += di.size();
			count++;
		}
	}


	protected void dumpInfo()
	{
		writer.println();
		writer.printf("D0: %08x   D4: %08x   A0: %08x   A4: %08x     PC:  %08x\n",
				cpu.getDataRegisterLong(0), cpu.getDataRegisterLong(4), cpu.getAddrRegisterLong(0), cpu.getAddrRegisterLong(4),
					cpu.getPC());
		writer.printf("D1: %08x   D5: %08x   A1: %08x   A5: %08x     SR:  %04x %s\n",
				cpu.getDataRegisterLong(1), cpu.getDataRegisterLong(5), cpu.getAddrRegisterLong(1), cpu.getAddrRegisterLong(5),
					cpu.getSR(), makeFlagView());

		writer.printf("D2: %08x   D6: %08x   A2: %08x   A6: %08x     USP: %08x\n",
				cpu.getDataRegisterLong(2), cpu.getDataRegisterLong(6), cpu.getAddrRegisterLong(2), cpu.getAddrRegisterLong(6),
					cpu.getUSP());

		writer.printf("D3: %08x   D7: %08x   A3: %08x   A7: %08x     SSP: %08x\n\n",
				cpu.getDataRegisterLong(3), cpu.getDataRegisterLong(7), cpu.getAddrRegisterLong(3), cpu.getAddrRegisterLong(7),
					cpu.getSSP());

		buffer.delete(0, buffer.length());
		int addr = cpu.getPC();
		int opcode = cpu.readMemoryWord(addr);

		Instruction i = cpu.getInstructionFor(opcode);
		DisassembledInstruction di = i.disassemble(addr, opcode);
		if(showBytes)
		{
			di.formatInstruction(buffer);
		}
		else
		{
			di.shortFormat(buffer);
		}

		writer.printf("==> %s\n\n", buffer.toString());
	}

	protected String makeFlagView()
	{
		StringBuilder sb = new StringBuilder(5);
		sb.append((cpu.isFlagSet(Cpu.X_FLAG) ? 'X' : '-'));
		sb.append((cpu.isFlagSet(Cpu.N_FLAG) ? 'N' : '-'));
		sb.append((cpu.isFlagSet(Cpu.Z_FLAG) ? 'Z' : '-'));
		sb.append((cpu.isFlagSet(Cpu.V_FLAG) ? 'V' : '-'));
		sb.append((cpu.isFlagSet(Cpu.C_FLAG) ? 'C' : '-'));
		return sb.toString();
	}

	protected char getPrintable(int val)
	{
		if(val < ' ' || val > '~')
			return '.';

		return (char)val;
	}

	protected int parseInt(String value) throws NumberFormatException
	{
		int v;

		if(value.startsWith("$"))
		{
			try
			{
				v = (int)(Long.parseLong(value.substring(1), 16) & 0x0ffffffffL);
			}
			catch(NumberFormatException e)
			{
				writer.println("Not a valid hex number [" + value + "]");
				throw e;
			}
		}
		else
		{
			try
			{
				v = (int)(Long.parseLong(value) & 0x0ffffffffL);
			}
			catch(NumberFormatException e)
			{
				writer.println("Not a valid decimal number [" + value + "]");
				throw e;
			}
		}

		return v;
	}

	protected void handlePC(String[] tokens)
	{
		if(tokens.length == 1)
		{
			writer.printf("PC: %08x\n", cpu.getPC());
		}
		else if(tokens.length == 2)
		{
			//set a value
			int value;
			try
			{
				value = parseInt(tokens[1]);
			}
			catch(NumberFormatException e)
			{
				writer.println("Bad value [" + tokens[1] + "]");
				return;
			}
			cpu.setPC(value);
		}
		else
		{
			writer.println("usage: " + tokens[0] + " [value]");
		}
	}
	
	protected void handleSR(String[] tokens)
	{
		if(tokens.length == 1)
		{
			writer.printf("SR: %04x\n", cpu.getSR());
		}
		else if(tokens.length == 2)
		{
			//set a value
			int value;
			try
			{
				value = parseInt(tokens[1]);
			}
			catch(NumberFormatException e)
			{
				writer.println("Bad value [" + tokens[1] + "]");
				return;
			}
			cpu.setSR(value);
		}
		else
		{
			writer.println("usage: " + tokens[0] + " [value]");
		}
	}

	protected void handleCCR(String[] tokens)
	{
		if(tokens.length == 1)
		{
			writer.printf("CCR: %02x  %s\n", cpu.getCCRegister(), makeFlagView());
		}
		else if(tokens.length == 2)
		{
			//set a value
			int value;
			try
			{
				value = parseInt(tokens[1]);
			}
			catch(NumberFormatException e)
			{
				writer.println("Bad value [" + tokens[1] + "]");
				return;
			}
			cpu.setCCRegister(value);
		}
		else
		{
			writer.println("usage: " + tokens[0] + " [value]");
		}
	}

	protected void handleMemLong(String[] tokens)
	{
		if(tokens.length != 2 && tokens.length != 3)
		{
			writer.println("usage: ml <address> [value]");
		}
		else
		{
			String address = tokens[1];

			if(tokens.length == 2)
			{
				// just read address
				try
				{
					int addr = parseInt(address);
					writer.printf("%08x  %08x\n", addr, cpu.readMemoryLong(addr));
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				//write address
				String value = tokens[2];
				try
				{
					int addr = parseInt(address);
					int v = parseInt(value);
					cpu.writeMemoryLong(addr, v);
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	protected void handleMemWord(String[] tokens)
	{
		if(tokens.length != 2 && tokens.length != 3)
		{
			writer.println("usage: mw <address> [value]");
		}
		else
		{
			String address = tokens[1];

			if(tokens.length == 2)
			{
				// just read address
				try
				{
					int addr = parseInt(address);
					writer.printf("%08x  %04x\n", addr, cpu.readMemoryWord(addr));
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				//write address
				String value = tokens[2];
				try
				{
					int addr = parseInt(address);
					int v = parseInt(value);
					cpu.writeMemoryWord(addr, v);
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	protected void handleMemByte(String[] tokens)
	{
		if(tokens.length != 2 && tokens.length != 3)
		{
			writer.println("usage: mb <address> [value]");
		}
		else
		{
			String address = tokens[1];

			if(tokens.length == 2)
			{
				// just read address
				try
				{
					int addr = parseInt(address);
					writer.printf("%08x  %02x\n", addr, cpu.readMemoryByte(addr));
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				//write address
				String value = tokens[2];
				try
				{
					int addr = parseInt(address);
					int v = parseInt(value);
					cpu.writeMemoryByte(addr, v);
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	protected void handleLoad(String[] tokens)
	{
		// load address file
		if(tokens.length != 3)
		{
			writer.println("usage: load <address> <file>");
			return;
		}

		int address;
		try
		{
			address = parseInt(tokens[1]);
		}
		catch(NumberFormatException e)
		{
			writer.println("Invalid address specified [" + tokens[1] + "]");
			return;
		}

		File f = new File(tokens[2]);
		if(!f.exists())
		{
			writer.println("Cannot find file [" + tokens[2] + "]");
			return;
		}

		if(address + (int)f.length() > bus.size())
		{
			//need larger memory
			writer.println("Need larger memory to load this file at " + tokens[1]);
			return;
		}
		try
		{
			FileInputStream fis = new FileInputStream(f);
			byte[] buffer = new byte[(int)f.length()];
			int len = fis.read(buffer);
			fis.close();
			for(int n = 0; n < len; n++)
			{
				bus.writeByte(address, buffer[n]);
				address++;
			}
//			cpu.reset();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
