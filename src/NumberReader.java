
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
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
public class NumberReader {
    
    private HashMap<String, BufferedImage[]> examples;
    private final String examplesPath;
    
    public NumberReader(String examplesPath)
    {
        this.examplesPath = examplesPath;
    }
    
//    /**
//     * Loads all of the examples.
//     */
    public void init(){
        examples = new HashMap();
        
        // get all of the example folders
        File[] exampleFolders = FileManager.getFileList(examplesPath);
        
        // parse each folder for the actual example images
        for (File folder: exampleFolders){
            String exampleName = folder.getName();
            BufferedImage[] imgs = new BufferedImage[FileManager.countFiles(folder)];
            // get each example image in the folder
            File[] files = folder.listFiles();
            for (int i = 0; i < imgs.length; i ++){
                imgs[i] = FileManager.loadImage(files[i].getPath());
            }
            // populate examples hashmap
            examples.put(exampleName, imgs);
        }
    }
    
    /**
     * Check each example image against input
     * @param img image to be tested
     * @return a collection of all the possibilities with a percentage confidence for each possibility
     */
    private Symbol getSymbol(BufferedImage img){
        
        HashMap<String, Double> probabilities = new HashMap();
        
        // loop through all possible symbols
        for (String symbol: examples.keySet()){
            // best match for current example symbol
            double maxProb = 0d;
            // loop through all examples of current symbol
            for (BufferedImage example: examples.get(symbol)){
                double prob = analyze(example, img);
                if (prob > maxProb)
                    maxProb = prob;
            }
            probabilities.put(symbol, maxProb);
        }
        return new Symbol(probabilities, img);
    }
    
    // takes an image and returns all of the parsed symbols from it
    public Symbol[] getSymbols(BufferedImage img)
    {
        BufferedImage[] splitImg = splitImage(img);
        
        Symbol[] symbols = new Symbol[splitImg.length];
        for (int i = 0; i < symbols.length; i ++)
        {
            symbols[i] = getSymbol(splitImg[i]);
        }
        return symbols;
    }
    
    
    /**
     * If just all pixels equal, then bias towards one (cause the most "correct" whites)
     * If just black, then bias away from one (cause the least "correct" blacks)
     */
    public double analyze(BufferedImage example, BufferedImage test){
        
        // get size intersect
        int smallestWidth = getMin(example.getWidth(), test.getWidth());
        int smallestHeight = getMin(example.getHeight(), test.getHeight());
        
        // scale each image to the intersect
        double[][] exampleScaled = scaleImage(example, smallestWidth, smallestHeight);
        double[][] testScaled = scaleImage(test, smallestWidth, smallestHeight);
        
        double score = smallestWidth * smallestHeight;
        //double penalty = 0d;
        for (int y = 0; y < testScaled.length; y ++){
            for (int x = 0; x < testScaled[y].length; x ++){
                score -= analyzePixel(exampleScaled, testScaled, x, y);
            }
        }
        score /= smallestWidth*smallestHeight;
        
        if (Math.abs((test.getWidth()/testScaled[0].length)/(test.getHeight()/testScaled.length)-1) >= 0.2)
            score *= 0.8;
        
        return score;
    }
    
    private int getMin(int num1, int num2){
        if (num1 < num2)
            return num1;
        return num2;
    }
    
