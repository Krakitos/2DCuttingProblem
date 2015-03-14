package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */

/**
 * Represent a kind of element to layout on the pattern
 */
public class CuttingElement {

    private final float width;
    private final float height;

    public CuttingElement(final float width, final float height){
        assert width > 0 : "Invalid width <= 0";
        assert height > 0 : "Invalid height <= 0";

        this.width = width;
        this.height = height;
    }

    /**
     * Width of this element
     * @return
     */
    public float width(){
        return width;
    }

    /**
     * Height of this element
     * @return
     */
    public float height(){
        return height;
    }

    /**
     * Compute the area of an element
     * @return
     */
    public double area() { return width * height; }
}
