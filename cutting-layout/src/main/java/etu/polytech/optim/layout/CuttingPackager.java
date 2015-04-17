package etu.polytech.optim.layout;

import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.exceptions.LayoutException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface CuttingPackager {

    /**
     * Try to find a layout for the current generation
     * @param generation
     * @throws LayoutException Thrown when no solution is found
     */
    List<Collection<CuttingLayoutElement>> layout(@NotNull final int[] generation) throws LayoutException;
}
