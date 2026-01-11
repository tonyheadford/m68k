package miggy;

import m68k.cpu.CpuCore;
import miggy.memory.TestMem;

/**
 * ${FILE}
 * <p>
 * Federico Berti
 * <p>
 * Copyright 2019
 */
public class SystemModel {

    public static TestCpu CPU;
    public static TestMem MEM;

    public static class CpuFlag {
        public static final int C = CpuCore.C_FLAG;
        public static final int V = CpuCore.V_FLAG;
        public static final int Z = CpuCore.Z_FLAG;
        public static final int N = CpuCore.N_FLAG;
        public static final int X = CpuCore.X_FLAG;
    }
}
