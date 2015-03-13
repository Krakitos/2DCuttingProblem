package etu.polytech.optim.api.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Morgan on 11/03/2015.
 */

/**
 * Represents a solution for the cutting problem
 */
public class CuttingSolution {

    private final Map<CuttingSheet, Integer> patterns;
    private final int patternPrice;

    public CuttingSolution(@NotNull final Map<CuttingSheet, Integer> patterns, final int patternPrice) {
        this.patterns = patterns;
        this.patternPrice = patternPrice;
    }

    /**
     * Fitness of the solution
     * Total price of the solution
     * @return
     */
    public int fitness(){
        return patterns.entrySet().stream().mapToInt(entry -> entry.getValue() * patternPrice).sum();
    }

    /**
     * Pattern and the number of time to print the pattern
     * @return
     */
    public Collection<Map.Entry<CuttingSheet, Integer>> patterns(){
        return patterns.entrySet();
    }
}