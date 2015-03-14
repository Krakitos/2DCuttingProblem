package etu.polytech.optim.api.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created by Morgan on 14/03/2015.
 */
public class CuttingConfiguration {
    private final CuttingSheet sheet;
    private final Map<CuttingElement, Integer> elements;

    public CuttingConfiguration(@NotNull final CuttingSheet sheet, @NotNull final Map<CuttingElement, Integer> elements) {
        this.sheet = sheet;
        this.elements = elements;
    }

    /**
     * Sheet configuration
     * @return
     */
    public CuttingSheet sheet(){
        return sheet;
    }

    /**
     * Pieces to layout, with the number of each of them
     * @return
     */
    public Map<CuttingElement, Integer> elements(){
        return elements;
    }
}
