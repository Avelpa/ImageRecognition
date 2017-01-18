
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Eval {
//    public static void main(String[] args)
//    {
//        String postfix = infixToPostfix("5-(4+96*42)/0");
//        try {
//            double ans = evaluatePostfix(postfix);
//            System.out.println(ans);
//        } catch (Exception e)
//        {
//            System.out.println("RROr");
//        }
//    }
//    
//    
//    public static String infixToPostfix(String infix){
//        infix = infix.replaceAll(" ", "");
//        char[] tokens = infix.toCharArray();
//        String postfix = "";
//        
//        Stack<Character> syms = new Stack();
//        
//        for (Character ch: tokens){
//            if (Character.isDigit(ch))
//            {
//                postfix += ch;
//            } else if (ch == '('){
//                syms.push(ch);
//            } else if (ch == ')'){
//                while (syms.peek() != '('){
//                    postfix += syms.pop();
//                } 
//                syms.pop();
//            } else if (ch == '+' || ch == '-'){
//                while (!syms.isEmpty() && syms.peek() != '('){
//                    postfix += syms.pop();
//                }
//                syms.push(ch);
//            } else {
//                while (!syms.isEmpty() && syms.peek() != '+' && syms.peek() != '-' && syms.peek() != '('){
//                    postfix += syms.pop();
//                }
//                syms.push(ch);
//            }
//        }
//        while (!syms.isEmpty())
//        {
//            postfix += syms.pop();
//        }
//        return postfix;
//    }
//    
//    public static double evaluatePostfix(String postfix)
//    {
//        char[] tokens = postfix.toCharArray();
//        Stack<Double> nums = new Stack();
//        
//        for (Character ch: tokens)
//        {
//            if (Character.isDigit(ch)){
//                nums.push((double)Character.getNumericValue(ch));
//            } else {
//                nums.push(eval(nums.pop(), nums.pop(), ch));
//            }
//        }
//        
//        return nums.pop();
//    }
//    
//    private static double eval(double num2, double num1, char op)
//    {
//        switch(op)
//        {
//            case '+':
//                return num1+num2;
//            case '-':
//                return num1-num2;
//            case '*':
//                return num1*num2;
//            case '/':
//                return num1/num2;
//            default:
//                return Double.MIN_VALUE;
//        }
//    }
}
