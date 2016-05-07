/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class tests {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        double[][] oldArray = {
            {0,0,0,0},
            {0,1,1,0},
            {0,1,1,0},
            {0,0,0,0},
        };
        
        int newWidth = 3;
        int newHeight = 3;
        
        double[][] newArray = smallen(oldArray, newWidth, newHeight);
        
        printArray(newArray);
        
    }
    
    public static double[][] largen(double[][] old, int width, int height)
    {
        double[][] scaled = new double[height][width];
        double widthFactor = old[0].length/(double)width;
        double heightFactor = old.length/(double)height;
        
        for (double i = 0; i < height; i += heightFactor)
        {
            for (double j = 0; j < width; j += widthFactor)
            {
                scaled[(int)i][(int)j] += old[(int)(i*heightFactor)][(int)(j*widthFactor)]*widthFactor*heightFactor;
                scaled[(int)i][(int)j] *= widthFactor*heightFactor;
            }
        }
        
//        for (double i = 0; i < height; i ++)
//        {
//            for (double j = 0; j < width; j ++)
//            {
//                scaled[(int)i][(int)j] += old[(int)(i*heightFactor)][(int)(j*widthFactor)];
//            }
//        }
        
        return scaled;
    }
    
    public static double[][] smallen(double[][] old, int width, int height)
    {
        double[][] scaled = new double[height][width];
        double widthFactor = old[0].length/(double)width;
        double heightFactor = old.length/(double)height;
        
        for (double i = 0; i < old.length; i += heightFactor)
        {
            for (double j = 0; j < old[(int)i].length; j += widthFactor)
            {
                for (int y = (int)i; y < old.length && y < i+heightFactor; y ++)
                {
                    for (int x = (int)j; x < old[y].length && x < j+widthFactor; x ++)
                    {
//                        System.out.println(i + " " + j + " " + y + " " + x);
                        scaled[(int)(y/heightFactor)][(int)(x/widthFactor)] += old[y][x]/(widthFactor*heightFactor);
                    }
                }
            }
        }
        
        return scaled;
    }
    
    public static void printArray(int[][] array)
    {
        for (int y = 0; y < array.length; y ++)
        {
            for (int x = 0; x < array[y].length; x ++)
            {
                System.out.print(array[y][x]);
            }
            System.out.println();
        }
    }
    public static void printArray(double[][] array)
    {
        for (int y = 0; y < array.length; y ++)
        {
            for (int x = 0; x < array[y].length; x ++)
            {
                if (array[y][x] == 0)
                    System.out.print("_,");
                else
                    System.out.print(array[y][x] + ",");
            }
            System.out.println();
        }
    }
    
}
