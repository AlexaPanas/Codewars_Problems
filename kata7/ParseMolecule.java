import java.util.*;

public class ParseMolecule {

    public static Map<String, Integer> getAtoms(String formula) {
        if (!formula.matches("[A-Za-z0-9\\[\\]\\(\\)\\{\\}]+")) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }

        Deque<Map<String, Integer>> stack = new ArrayDeque<>();
        Map<String, Integer> currentMap = new HashMap<>();
        Deque<Character> bracketStack = new ArrayDeque<>();
        int i = 0;

        while (i < formula.length()) {
            char ch = formula.charAt(i);

            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(currentMap);
                currentMap = new HashMap<>();
                bracketStack.push(ch);
                i++;
            } else if (ch == ')' || ch == ']' || ch == '}') {
                if (bracketStack.isEmpty() || !matches(bracketStack.pop(), ch)) {
                    throw new IllegalArgumentException("Unmatched closing bracket at position " + i);
                }
                i++;
                int[] parsed = parseNumber(formula, i);
                int multiplier = parsed[0];
                i = parsed[1];

                Map<String, Integer> tempMap = currentMap;
                currentMap = stack.pop();
                for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                    currentMap.put(entry.getKey(), currentMap.getOrDefault(entry.getKey(), 0) + entry.getValue() * multiplier);
                }
            } else if (Character.isUpperCase(ch)) {
                int start = i++;
                while (i < formula.length() && Character.isLowerCase(formula.charAt(i))) i++;
                String atom = formula.substring(start, i);

                int[] parsed = parseNumber(formula, i);
                int count = parsed[0];
                i = parsed[1];

                currentMap.put(atom, currentMap.getOrDefault(atom, 0) + count);
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }

        if (!bracketStack.isEmpty()) {
            throw new IllegalArgumentException("Unmatched opening bracket(s) in formula: " + formula);
        }

        return currentMap;
    }

    private static int[] parseNumber(String formula, int i) {
        if (i >= formula.length() || !Character.isDigit(formula.charAt(i))) return new int[] {1, i};

        int val = 0;
        while (i < formula.length() && Character.isDigit(formula.charAt(i))) {
            val = val * 10 + (formula.charAt(i++) - '0');
        }
        return new int[] {val, i};
    }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')') ||
               (open == '[' && close == ']') ||
               (open == '{' && close == '}');
    }
}
