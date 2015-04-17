package etu.polytech.optim.api.observers;

import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 13/03/2015.
 */
public interface CuttingEngineObserver {
    /**
     * Indicate that a new solution is available
     * @param fitness
     */
    void onNewSolution(final long iteration, @NotNull final double fitness);

    /**
     * Indicate the start of the generation
     */
    void onGenerationStarted();

    /**
     * Indicate the end of an iteration
     * @param progressObservable
     */
    void onGenerationProgress(final ProgressObservable progressObservable);

    /**
     * Indicate the end of the generation
     * @param bestSolution
     */
    void onGenerationFinished(@NotNull final CuttingSolution bestSolution);
}
