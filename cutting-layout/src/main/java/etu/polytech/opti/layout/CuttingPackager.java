package etu.polytech.opti.layout;

import etu.polytech.opti.layout.exceptions.LayoutException;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface CuttingPackager {

    /**
     * Try to find a layout for the current generation
     * @param generation
     * @throws LayoutException Thrown when no solution is found
     */
    int[][] layout(@NotNull final int[] generation) throws LayoutException;
}
