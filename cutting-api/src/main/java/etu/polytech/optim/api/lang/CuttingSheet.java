package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */
public class CuttingSheet {

    private final int width;
    private final int height;
    private final int price;

    public CuttingSheet(int width, int height, int price) {
        this.width = width;
        this.height = height;
        this.price = price;
    }

    /**
     * Width of this pattern
     * @return
     */
    public int width(){
        return width;
    }

    /**
     * Height of this pattern
     * @return
     */
    public int height(){
        return height;
    }

    /**
     * Price of a pattern to print
     * @return
     */
    public int price() {
        return price;
    }

    /**
     * Area of the pattern
     * width * height
     * @return
     */
    public int area(){
        return width * height;
    }
}
