
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
    private final String EXAMPLES_PATH;
    
    public NumberReader(String examplesPath)
    {
        this.EXAMPLES_PATH = examplesPath;
        
        this.examples = new HashMap();
        
        File[] exampleFolders = FileManager.getFileList(this.EXAMPLES_PATH);
        
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
     * Check each example image against input, and build a list of probabilities
     * @param img image to be tested
     * @return a Symbol object
     */
    private Symbol getSymbol(BufferedImage img){
        
        HashMap<String, Double> probabilities = new HashMap();
        
        // loop through all possible symbols
        for (String symbol: examples.keySet()){
            // best match for current example symbol
            double maxProbability = 0d;
            // loop through all examples of symbol
            for (BufferedImage example: examples.get(symbol)){
                double prob = analyze(example, img);
                if (prob > maxProbability)
                    maxProbability = prob;
            }
            probabilities.put(symbol, maxProbability);
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
        
        // get smallest shared size
        int smallestWidth = getMin(example.getWidth(), test.getWidth());
        int smallestHeight = getMin(example.getHeight(), test.getHeight());
        
        // scale each image down to the smallest shared size
        double[][] exampleScaled = scaleImageDown(example, smallestWidth, smallestHeight);
        double[][] testScaled = scaleImageDown(test, smallestWidth, smallestHeight);
        
        double score = smallestWidth * smallestHeight;
        for (int y = 0; y < testScaled.length; y ++){
            for (int x = 0; x < testScaled[y].length; x ++){
                score -= analyzePixel(exampleScaled, testScaled, x, y);
            }
        }
        score /= smallestWidth*smallestHeight;
        
//        if (Math.abs((test.getWidth()/testScaled[0].length)/(test.getHeight()/testScaled.length)-1) >= 0.2)
//            score *= 0.8;
        
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
        
        while (x - offset >= 0 || x + offset < example[0].length || y - offset >= 0 || y + offset < example.length){
            
            // match white to white, and non-white to non-white (hence the Math.ceil())
            
            // left
            if (x-offset >= 0) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y][x-offset])) {
                    penalty += Math.abs(test[y][x]-example[y][x-offset]);
                    break;
                }
            }
            // right
            if (x+offset < example[0].length) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y][x+offset])) {
                    penalty += Math.abs(test[y][x]-example[y][x+offset]);
                    break;
                }
                
            }
            // top
            if (y-offset >= 0) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x])) {
                    penalty += Math.abs(test[y][x]-example[y-offset][x]);
                    break;
                }
            }
            // bottom
            if (y+offset < example.length) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x])) {
                    penalty += Math.abs(test[y][x]-example[y+offset][x]);
                    break;
                }
            }
            // top left
            if (x-offset >= 0 && y-offset >= 0) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x-offset])) {
                    penalty += Math.abs(test[y][x]-example[y-offset][x-offset]);
                    break;
                }
            }
            // top right
            if (x+offset < example[0].length && y-offset >= 0) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x+offset])) {
                    penalty += Math.abs(test[y][x]-example[y-offset][x+offset]);
                    break;
                }
            }
            // bottom left
            if (x-offset >= 0 && y+offset < example.length) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x-offset])) {
                    penalty += Math.abs(test[y][x]-example[y+offset][x-offset]);
                    break;
                }
            }
            // bottom right
            if (x+offset < example[0].length && y+offset < example.length) {
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x+offset])) {
                    penalty += Math.abs(test[y][x]-example[y+offset][x+offset]);
                    break;
                }
            }
            
            // failed to make a match in the surrounding square
            penalty ++;
            offset ++;
        }
        return penalty;
    }
    
    public void remember(Symbol[] symbols)
    {
        for (Symbol sym: symbols){
            remember(sym);
        }
    }
    
    private void remember(Symbol sym)
    {
    sym.printProbs();
        if (sym.getProb() == 1)
        {   
            System.out.println("Guessed: " + sym.getName() + "(" + sym.getDisplayString() + ") with 100% confidence");
            return;
        }
        
        String realName;
        
        System.out.print("Guessed: " + sym.getName() + "(" + sym.getDisplayString() + ") with " + sym.getProb()*100 + " confidence.\nReal symbol >> ");
        Scanner in = new Scanner(System.in);
        realName = in.next();
        
        FileManager.assertFolderExists(EXAMPLES_PATH + realName);
        int newIndex = FileManager.countFiles(EXAMPLES_PATH + realName);
        
        FileManager.saveImage(sym.getImage(), EXAMPLES_PATH + realName + "/" + realName + "_" + newIndex + ".png");
    }
    
    private ArrayList<Integer> getBreakPoints(BufferedImage img){
        ArrayList<Integer> breakPoints = new ArrayList();
        breakPoints.add(0);
        
        boolean blackHasOccurred = false;
        for (int x = 0; x < img.getWidth(); x ++){
            boolean curColumnIsWhite = true;
            for (int y = 0; y < img.getHeight(); y ++){
                if (img.getRGB(x, y) != Color.WHITE.getRGB())
                {
                    blackHasOccurred = true;
                    curColumnIsWhite = false;
                }
            }
                    
            if (curColumnIsWhite && blackHasOccurred){
                blackHasOccurred = false;
                breakPoints.add(x);
            }
        }
        breakPoints.add(img.getWidth()); // check the thing from before
        
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
        ArrayList<Integer> breakPoints = getBreakPoints(img);
        
        BufferedImage[] imgs = new BufferedImage[breakPoints.size()-1];
        
        for (int i = 1; i < breakPoints.size(); i ++){
            imgs[i-1] = Bounds.cropImage(img.getSubimage(breakPoints.get(i-1), 0, breakPoints.get(i)-breakPoints.get(i-1), img.getHeight()));
        }
        return imgs;
    }
    
    /*  scales an image down by averaging out pixel cluster colors. Fully black is a 1, fully white is a 0
    * i.e., scaling down 3x3 to a 2x2 will give the following correspondence
    *  consider a = b = c = d = 1
    * |a|a|c|             The resulting scaled down cells are averages of the clusters (so if one of the a's was a white pixel
    * |a|a|c| --> |a|c|     The corresponding 'a' cell in the 2x2 would have a value of 3/4
    * |b|b|d|     |b|d|       
    */
    public double[][] scaleImageDown(BufferedImage img, int newWidth, int newHeight) {
        
        // the number of black cells in the corresponding scaled image cell (in the above example, a = 4, b = c = 2, d = 1
        double[][] scaledImgBMPBlacks = new double[newHeight][newWidth];
        // the number of total cells in the corresponding scaled image cell (in the above example, a = 4, b = c = 2, d = 1
        double[][] scaledImgBMPCount = new double[newHeight][newWidth];
        // the actual scaled image where each cell is the corresponding entry from blacks divided by the corresponding entry from count
        double[][] scaledImgBMP = new double[newHeight][newWidth];
        
        double scaleWidthRatio = (double)newWidth/img.getWidth();
        double scaleHeightRatio = (double)newHeight/img.getHeight();
        
        int occurrence = 0;
        int blacks = 0;
        
        int newX = 0, newY = 0;
        int oldX = newX, oldY = newY;
        for (int y = 0; y < img.getHeight(); y ++){
            for (int x = 0; x < img.getWidth(); x ++){
                // to actually get the corresponding cell indices in the smaller grid, multiply by the scale ratios
                // e.g.: for the above example: (0, 1) --> (0*2/3, 1*2/3) = (0, 0). That's why all the a's are in the top-left corner of the 2x2
                newX = (int)(x*scaleWidthRatio);
                newY = (int)(y*scaleHeightRatio);
                
                // since multiple cells in the bigger grid could correspond to the same cell in the smaller one 
                // (e.g., 'a' corresponds to 4 in the example up top), only store the information collected thus far for a smaller cell
                // when switching smaller cells (e.g., going from a: (0, 1) --> (0, 0) to c: (0, 2) --> (0, 1)
                if (oldX != newX || oldY != newY){
                    scaledImgBMPCount[oldY][oldX] += occurrence;
                    scaledImgBMPBlacks[oldY][oldX] += blacks;
                    blacks = 0;
                    occurrence = 0;
                    oldX = newX;
                    oldY = newY;
                }
                occurrence ++;
                if (img.getRGB(x, y) == Color.BLACK.getRGB()) {
                    blacks ++;
                }
            }
        }
        scaledImgBMPCount[oldY][oldX] += occurrence;
        scaledImgBMPBlacks[oldY][oldX] += blacks;
        
        for (int y = 0; y < newHeight; y ++) {
            for (int x = 0; x < newWidth; x ++) {
                scaledImgBMP[y][x] = (double) scaledImgBMPBlacks[y][x] / scaledImgBMPCount[y][x];
            }
        }
        
        return scaledImgBMP;
    }
    ///////////////////////////////// buggy smallen ///////////////////////////
