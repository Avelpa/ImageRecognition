
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Scanner;

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
        if (tokens != null){
            if (isExpression(tokens))
                doMath2(tokens);
            else
                learnBulk(tokens);
        }
        else
            learn();
//        doMath2();
    }
    
    private static boolean isExpression(BufferedImage[] tokens)
    {
        NumberReader reader = new NumberReader();
        reader.init();
        
        boolean parseInt = true;
        int num1 = 0;
        int num2 = 0;
        String operator = "";
        
        boolean n1parsed = false;
        boolean n2parsed = false;

        for(int i = 0; i < tokens.length; i ++){
            tokens[i] = Bounds.cropImage(tokens[i]);
            HashMap<String, Double> probs = reader.parseSymbol(tokens[i]);
            String sym = reader.getResult(probs);

            if (parseInt){
                if (sym.matches("\\d"))
                {
                    if (operator.isEmpty())
                    {
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
    
    private static void doMath2(BufferedImage[] tokens)
    {
        NumberReader reader = new NumberReader();
        reader.init();
        
        boolean parseInt = true;
        int num1 = 0;
        int num2 = 0;
        String operator = "";

        for(int i = 0; i < tokens.length; i ++){
            tokens[i] = Bounds.cropImage(tokens[i]);
            HashMap<String, Double> probs = reader.parseSymbol(tokens[i]);
            String sym = reader.getResult(probs);

            if (parseInt){
                if (sym.matches("\\d"))
                {
                    if (operator.isEmpty())
                    {
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
        switch(operator){
            case "+":
                ans = num1+num2;
                break;
            case "-":
                ans = num1-num2;
                break;
            case "x":
                ans = num1*num2;
                operator = "*";
                break;
            case "div":
                ans = (double)num1/num2;
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
        if (in.nextDouble() != ans){
            learnBulk(tokens);
        }
    }
    
    private static void learnBulk(BufferedImage[] tokens){
        NumberReader reader = new NumberReader();
        reader.init();
        
        for(int i = 0; i < tokens.length; i ++){
            tokens[i] = Bounds.cropImage(tokens[i]);
            HashMap<String, Double> probs = reader.parseSymbol(tokens[i]);
            String sym = reader.getResult(probs);
            System.out.println(sym);
            reader.remember(tokens[i], sym, probs);
        }
    }
    
    private static void learn(){
        // create number reader
        NumberReader reader = new NumberReader();
        reader.init();
        BufferedImage testImg = FileManager.loadImage("images/tests/test.png");
        testImg = Bounds.cropImage(testImg);
        HashMap<String, Double> probs = reader.parseSymbol(testImg);
        
        for (String symbol: probs.keySet()){
            System.out.print(symbol + ": " + probs.get(symbol) + ", ");
            System.out.println();
        }
        String symbol = reader.getResult(probs);
        System.out.println("Therefore... it's a " + symbol);
        
        reader.remember(testImg, symbol, probs);
    }
}
