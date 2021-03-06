package net.akaritakai.aoc2020;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * In Day 14, we are given a machine with a mask instruction and a write instruction. The mask instruction sets a mask
 * to be applied a certain way to the write instruction, and the write instruction writes a given value into a given
 * memory location in a certain way.
 *
 * In part 1, we are told how to mutate the value given the mask before setting it in memory.
 * In part 2, we are given a way to turn the mask and location into multiple write instructions that we then carry out.
 *
 * Both parts are made simpler by a strong knowledge of bitwise operators.
 */
public class Puzzle14 extends AbstractPuzzle {
    public Puzzle14(String puzzleInput) {
        super(puzzleInput);
    }

    @Override
    public int getDay() {
        return 14;
    }

    @Override
    public String solvePart1() {
        var mask = new MaskInstruction(0, 0);
        var memory = new HashMap<Long, Long>();
        for (var line : getPuzzleInput().split("\n")) {
            if (MaskInstruction.PATTERN.asPredicate().test(line)) {
                mask = MaskInstruction.parse(line);
            } else if (WriteInstruction.PATTERN.asPredicate().test(line)) {
                var write = WriteInstruction.parse(line);
                memory.put(write.address, (write.value | (mask.mask1 & mask.mask2)) & (mask.mask1 | ~mask.mask2));
            }
        }
        return String.valueOf(memory.values().stream().mapToLong(i -> i).sum());
    }

    @Override
    public String solvePart2() {
        var mask = new MaskInstruction(0, 0);
        var memory = new HashMap<Long, Long>();
        for (var line : getPuzzleInput().split("\n")) {
            if (MaskInstruction.PATTERN.asPredicate().test(line)) {
                mask = MaskInstruction.parse(line);
            } else if (WriteInstruction.PATTERN.asPredicate().test(line)) {
                var write = WriteInstruction.parse(line);
                execute(memory, write.address | mask.mask1, write.value, ~mask.mask2 & 0xFFFFFFFFFL);
            }
        }
        return String.valueOf(memory.values().stream().mapToLong(i -> i).sum());
    }

    private static void execute(Map<Long, Long> memory, long address, long value, long floating) {
        if (floating == 0) {
            memory.put(address, value);
        } else {
            var i = 0;
            while ((floating & 1) == 0) {
                floating >>= 1;
                i++;
            }
            floating = (floating & 0xFFFFFFFFEL) << i;
            execute(memory, address, value, floating);
            execute(memory, address ^ (1L << i), value, floating);
        }
    }

    private record MaskInstruction(long mask1, long mask2) {
        public static final Pattern PATTERN = Pattern.compile("^mask = ([01X]{36})$");
        public static MaskInstruction parse(String s) {
            var matcher = PATTERN.matcher(s);
            if (matcher.find()) {
                return new MaskInstruction(
                        Long.parseLong(matcher.group(1).replaceAll("X", "0"), 2),
                        Long.parseLong(matcher.group(1).replaceAll("0", "1").replaceAll("X", "0"), 2));
            }
            throw new IllegalArgumentException("Not a valid mask instruction");
        }
    }

    private record WriteInstruction(long address, long value) {
        public static final Pattern PATTERN = Pattern.compile("^mem\\[(\\d+)] = (\\d+)$");
        public static WriteInstruction parse(String s) {
            var matcher = PATTERN.matcher(s);
            if (matcher.find()) {
                return new WriteInstruction(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }
            throw new IllegalArgumentException("Not a valid write instruction");
        }
    }
}
