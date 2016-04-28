
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dmitry
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NumberReader reader = new NumberReader();
        reader.init();

        BufferedImage testImg = FileManager.loadImage("images/tests/test.png");
        testImg = Bounds.cropImage(testImg);

        BufferedImage[] tokens = reader.splitImage(testImg);

        if (tokens != null) {
            String[] syms = new String[tokens.length];
            HashMap<String, Double>[] probsList = new HashMap[tokens.length];

            for (int i = 0; i < syms.length; i++) {
                probsList[i] = reader.parseSymbol(tokens[i]);
                syms[i] = reader.getResult(probsList[i]);
                System.out.print(syms[i]);
            }
            try {
                String postfix = infixToPostfix(toString(syms));
                double answer = evaluatePostfix(postfix);
                System.out.println("=" + answer);
            } catch (Exception e) {
                System.err.println("Error parsing expression: " + e);
            }
            /*
             if (isExpression(tokens))
             doMath(tokens);
             else
             learnBulk(tokens);*/
        } else {
            learn();
        }
    }
    
    private static String toString(String[] arr)
    {
        StringBuilder builder = new StringBuilder();
        for(String s : arr) {
            builder.append(s);
        }
        return builder.toString();
    }

    public static String infixToPostfix(String infix) {
        infix = infix.replaceAll(" ", "");
        char[] tokens = infix.toCharArray();
        String postfix = "";

        Stack<Character> syms = new Stack();

        for (Character ch : tokens) {
            if (Character.isDigit(ch)) {
                postfix += ch;
            } else if (ch == '(') {
                syms.push(ch);
            } else if (ch == ')') {
                while (syms.peek() != '(') {
                    postfix += syms.pop();
                }
                syms.pop();
            } else if (ch == '+' || ch == '-') {
                while (!syms.isEmpty() && syms.peek() != '(') {
                    postfix += syms.pop();
                }
                syms.push(ch);
            } else {
                while (!syms.isEmpty() && syms.peek() != '+' && syms.peek() != '-' && syms.peek() != '(') {
                    postfix += syms.pop();
                }
                syms.push(ch);
            }
        }
        while (!syms.isEmpty()) {
            postfix += syms.pop();
        }
        return postfix;
    }

    private static boolean isExpression(BufferedImage[] tokens) {
        NumberReader reader = new NumberReader();
        reader.init();

        boolean parseInt = true;
        int num1 = 0;
        int num2 = 0;
        String operator = "";

        boolean n1parsed = false;
        boolean n2parsed = false;

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = Bounds.cropImage(tokens[i]);
            HashMap<String, Double> probs = reader.parseSymbol(tokens[i]);
            String sym = reader.getResult(probs);

            if (parseInt) {
                if (sym.matches("\\d")) {
                    if (operator.isEmpty()) {
                        n1parsed = true;
                        num1 *= 10;
                        num1 += Integer.parseInt(sym);
                    } else {
                        n2parsed = true;
                        num2 *= 10;
                        num2 += Integer.parseInt(sym);
                    }
                } else {
                    operator = sym;
                }
            }
        }

        return n1parsed && n2parsed && !operator.isEmpty();
    }

    private static void doMath(BufferedImage[] tokens) {
        NumberReader reader = new NumberReader();
        reader.init();

        boolean parseInt = true;
        int num1 = 0;
        int num2 = 0;
        String operator = "";

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = Bounds.cropImage(tokens[i]);
            HashMap<String, Double> probs = reader.parseSymbol(tokens[i]);
            String sym = reader.getResult(probs);

            if (parseInt) {
                if (sym.matches("\\d")) {
                    if (operator.isEmpty()) {
                        num1 *= 10;
                        num1 += Integer.parseInt(sym);
                    } else {
                        num2 *= 10;
                        num2 += Integer.parseInt(sym);
                    }
                } else {
                    operator = sym;
                }
            }
        }
        double ans = 0;
        switch (operator) {
            case "+":
                ans = num1 + num2;
                break;
            case "-":
                ans = num1 - num2;
                break;
            case "x":
                ans = num1 * num2;
                operator = "*";
                break;
            case "div":
                ans = (double) num1 / num2;
                operator = "/";
                break;
            case "gt":
                ans = num1 > num2 ? 1 : 0;
                operator = ">";
                break;
            case "lt":
                ans = num1 < num2 ? 1 : 0;
                operator = "<";
                break;
            default:
                ans = Double.MIN_VALUE;
        }
        System.out.println(num1 + " " + operator + " " + num2 + " = " + ans);
        System.out.print("Expected answer:\n>> ");
        Scanner in = new Scanner(System.in);
        if (in.nextDouble() != ans) {
            learnBulk(tokens);
        }
    }

    public static double evaluatePostfix(String postfix) {
        char[] tokens = postfix.toCharArray();
        Stack<Double> nums = new Stack();

        for (Character ch : tokens) {
            if (Character.isDigit(ch)) {
                nums.push((double) Character.getNumericValue(ch));
            } else {
                nums.push(eval(nums.pop(), nums.pop(), ch));
            }
        }

        return nums.pop();
    }

    private static double eval(double num2, double num1, char op) {
        switch (op) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                return num1 / num2;
            default:
                return Double.MIN_VALUE;
        }
    }

    private static void learnBulk(BufferedImage[] tokens) {
        NumberReader reader = new NumberReader();
        reader.init();

        String[] syms = new String[tokens.length];
        HashMap<String, Double>[] superProbs = new HashMap[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = Bounds.cropImage(tokens[i]);
            HashMap<String, Double> probs = reader.parseSymbol(tokens[i]);
            String sym = reader.getResult(probs);
            System.out.print(sym);
            syms[i] = sym;
            superProbs[i] = probs;
        }
        System.out.println();
        for (int i = 0; i < syms.length; i++) {
            System.out.println(syms[i]);
            reader.remember(tokens[i], syms[i], superProbs[i]);
        }
    }

    private static void learn() {
        // create number reader
        NumberReader reader = new NumberReader();
        reader.init();
        BufferedImage testImg = FileManager.loadImage("images/tests/test.png");
        testImg = Bounds.cropImage(testImg);
        HashMap<String, Double> probs = reader.parseSymbol(testImg);

        for (String symbol : probs.keySet()) {
            System.out.print(symbol + ": " + probs.get(symbol) + ", ");
            System.out.println();
        }
        String symbol = reader.getResult(probs);
        System.out.println("Therefore... it's a " + symbol);

        reader.remember(testImg, symbol, probs);
    }
}
