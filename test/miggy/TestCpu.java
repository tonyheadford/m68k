package miggy;

import m68k.cpu.MC68000;
import m68k.cpu.Size;
import miggy.memory.TestMem;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class TestCpu extends MC68000 {

    public TestCpu(TestMem mem) {
        super();
        this.memory = mem;
    }

    public void push(int value, Size size) {
        switch (size) {
            case Byte:
                throw new IllegalStateException("Not implemented");
            case Word:
                pushWord(value);
                break;
            case Long:
                pushLong(value);
                break;
        }
    }

    public int fetch(Size size) {
        switch (size) {
            //fetch will always read 2 bytes if Size.Byte
            case Byte:
                //fall-through
            case Word:
                return fetchPCWord();
            case Long:
                return fetchPCLong();
        }
        return super.fetchPCLong();
    }

    public boolean isSet(int flag) {
        return super.isFlagSet(flag);
    }

    public void setCCR(int value) {
        super.setCCRegister(value);
    }

    public int getAddrRegister(int reg) {
        return super.getAddrRegisterLong(reg);
    }

    public int getDataRegister(int reg) {
        return super.getDataRegisterLong(reg);
    }

    public void setDataRegister(int reg, int value, Size size) {
        switch (size) {
            case Byte:
                setDataRegisterByte(reg, value);
                break;
            case Word:
                setDataRegisterWord(reg, value);
                break;
            case Long:
                setDataRegisterLong(reg, value);
                break;
        }

    }

    public void setDataRegister(int reg, int value) {
        Size size = getSize(value);
        setDataRegister(reg, value, size);
    }

    public void setAddrRegister(int reg, int value, Size size) {
        switch (size) {
            case Byte:
                setAddrRegisterByte(reg, value);
                break;
            case Word:
                setAddrRegisterWord(reg, value);
                break;
            case Long:
                setAddrRegisterLong(reg, value);
                break;
        }
    }

    public void setAddrRegister(int reg, int value) {
        Size size = getSize(value);
        setAddrRegister(reg, value, size);
    }


    private static Size getSize(int val) {
        if ((val & 0xFFFF) != val) {
            return Size.Long;
        }
        if ((val & 0xFF) != val) {
            return Size.Word;
        }
        return Size.Byte;
    }
}
