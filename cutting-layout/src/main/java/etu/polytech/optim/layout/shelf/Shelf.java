package etu.polytech.optim.layout.shelf;

import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.lang.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Morgan on 10/04/2015.
 */
public class Shelf {
    private double height;

    private List<CuttingLayoutElement> rectangles;

    public Shelf(double height) {
        this.height = height;

        this.rectangles = new ArrayList<>();
    }

    /**
     * Insert the specified element in the shelf.
     * @param position
     * @param ref
     */
    public void insert(@NotNull final Rectangle position, final CuttingElement ref){
        final boolean isRotate = position.width() != ref.width();

        rectangles.add(new CuttingLayoutElement(position.x(), position.y(), isRotate, ref));
    }

    /**
     * Height of this shelf
     * @return
     */
    public double height() {
        return height;
    }

    public Collection<CuttingLayoutElement> items() {
        return rectangles;
    }
}
