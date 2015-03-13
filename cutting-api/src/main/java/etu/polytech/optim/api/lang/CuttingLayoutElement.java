package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 13/03/2015.
 */
public class CuttingLayoutElement {
    private final int x;
    private final int y;
    private final CuttingElement element;

    public CuttingLayoutElement(int x, int y, CuttingElement element) {
        this.x = x;
        this.y = y;
        this.element = element;
    }

    /**
     * X coordinate of the element
     * @return
     */
    public int x(){
        return x;
    }

    /**
     * Y coordinate of the element
     * @return
     */
    public int y(){
        return y;
    }

    /**
     * Element associated
     * @return
     */
    public CuttingElement element(){
        return element;
    }
}
