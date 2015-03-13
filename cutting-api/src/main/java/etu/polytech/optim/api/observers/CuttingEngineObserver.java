package etu.polytech.optim.api.observers;

import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 13/03/2015.
 */
public interface CuttingEngineObserver {
    /**
     * Indicate that a new solution is available
     * @param solution
     */
    public void onNewSolution(@NotNull final CuttingSolution solution);

    /**
     * Indicate the start of the generation
     */
    public void onGenerationStarted();

    /**
     * Indicate the end of an iteration
     * @param iteration
     */
    public void onGenerationProgress(final int iteration);

    /**
     * Indicate the end of the generation
     * @param bestSolution
     */
    public void onGenerationFinished(@NotNull final CuttingSolution bestSolution);
}
