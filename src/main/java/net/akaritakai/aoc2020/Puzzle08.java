package net.akaritakai.aoc2020;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Day 8 asks us to simulate a simple machine and determine at what state it repeats an instruction or halts.
 *
 * Because the machine cannot repeat any earlier instruction, it cannot take more than O(n) to either repeat an
 * instruction or halt.
 *
 * A much harder version of this problem is the halting problem, which asks if a given program/input tuple will halt.
 * The halting problem is famously undecidable over the domain of all program/input tuples.
 */
public class Puzzle08 extends AbstractPuzzle {
    public Puzzle08(String puzzleInput) {
        super(puzzleInput);
    }

    @Override
    public int getDay() {
        return 8;
    }

    @Override
    public String solvePart1() {
        var machine = new Machine(getPuzzleInput());
        machine.run();
        return String.valueOf(machine.acc);
    }

    @Override
    public String solvePart2() {
        var matcher = Pattern.compile("nop|jmp").matcher(getPuzzleInput());
        while (matcher.find()) {
            var op = getPuzzleInput().substring(matcher.start(), matcher.end()).equals("nop") ? "jmp" : "nop";
            var input = getPuzzleInput().substring(0, matcher.start()) + op + getPuzzleInput().substring(matcher.end());
            var machine = new Machine(input);
            machine.run();
            if (machine.halted()) {
                return String.valueOf(machine.acc);
            }
        }
        throw new IllegalStateException("Unable to find the solution");
    }

    private static class Machine {
        private final List<Instruction> instructions;

        private int ip = 0;
        private int acc = 0;

        public Machine(String input) {
            instructions = input.lines().map(Instruction::from).collect(Collectors.toList());
        }

        public void run() {
            var seen = new HashSet<Integer>();
            while (ip < instructions.size()) {
                if (!seen.add(ip)) {
                    break;
                }
                var instruction = instructions.get(ip);
                switch (instruction.op) {
                    case "acc" -> acc += instruction.value;
                    case "jmp" -> ip += instruction.value - 1;
                }
                ip++;
            }
        }

        public boolean halted() {
            return ip >= instructions.size();
        }
    }

    private record Instruction(String op, int value) {
        public static Instruction from(String s) {
            return new Instruction(s.split(" ")[0], Integer.parseInt(s.split(" ")[1]));
        }
    }
}
