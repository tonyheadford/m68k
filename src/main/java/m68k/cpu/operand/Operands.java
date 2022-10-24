package m68k.cpu.operand;

import m68k.cpu.Cpu;
import m68k.cpu.CpuUtils;
import m68k.cpu.Size;

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
/**
 * refactor and adjust timings
 * @author Federico Berti
 */
public class Operands {

    public static class DataRegisterOperand extends AbstractOperand
    {
        protected int regNumber;

        public DataRegisterOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            
            regNumber = param;
        }

        public int getByte()
        {
            return cpu.getDataRegisterByte(regNumber);
        }

        public int getWord()
        {
            return cpu.getDataRegisterWord(regNumber);
        }

        public int getLong()
        {
            return cpu.getDataRegisterLong(regNumber);
        }

        public int getByteSigned()
        {
            return cpu.getDataRegisterByteSigned(regNumber);
        }

        public int getWordSigned()
        {
            return cpu.getDataRegisterWordSigned(regNumber);
        }

        public void setByte(int value)
        {
            cpu.setDataRegisterByte(regNumber, value);
        }

        public void setWord(int value)
        {
            cpu.setDataRegisterWord(regNumber, value);
        }

        public void setLong(int value)
        {
            cpu.setDataRegisterLong(regNumber, value);
        }

        public boolean isRegisterMode()
        {
            return true;
        }

        public boolean isSR()
        {
            return false;
        }
        // used for jmp and jsr
        public int getComputedAddress()
        {
            throw new RuntimeException("Data Register has no computed address");
        }

