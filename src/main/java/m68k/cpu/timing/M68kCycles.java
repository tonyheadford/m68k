package m68k.cpu.timing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class M68kCycles {
    private static final int NUM_OPCODES = 0x10_000;
    public static final int[] m68k_cycles;

    /**
     * NOTE, timings file taken from Genesis Plus Gx, which in turns is based on yacht.txt
     * The agreement seems to be that yacht.txt is more accurate than MC68000UM.pdf
     * https://github.com/larsbrinkhoff/m68k-microcode/blob/master/doc/Yacht.txt
     */
    private static final String fileLoc = "res";
    private static final String fileName = "m68kCycles.dat";

    static {
        Path p = Paths.get(fileLoc, fileName);
        long ns = System.nanoTime();
        int[] data = new int[0];
        try {
            List<String> l = Files.readAllLines(p);
            assert l != null && l.size() > 0;
            data = l.stream().filter(st -> !st.startsWith("#")).flatMap(st -> Arrays.stream(st.split(","))).
                    mapToInt(st -> Integer.parseInt(st.trim())).toArray();
            assert data.length == NUM_OPCODES;
            System.out.printf("M68kCycle table loaded in: %d ms", Duration.ofNanos(System.nanoTime() - ns).toMillis());
        } catch (Exception e){
            System.err.println("Unable to load cycle table from: " + p.toAbsolutePath());
            e.printStackTrace();
        }
        m68k_cycles = data;
    }

    public static final int getTimingByOpcode(int opcode) {
        return m68k_cycles[opcode];
    }
}