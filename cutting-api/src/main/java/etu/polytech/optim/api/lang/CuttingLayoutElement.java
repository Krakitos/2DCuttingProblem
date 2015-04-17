package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 13/03/2015.
 */
public class CuttingLayoutElement {
    private final double x;
    private final double y;
    private final boolean rotate;
    private final CuttingElement element;

    public CuttingLayoutElement(double x, double y, boolean rotate, CuttingElement element) {
        this.x = x;
        this.y = y;
        this.rotate = rotate;
        this.element = element;
    }

    /**
     * X coordinate of the element
     * @return
     */
    public double x(){
        return x;
    }

    /**
     * Y coordinate of the element
     * @return
     */
    public double y(){
        return y;
    }

    /**
     * Width of this element
     * @return
     */
    public double width(){ return rotate ? element.height() : element().width(); }

    /**
     * Height of this element
     * @return
     */
    public double height (){ return rotate ? element.width() : element().height(); }

    /**
     * Indicate whether the piece is rotated or not
     * @return
     */
    public boolean isRotate(){
        return rotate;
    }

    /**
     * Element associated
     * @return
     */
    public CuttingElement element(){
        return element;
    }

    @Override
    public String toString() {
        return "CuttingLayoutElement{" +
                "x=" + x +
                ", y=" + y +
                ", rotate=" + rotate +
                ", element=" + element +
                '}';
    }
}
