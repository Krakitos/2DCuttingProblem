package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */
public class CuttingSheet {

    private final int width;
    private final int height;

    public CuttingSheet(int width, int height) {
        this.width = width;
        this.height = height;
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
     * Area of the pattern
     * width * height
     * @return
     */
    public int area(){
        return width * height;
    }
}
