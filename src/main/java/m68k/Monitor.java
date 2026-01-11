package m68k;

import m68k.cpu.Cpu;
import m68k.cpu.DisassembledInstruction;
import m68k.cpu.Instruction;
import m68k.cpu.MC68000;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;

import java.io.*;
import java.util.ArrayList;

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
	private final AddressSpace memory;
	private boolean running;
	private StringBuilder buffer;
	private BufferedReader reader;
	private PrintWriter writer;
	private boolean showBytes;
	private boolean autoRegs;
	private ArrayList<Integer> breakpoints;

	public Monitor(Cpu cpu, AddressSpace memory)
	{
		this.cpu = cpu;
		this.memory = memory;
		buffer = new StringBuilder(128);
		showBytes = false;
		autoRegs = false;
		breakpoints = new ArrayList<Integer>();
	}

	public static void main(String[] args)
	{
		//TODO: commandline args could be expanded in future to include CPU type etc.

		int mem_size = 512;	// 512kb of memory default

		if(args.length == 1)
		{
			// initial memory size
			try
			{
				mem_size = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e)
			{
				System.err.println("Invalid number: " + args[0]);
				System.out.println("Usage: m68k.Monitor [memory size Kb]");
				System.exit(-1);
			}
		}

		System.out.println("m68k Monitor v0.1 - Copyright 2008-2010 Tony Headford");

		AddressSpace memory = new MemorySpace(mem_size);
		Cpu cpu = new MC68000();
		cpu.setAddressSpace(memory);
		cpu.reset();	//init cpu

		Monitor monitor = new Monitor(cpu,memory);
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
			try
			{
				writer.print("> ");
				writer.flush();
				handleCommand(reader.readLine());

				if(autoRegs)
				{
					dumpInfo();
				}
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
		String cmd = tokens[0].trim().toLowerCase();

		if(cmd.length() > 0)
		{
			if(cmd.equals("q"))
			{
				running = false;
			}
			else if(cmd.equals("r"))
			{
				dumpInfo();
			}
			else if(cmd.equals("pc"))
			{
				handlePC(tokens);
			}
			else if(cmd.equals("d"))
			{
				handleDisassemble(tokens);
			}
			else if(cmd.equals("b"))
			{
				handleBreakPoints(tokens);
			}
			else if(cmd.equals("sr"))
			{
				handleSR(tokens);
			}
			else if(cmd.equals("ccr"))
			{
				handleCCR(tokens);
			}
			else if(cmd.equals("usp"))
			{
				handleUSP(tokens);
			}
			else if(cmd.equals("ssp"))
			{
				handleSSP(tokens);
			}
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
			else if(cmd.equals("g"))
			{
				handleGo(tokens);
			}
			else if(cmd.equals("autoregs"))
			{
				handleAutoRegs(tokens);
			}
			else if(cmd.equals("showbytes"))
			{
				handleShowBytes(tokens);
			}
			else if(cmd.equals("load"))
			{
				handleLoad(tokens);
			}
			else if(cmd.startsWith("d"))
			{
				handleDataRegs(tokens);
			}
			else if(cmd.startsWith("a"))
			{
				handleAddrRegs(tokens);
			}
			else if(cmd.equals("?") || cmd.equals("h") || cmd.equals("help"))
			{
				showHelp(tokens);
			}
			else
			{
				writer.println("Unknown command: " + tokens[0]);
			}
		}
	}

	protected void handleGo(String[] tokens)
	{
		int count = 0;
		boolean going = true;

		while(running && going)
		{
			try
			{
				int time = cpu.execute();
				count += time;
				int addr = cpu.getPC();
				if(breakpoints.contains(addr))
				{
					//time to stop
					writer.println("BREAKPOINT");
					going = false;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				going = false;
			}

		}
		writer.printf("[Consumed %d ticks]\n", count);
	}

	protected void handleBreakPoints(String[] tokens)
	{
		if(tokens.length > 1)
		{
			// add or remove toggle
			try
			{
				int addr = parseInt(tokens[1]);
				if(breakpoints.contains(addr))
					breakpoints.remove(new Integer(addr));
				else
					breakpoints.add(addr);
			}
			catch(NumberFormatException e)
			{
				return;
			}
		}

		//list breakpoints
		writer.println("Breakpoints:");
		for(int bp : breakpoints)
		{
			writer.println(String.format("$%x", bp));
		}
	}

	protected void handleAutoRegs(String[] tokens)
	{
		if(tokens.length > 1)
		{
			if(tokens[1].equalsIgnoreCase("on"))
			{
				autoRegs = true;
			}
			else if(tokens[1].equalsIgnoreCase("off"))
			{
				autoRegs = false;
			}
		}

		writer.println("autoregs is " + (autoRegs ? "on" : "off"));
	}

	protected void handleShowBytes(String[] tokens)
	{
		if(tokens.length > 1)
		{
			if(tokens[1].equalsIgnoreCase("on"))
			{
				showBytes = true;
			}
			else if(tokens[1].equalsIgnoreCase("off"))
			{
				showBytes = false;
			}
		}

		writer.println("showbytes is " + (showBytes ? "on" : "off"));
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
		int size = memory.size();
		try
		{
			int addr = parseInt(address);
			if(addr < 0 || addr >= size)
			{
				writer.println("Address out of range");
				return;
			}
			StringBuilder sb = new StringBuilder(80);
			StringBuilder asc = new StringBuilder(16);

			for(int y = 0; y < 8 && addr < size; y++)
			{
				sb.append(String.format("%08x", addr)).append("  ");
				for(int x = 0; x < 16 && addr < size; x++)
				{
					int b = cpu.readMemoryByte(addr);
					sb.append(String.format("%02x ", b));
					asc.append(getPrintable(b));
					addr++;
				}
				if(sb.length() < 48)
				{
					for(int n = sb.length(); n < 48; n++)
						sb.append(" ");
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

		while(start < memory.size() && count < num_instructions)
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
		if(addr < 0 || addr >= memory.size())
		{
			buffer.append(String.format("%08x   ????", addr));
		}
		else
		{
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

	protected void handleUSP(String[] tokens)
	{
		if(tokens.length == 1)
		{
			writer.printf("USP: %08x\n", cpu.getUSP());
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
			cpu.setUSP(value);
		}
		else
		{
			writer.println("usage: " + tokens[0] + " [value]");
		}
	}

	protected void handleSSP(String[] tokens)
	{
		if(tokens.length == 1)
		{
			writer.printf("SSP: %08x\n", cpu.getSSP());
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
			cpu.setSSP(value);
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
					if(addr < 0 || addr >= memory.size())
					{
						writer.println("Address out of range");
						return;
					}
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
					if(addr < 0 || addr >= memory.size())
					{
						writer.println("Address out of range");
						return;
					}
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
					if(addr < 0 || addr >= memory.size())
					{
						writer.println("Address out of range");
						return;
					}
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
					if(addr < 0 || addr >= memory.size())
					{
						writer.println("Address out of range");
						return;
					}
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
					if(addr < 0 || addr >= memory.size())
					{
						writer.println("Address out of range");
					}
					else
					{
						writer.printf("%08x  %02x\n", addr, cpu.readMemoryByte(addr));
					}
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
					if(addr < 0 || addr >= memory.size())
					{
						writer.println("Address out of range");
						return;
					}
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

		if(address + (int)f.length() >= memory.size())
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
				memory.writeByte(address, buffer[n]);
				address++;
			}
//			cpu.reset();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	protected void showHelp(String[] tokens)
	{
		writer.println("Command Help:");
		writer.println("Addresses and values can be specified in hexadecimal by preceeding the value with '$'");
		writer.println("      eg. d0 $deadbeef  - Set register d0 to 0xDEADBEEF");
		writer.println("          m $10         - Memory dump starting at 0x10 (16 in decimal)");
		writer.println("          pc 10         - Set the PC register to 10 (0x0A in hexadecimal)");
		writer.println("General:");
		writer.println("  ?,h,help              - Show this help.");
		writer.println("  q                     - Quit.");
		writer.println("Registers:");
		writer.println("  r                     - Display all registers");
		writer.println("  d[0-9] [value]        - Set or view a data register");
		writer.println("  a[0-9] [value]        - Set or view an address register");
		writer.println("  pc [value]            - Set or view the PC register");
		writer.println("  sr [value]            - Set or view the SR register");
		writer.println("  ccr [value]           - Set or view the CCR register");
		writer.println("  usp [value]           - Set or view the USP register");
		writer.println("  ssp [value]           - Set or view the SSP register");
		writer.println("Memory:");
		writer.println("  m <address>           - View (128 byte) memory dump starting at the specified address");
		writer.println("  mb <address> [value]  - Set or view a byte (8-bit) value at the specified address");
		writer.println("  mw <address> [value]  - Set or view a word (16-bit) value at the specified address");
		writer.println("  ml <address> [value]  - Set or view a long (32-bit) value at the specified address");
		writer.println("  load <address> <file> - Load <file> into memory starting at <address>");
		writer.println("Execution & Disassembly:");
		writer.println("  s                     - Execute the instruction at the PC register");
		writer.println("  d <address> [count]   - Disassemble the memory starting at <address> for an optional");
		writer.println("                          <count> instructions. Default is 8 instructions.");
	}
}
