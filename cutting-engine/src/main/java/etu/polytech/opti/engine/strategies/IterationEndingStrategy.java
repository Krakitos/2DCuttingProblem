package etu.polytech.opti.engine.strategies;

import etu.polytech.opti.engine.lang.GenerationState;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 13/03/2015.
 */
public class IterationEndingStrategy implements GenerationStrategy {

    private final long maxIterations;

    public IterationEndingStrategy(long maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public void handleStart() { /** Does nothing **/}

    @Override
    public boolean canContinue(@NotNull GenerationState state) {
        return state.iteration() < maxIterations;
    }
}
