
import java.awt.Color;
import java.awt.image.BufferedImage;
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
    public static void main(String[] args)
    {
        NumberReader reader = new NumberReader("images/new/");
        reader.init();
        
        BufferedImage testImg = FileManager.loadImage("images/tests/test.png");
        testImg = Bounds.cropImage(testImg);
        Symbol[] symbols = reader.getSymbols(testImg);
        
        learn(symbols, reader);
        
//        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_BGR);
//        int[] data = new int[10000];
//        int i = 0;
//        for (int y = 0; y < 100; y ++)
//        {
//            for (int x = 0; x < 100; x ++)
//            {
//                if ((x+y)%2 == 0)
//                    data[i] = Color.BLACK.getRGB();
//                else
//                    data[i] = Color.WHITE.getRGB();
//                i ++;
//            }
//        }
//        
//        img.setRGB(0, 0, 100, 100, data, 0, 100);
//        
//        FileManager.saveImage(img, "images/newthing.png");
    }
    
    private static void printSyms(Symbol[] syms, String delimeter)
    {
        for (Symbol sym: syms)
        {
            System.out.print(sym.getDisplayString());
            System.out.print(delimeter);
        }
    }
    private static void printSyms(Symbol[] syms)
    {
        printSyms(syms, "");
        System.out.println();
    }
    
    private static void learn(Symbol[] symbols, NumberReader reader)
    {
        printSyms(symbols);
        
        reader.remember(symbols);
    }
    
    private static void doMath(Symbol[] symbols, NumberReader reader)
    {
        try {
            String postfix = reader.infixToPostfix(symbols);
            double ans = reader.evaluatePostfix(postfix);
            
            printSyms(symbols, " ");
            System.out.print("= ");
            System.out.println(ans);
            
            System.out.print("right?\n>> ");
            Scanner in = new Scanner(System.in);
            if (in.next().startsWith("n"))
                reader.remember(symbols);
            else
                reader.remember(symbols);
        } catch (Exception e) {
            System.out.println("INVALID EXPRESSION");
            printSyms(symbols);
            reader.remember(symbols);
        }
    }
}