    public double analyzePixel(double[][] example, double[][] test, int x, int y){
        int offset = 0;
        double penalty = 0;
        
        boolean done = false;
        while (x - offset >= 0 || x + offset < example[0].length || y - offset >= 0 || y + offset < example.length){
            
            // left
            if (x-offset >= 0){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y][x-offset])){
                    penalty += Math.abs(test[y][x]-example[y][x-offset]);
                    done = true;
                }
            }
            // right
            if (x+offset < example[0].length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y][x+offset])){
                    penalty += Math.abs(test[y][x]-example[y][x+offset]);
                    done = true;
                }
                
            }
            // top
            if (y-offset >= 0 && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x])){
                    penalty += Math.abs(test[y][x]-example[y-offset][x]);
                    done = true;
                }
            }
            // bottom
            if (y+offset < example.length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x])){
                    penalty += Math.abs(test[y][x]-example[y+offset][x]);
                    done = true;
                }
            }
            // top left
            if (x-offset >= 0 && y-offset >= 0 && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x-offset])){
                    penalty += Math.abs(test[y][x]-example[y-offset][x-offset]);
                    done = true;
                }
            }
            // top right
            if (x+offset < example[0].length && y-offset >= 0 && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x+offset])){
                    penalty += Math.abs(test[y][x]-example[y-offset][x+offset]);
                    done = true;
                }
            }
            // bottom left
            if (x-offset >= 0 && y+offset < example.length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x-offset])){
                    penalty += Math.abs(test[y][x]-example[y+offset][x-offset]);
                    done = true;
                }
            }
            // bottom right
            if (x+offset < example[0].length && y+offset < example.length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x+offset])){
                    penalty += Math.abs(test[y][x]-example[y+offset][x+offset]);
                    done = true;
                }
            }
            
            if (done)
                break;
            
            penalty ++; 
            offset ++;
        }
        return penalty;
    }
    
    public void remember(Symbol[] symbols, boolean brief)
    {
        for (Symbol sym: symbols){
            remember(sym, brief);
        }
    }
    
    private void remember(Symbol sym, boolean brief)
    {
//        sym.printProbs();
        if (sym.getProb() == 1)
        {   
            if (!brief)
                System.out.println("Guessed: " + sym.getName() + "(" + sym.getDisplayString() + ") with 100% confidence");
            return;
        }
        
        String realName;
        
        if (!brief){
            System.out.print("Guessed: " + sym.getName() + "(" + sym.getDisplayString() + ") with " + sym.getProb()*100 + " confidence.\nReal symbol >> ");
            Scanner in = new Scanner(System.in);
            realName = in.next();
        } else {
            realName = sym.getName();
        }
        
        FileManager.assertFolderExists(examplesPath + realName);
        int newIndex = FileManager.countFiles(examplesPath + realName);
        
        FileManager.saveImage(sym.getImage(), examplesPath + realName + "/" + realName + "_" + newIndex + ".png");
    }
    
    private ArrayList<Integer> getBreakPoints(BufferedImage img){
        ArrayList<Integer> breakPoints = new ArrayList();
        breakPoints.add(0);
        
        boolean parsing = false;
        for (int x = 0; x < img.getWidth(); x ++){
            boolean allWhite = true;
            for (int y = 0; y < img.getHeight(); y ++){
                if (img.getRGB(x, y) != Color.WHITE.getRGB())
                {
                    parsing = true;
                    allWhite = false;
                }
            }
                    
            if (allWhite && parsing){
                parsing = false;
                breakPoints.add(x);
            }
        }
        breakPoints.add(img.getWidth());
        
        return breakPoints;
    }
    
    public String infixToPostfix(Symbol[] symbols){
        StringBuilder postfix = new StringBuilder(symbols.length);
        
        Stack<String> syms = new Stack();
        
        boolean buildingNum = false;
        for (Symbol sym: symbols){
            String str = sym.getDisplayString();
            if (str.matches("[0-9]"))
            {
                buildingNum = true;
                postfix.append(str);
            } else {
                if (buildingNum){
                    postfix.append(",");
                    buildingNum = false;
                }
                if (str.equals("(")){
                    syms.push(str);
                } else if (str.equals(")")){
                    while (!syms.peek().equals("(")){
                        postfix.append(syms.pop());
                        postfix.append(",");
                    } 
                    syms.pop();
                } else if (str.equals("+") || str.equals("-")){
                    while (!syms.isEmpty() && !syms.peek().equals("(")){
                        postfix.append(syms.pop());
                        postfix.append(",");
                    }
                    syms.push(str);
                } else {
                    while (!syms.isEmpty() && !syms.peek().equals("+") && !syms.peek().equals("-") && !syms.peek().equals("(")){
                        postfix.append(syms.pop());
                        postfix.append(",");
                    }
                    syms.push(str);
                }
            }
        }
        while (!syms.isEmpty())
        {
            if (buildingNum)
            {
                postfix.append(",");
                buildingNum = false;
            }
            postfix.append(syms.pop());
            postfix.append(",");
        }
        return postfix.toString();
    }
    
    public double evaluatePostfix(String postfix) {
        String[] tokens = postfix.split(",");
        Stack<Double> nums = new Stack();

        for (String str : tokens) {
            if (str.matches("[0-9]+")) {
                nums.push((double)Integer.parseInt(str));
            } else {
                nums.push(eval(nums.pop(), nums.pop(), str));
            }
        }
        return nums.pop();
    }

    private double eval(double num2, double num1, String op) {
        switch (op) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                return num1 / num2;
            case "<":
                return (num1 < num2) ? 1 : 0;
            case ">":
                return (num1 > num2) ? 1 : 0;
            default:
                return Double.MIN_VALUE;
        }
    }
    
    /**
     * Splits an image horizontally into  cropped sub-images
     * @param img
     * @return 
     */
    public BufferedImage[] splitImage(BufferedImage img){
        ArrayList<Integer> bps = getBreakPoints(img);
        
        if (bps.isEmpty())
            return null;
        BufferedImage[] imgs = new BufferedImage[bps.size()-1];
        
        for (int i = 1; i < bps.size(); i ++){
            imgs[i-1] = img.getSubimage(bps.get(i-1), 0, bps.get(i)-bps.get(i-1), img.getHeight());
            imgs[i-1] = Bounds.cropImage(imgs[i-1]);
        }
        return imgs;
    }
    /*
    public double[][] scaleImage(BufferedImage img, int width, int height){
        
        double[][] scaledImg = new double[height][width];
        double scaleWidth = (double)width/img.getWidth();
        double scaleHeight = (double)height/img.getHeight();
        
        int scaledX = 0, scaledY = 0;
        
        int xPrev = scaledX, yPrev = scaledY;
        int occurrence = 0;
        
        double currentCell = 0;
        
        for (int y = 0; y < img.getHeight(); y ++){
            for (int x = 0; x < img.getWidth(); x ++){
                scaledX = (int)(x*scaleWidth);
                scaledY = (int)(y*scaleHeight);
                
                if (xPrev != scaledX || yPrev != scaledY){
                    currentCell /= occurrence;
                    scaledImg[yPrev][xPrev] = currentCell;
                    currentCell = 0;
                    occurrence = 0;
                    xPrev = scaledX;
                    yPrev = scaledY;
                }
                occurrence ++;
                if (img.getRGB(x, y) == Color.BLACK.getRGB())
                    currentCell ++;
            }
        }
        currentCell /= occurrence;
        scaledImg[scaledY][scaledX] = currentCell;
        
        return scaledImg;
    }*/
    ///////////////////////////////// buggy smallen ///////////////////////////
    public double[][] scaleImage(BufferedImage img, int width, int height)
    {
        double[][] scaled = new double[height][width];
        double widthFactor = img.getWidth() >= width ? img.getWidth()/(double)width : 1d;
        double heightFactor = img.getHeight() >= height ? img.getHeight()/(double)height : 1d;
        
        
        for (double i = 0; i < img.getHeight(); i += heightFactor)
        {
            for (double j = 0; j < img.getWidth(); j += widthFactor)
            {
                for (int y = (int)i; y < img.getHeight() && y < i+heightFactor; y ++)
                {
                    for (int x = (int)j; x < img.getWidth() && x < j+widthFactor; x ++)
                    {
                        int scaledY = y, scaledX = x;
                        if (img.getHeight() < height)
                            scaledY += (height-img.getHeight())/2;
                        else
                            scaledY /= heightFactor;
                        if (img.getWidth() < width)
                            scaledX += (width-img.getWidth())/2;
                        else
                            scaledX /= widthFactor;
                        
                        scaled[scaledY][scaledX] += (img.getRGB(x,y)==Color.BLACK.getRGB() ? 1: 0)/(widthFactor*heightFactor);
                    }
                }
            }
        }
        
//        for (int y = 0; y < scaled.length; y ++)
//        {
//            for (int x = 0; x < scaled[y].length; x ++)
//            {
//                if (scaled[y][x] == 0)
//                    System.out.print("_._");
//                else
//                    System.out.print(scaled[y][x]);
//            }
//            System.out.println();
//        }
        
        return scaled;
    }
}