//    public double[][] scaleImage(BufferedImage img, int width, int height)
//    {
//        double[][] scaled = new double[height][width];
//        double widthFactor = img.getWidth() >= width ? img.getWidth()/(double)width : 1d;
//        double heightFactor = img.getHeight() >= height ? img.getHeight()/(double)height : 1d;
//        
//        
//        System.out.println("wf: " +widthFactor);
//        
//        for (double i = 0; i < img.getHeight(); i += heightFactor)
//        {
//            for (double j = 0; j < img.getWidth(); j += widthFactor)
//            {
//                for (int y = (int)i; y < img.getHeight() && y < i+heightFactor; y ++)
//                {
//                    for (int x = (int)j; x < img.getWidth() && x < j+widthFactor; x ++)
//                    {
//                        int scaledY = y, scaledX = x;
//                        if (img.getHeight() < height)
//                            scaledY += (height-img.getHeight())/2;
//                        else
//                            scaledY /= heightFactor;
//                        if (img.getWidth() < width)
//                            scaledX += (width-img.getWidth())/2;
//                        else
//                            scaledX /= widthFactor;
//                        
//                        scaled[scaledY][scaledX] += (img.getRGB(x,y)==Color.BLACK.getRGB() ? 1: 0)/(widthFactor*heightFactor);
//                    }
//                }
//            }
//        }
//        
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
//        
//        return scaled;
//    }
    
    void bulkRemember(Symbol[] symbols, String realName) {
        
        for (Symbol sym: symbols)
        {
            FileManager.assertFolderExists(EXAMPLES_PATH + realName);
            int newIndex = FileManager.countFiles(EXAMPLES_PATH + realName);

            FileManager.saveImage(sym.getImage(), EXAMPLES_PATH + realName + "/" + realName + "_" + newIndex + ".png");
            
            newIndex ++;
        }
    }
    
}
