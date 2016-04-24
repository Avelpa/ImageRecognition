
import com.sun.javafx.scene.text.TextLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

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
        doMath();
    }
    
    private static void learn(){
        // create number reader
        NumberReader reader = new NumberReader();
        reader.init();
        BufferedImage testImg = FileManager.loadImage("images/tests/test.png");
        testImg = Bounds.cropImage(testImg);
        HashMap<String, Double> probs = reader.getProbs(testImg);
        
        for (String symbol: probs.keySet()){
            System.out.print(symbol + ": " + probs.get(symbol) + ", ");
            System.out.println();
        }
        String symbol = reader.getResult(probs);
        System.out.println("Therefore... it's a " + symbol);
        
        reader.remember(testImg, symbol, probs);
    }
    
    private static void doMath(){
        NumberReader reader = new NumberReader();
        reader.init();
        
        BufferedImage firstNumber = FileManager.loadImage("images/tests/firstNum.png");
        firstNumber = Bounds.cropImage(firstNumber);
        BufferedImage secondNumber = FileManager.loadImage("images/tests/secondNum.png");
        secondNumber = Bounds.cropImage(secondNumber);
        BufferedImage operation = FileManager.loadImage("images/tests/operation.png");
        operation = Bounds.cropImage(operation);
        
        int num1 = Integer.parseInt(reader.getResult(reader.getProbs(firstNumber)));
        int num2 = Integer.parseInt(reader.getResult(reader.getProbs(secondNumber)));
        String op = reader.getResult(reader.getProbs(operation));
        
        switch(op){
            case "+":
                System.out.println(num1+num2);
                break;
            case "-":
                System.out.println(num1-num2);
                break;
            case "x":
                System.out.println(num1*num2);
                break;
            case "div":
                System.out.println((double)num1/num2);
                break;
        }
    }
}
