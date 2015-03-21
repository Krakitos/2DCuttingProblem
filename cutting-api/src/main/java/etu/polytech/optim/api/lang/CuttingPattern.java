package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */
public class CuttingPattern {

    private final int width;
    private final int height;
    private final float price;

    public CuttingPattern(final int width, final int height, final float price) {
        assert width > 0 : "Invalid width has to be > 0 (width=" + width + ")";
        assert height > 0 : "Invalid height has to be > 0 (height=" + height + ")";
        assert price > 0 : "Invalid price has to be > 0 (price=" + price + ")";

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
     * Price of a pattern
     * @return
     */
    public float price(){
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