        public String toString()
        {
            return "d" + regNumber;
        }
    }

    public static class AddressRegisterOperand extends AbstractOperand
    {
        protected int regNumber;

        public AddressRegisterOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            
            regNumber = param;
        }

        public int getByte()
        {
            return cpu.getAddrRegisterByte(regNumber);
        }

        public int getWord()
        {
            return cpu.getAddrRegisterWord(regNumber);
        }

        public int getLong()
        {
            return cpu.getAddrRegisterLong(regNumber);
        }

        public int getByteSigned()
        {
            return cpu.getAddrRegisterByteSigned(regNumber);
        }

        public int getWordSigned()
        {
            return cpu.getAddrRegisterWordSigned(regNumber);
        }

        public void setByte(int value)
        {
            cpu.setAddrRegisterByte(regNumber, value);
        }

        public void setWord(int value)
        {
            cpu.setAddrRegisterWord(regNumber, value);
        }

        public void setLong(int value)
        {
            cpu.setAddrRegisterLong(regNumber, value);
        }

        public boolean isRegisterMode()
        {
            return true;
        }

        public boolean isSR()
        {
            return false;
        }
        // used for jmp and jsr
        public int getComputedAddress()
        {
            throw new RuntimeException("Address Register direct has no computed address");
        }

        public String toString()
        {
            return new StringBuilder(2).append("a").append(regNumber).toString();
        }

    }

    public static class AddressRegisterIndirectOperand extends AbstractOperand
    {
        protected int regNumber;
        protected int address;

        public AddressRegisterIndirectOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            
            regNumber = param;            
            address = cpu.getAddrRegisterLong(regNumber);
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            return new StringBuilder(4).append("(a").append(regNumber).append(")").toString();
        }
    }

    public static class AddressRegisterPostIncOperand extends AbstractOperand
    {
        protected int regNumber;
        protected int address;

        public AddressRegisterPostIncOperand(Cpu cpu) {
            super(cpu);
        }


        public void init(int param, Size sz)
        {
            
            regNumber = param;
            address = cpu.getAddrRegisterLong(regNumber);

            // in the 68008 At LEAST, moving bytes to the stack will change the stack pointer by 2, not 1
            if (param == 7 && sz.byteCount() == 1)
                cpu.incrementAddrRegister(regNumber, 2);
            else
                cpu.incrementAddrRegister(regNumber, sz.byteCount());
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }
        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString() {
            return new StringBuilder(5).append("(a").append(regNumber).append(")+").toString();
        }
    }

    public static class AddressRegisterPreDecOperand extends AbstractOperand {
        protected int regNumber;
        protected int address;

        public AddressRegisterPreDecOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz) {
            
            regNumber = param;

            // in the 68008 At LEAST, moving bytes to the stack will change the stack pointer by 2, not 1
            if (param == 7 && sz.byteCount() == 1)
                cpu.decrementAddrRegister(regNumber, 2);
            else
                cpu.decrementAddrRegister(regNumber, sz.byteCount());

            address = cpu.getAddrRegisterLong(regNumber);
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value) {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value) {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value) {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isSR() {
            return false;
        }

        public boolean isRegisterMode() {
            return false;
        }

        // used for jmp and jsr, lea, movem
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            return new StringBuilder(5).append("-(a").append(regNumber).append(")").toString();
        }
    }

    public static class AddressRegisterWithDisplacementOperand extends AbstractOperand
    {
        protected int regNumber;
        protected int address;
        protected int displacement;

        public AddressRegisterWithDisplacementOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            
            regNumber = param;
            displacement = cpu.fetchPCWordSigned();
            address = cpu.getAddrRegisterLong(regNumber) + displacement;
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            return new StringBuilder(10).append(String.format("$%x",displacement)).append("(a").append(regNumber).append(")").toString();
        }
    }

    public static class AddressRegisterWithIndexOperand extends AbstractOperand
    {
        protected int regNumber;
        protected int address;
        protected int displacement;
        protected int idxRegNumber;
        protected Size idxSize;
        protected boolean idxIsAddressReg;

        public AddressRegisterWithIndexOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            
            regNumber = param;
            int ext = cpu.fetchPCWordSigned();
            displacement = CpuUtils.signExtendByte(ext);
            idxRegNumber = (ext >> 12) & 0x07;
            idxSize = ((ext & 0x0800) == 0x0800 ? Size.Long : Size.Word);
            idxIsAddressReg = ((ext & 0x8000) == 0x8000);
            int idxVal;
            if(idxIsAddressReg)
            {
                if(idxSize == Size.Word)
                {
                    idxVal = cpu.getAddrRegisterWordSigned(idxRegNumber);
                }
                else
                {
                    idxVal = cpu.getAddrRegisterLong(idxRegNumber);
                }
            }
            else
            {
                if(idxSize == Size.Word)
                {
                    idxVal = cpu.getDataRegisterWordSigned(idxRegNumber);
                }
                else
                {
                    idxVal = cpu.getDataRegisterLong(idxRegNumber);
                }
            }
            address = cpu.getAddrRegisterLong(regNumber) + displacement + idxVal;
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isSR()
        {
            return false;
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder(20);
            sb.append(displacement).append("(a").append(regNumber).append(",");
            if(idxIsAddressReg)
            {
                sb.append("a");
            }
            else
            {
                sb.append("d");
            }
            sb.append(idxRegNumber).append(idxSize.ext()).append(")");
            return sb.toString();
        }
    }

    public static class AbsoluteShortOperand extends AbstractOperand
    {
        
        protected int address;
        protected final int index = 7;

        public AbsoluteShortOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            //don't need the param
            
            // yes it is sign extended (the high byte is discarded in 68000 addressing)
            address = cpu.fetchPCWordSigned();
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            return new StringBuilder(12).append("$").append(Integer.toHexString(address)).append(".w").toString();
        }
    }

    public static class AbsoluteLongOperand extends AbstractOperand
    {
        
        protected int address;

        public AbsoluteLongOperand(Cpu cpu) {
            super(cpu);
        }


        public void init(int param, Size sz)
        {
            //don't need the param
            
            address = cpu.fetchPCLong();
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            return new StringBuilder(12).append("$").append(Integer.toHexString(address)).append(".l").toString();
        }
    }

    public static class PCWithDisplacementOperand extends AbstractOperand
    {
        
        protected int address;
        protected int displacement;

        public PCWithDisplacementOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            // param not used
            
            //the address of pc before displacement read is used
            address = cpu.getPC();
            displacement = cpu.fetchPCWordSigned();
            address += displacement;
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            return new StringBuilder(12).append(displacement).append("(pc)").toString();
        }
    }

    public static class PCWithIndexOperand extends AbstractOperand
    {
        
        protected int address;
        protected int displacement;
        protected int idxRegNumber;
        protected Size idxSize;
        protected boolean idxIsAddressReg;

        public PCWithIndexOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            // param not used
            
            // get pc before it's incremented by the cpu.fetch
            address = cpu.getPC();
            int ext = cpu.fetchPCWordSigned();
            displacement = CpuUtils.signExtendByte(ext);
            idxRegNumber = (ext >> 12) & 0x07;
            idxSize = ((ext & 0x0800) == 0x0800 ? Size.Long : Size.Word);
            idxIsAddressReg = ((ext & 0x8000) == 0x8000);
            int idxVal;
            if(idxIsAddressReg)
            {
                if(idxSize == Size.Word)
                {
                    idxVal = cpu.getAddrRegisterWordSigned(idxRegNumber);
                }
                else
                {
                    idxVal = cpu.getAddrRegisterLong(idxRegNumber);
                }
            }
            else
            {
                if(idxSize == Size.Word)
                {
                    idxVal = cpu.getDataRegisterWordSigned(idxRegNumber);
                }
                else
                {
                    idxVal = cpu.getDataRegisterLong(idxRegNumber);
                }
            }
            address += displacement + idxVal;
        }

        public int getByte()
        {
            return cpu.readMemoryByte(address);
        }

        public int getWord()
        {
            return cpu.readMemoryWord(address);
        }

        public int getLong()
        {
            return cpu.readMemoryLong(address);
        }

        public int getByteSigned()
        {
            return cpu.readMemoryByteSigned(address);
        }

        public int getWordSigned()
        {
            return cpu.readMemoryWordSigned(address);
        }

        public void setByte(int value)
        {
            cpu.writeMemoryByte(address, value);
        }

        public void setWord(int value)
        {
            cpu.writeMemoryWord(address, value);
        }

        public void setLong(int value)
        {
            cpu.writeMemoryLong(address, value);
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            return address;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder(20);
            sb.append(displacement).append("(pc,");
            if(idxIsAddressReg)
            {
                sb.append("a");
            }
            else
            {
                sb.append("d");
            }
            sb.append(idxRegNumber).append(idxSize.ext()).append(")");
            return sb.toString();
        }
    }

    public static class ImmediateOperand extends AbstractOperand
    {
        
        protected int value;
        protected final int index = 11;

        public ImmediateOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            // param not used
            
            if(sz == Size.Long)
            {
                value = cpu.fetchPCLong();
            }
            else
            {
                // byte & word
                value = cpu.fetchPCWord();
                if(sz == Size.Byte)
                    value &= 0x00ff;
            }
        }

        public int getByte()
        {
            return value & 0x00ff;
        }

        public int getWord()
        {
            return value & 0x0000ffff;
        }

        public int getLong()
        {
            return value;
        }

        public int getByteSigned()
        {
            return CpuUtils.signExtendByte(value);
        }

        public int getWordSigned()
        {
            return CpuUtils.signExtendWord(value);
        }

        public void setByte(int value)
        {
            // should never be called
            throw new RuntimeException("Cannot setByte on source only operand");
        }

        public void setWord(int value)
        {
            // should never be called
            throw new RuntimeException("Cannot setWord on source only operand");
        }

        public void setLong(int value)
        {
            // should never be called
            throw new RuntimeException("Cannot setLong on source only operand");
        }

        public boolean isRegisterMode()
        {
            return false;
        }

        public boolean isSR()
        {
            return false;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            throw new RuntimeException("Immediate addressing has no computed address");
        }

        public String toString()
        {
            return new StringBuilder(12).append("#$").append(Integer.toHexString(value)).toString();
        }
    }

    public static class StatusRegisterOperand extends AbstractOperand
    {
        protected int value;
        protected String name;

        public StatusRegisterOperand(Cpu cpu) {
            super(cpu);
        }

        public void init(int param, Size sz)
        {
            // param not used
            
            switch(sz)
            {
                case Byte:
                {
                    //condition codes - not privileged
                    value = cpu.getCCRegister();
                    name = "ccr";
                    break;
                }
                case Word:
                {
                    //status register - priviledged!
                    value = cpu.getSR();
                    name = "sr";
                    break;
                }
                default:
                {
                    throw new IllegalArgumentException("Status Register is byte or word access only");
                }
            }
        }

        public int getByte()
        {
            return value & 0x00ff;
        }

        public int getWord()
        {
            return value & 0x0000ffff;
        }

        public int getLong()
        {
            // should never be called
            throw new RuntimeException("Cannot getLong on status register");
        }

        public int getByteSigned()
        {
            return CpuUtils.signExtendByte(value);
        }

        public int getWordSigned()
        {
            return CpuUtils.signExtendWord(value);
        }

        public void setByte(int value)
        {
            cpu.setCCRegister(value);
        }

        public void setWord(int value)
        {
            cpu.setSR(value);
        }

        public void setLong(int value)
        {
            // should never be called
            throw new RuntimeException("Cannot setLong on status register");
        }

        public boolean isRegisterMode()
        {
            return true;
        }

        public boolean isSR()
        {
            return true;
        }

        // used for jmp and jsr
        public int getComputedAddress()
        {
            throw new RuntimeException("Status Register has no computed address");
        }

        public String toString()
        {
            return name;
        }
    }

}
