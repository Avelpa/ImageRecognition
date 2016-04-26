/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Eval {
    public static void main(String[] args)
    {
        String[] equation = "1 + 2 / 3 - 4 * 5".split(" ");
        double answer = Double.parseDouble(equation[0]);
        answer = eval(answer, equation[1], Double.parseDouble(equation[2]));
        System.out.println(answer);
        answer = eval(answer, equation[3], Double.parseDouble(equation[4]));
        System.out.println(answer);
        
        
    }
    
    private static double eval(double num1, String op, double num2)
    {
        switch (op){
            case "+":
                return num1+num2;
            case "/":
                return num1/num2;
            case "-":
                return num1-num2;
            case "*":
                return num1*num2;
            default:
                return Double.MIN_VALUE;
        }
    }
}
