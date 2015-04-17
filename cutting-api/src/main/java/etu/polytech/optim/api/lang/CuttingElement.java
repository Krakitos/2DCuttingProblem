package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */

/**
 * Represent a kind of element to layout on the pattern
 */
public class CuttingElement {

    private final int id;
    private final double width;
    private final double height;
    private final int asking;

    public CuttingElement(final int id, final double width, final double height, final int asking){
        assert width > 0 : "Invalid width <= 0";
        assert height > 0 : "Invalid height <= 0";
        assert asking > 0 : "Invalid asking <= 0";

        this.id = id;
        this.width = width;
        this.height = height;
        this.asking = asking;
    }

    /**
     * Id of the piece
     * @return
     */
    public int id(){
        return id;
    }

    /**
     * Width of this element
     * @return
     */
    public double width(){
        return width;
    }

    /**
     * Height of this element
     * @return
     */
    public double height(){
        return height;
    }

    /**
     * Compute the area of an element
     * @return
     */
    public double area() { return width * height; }

    /**
     * Number of item to print
     * @return
     */
    public int asking(){
        return asking;
    }

    @Override
    public String toString() {
        return "CuttingElement{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", asking=" + asking +
                '}';
    }
}
