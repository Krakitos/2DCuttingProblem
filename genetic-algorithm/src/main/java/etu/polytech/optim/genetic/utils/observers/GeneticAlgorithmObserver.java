package etu.polytech.optim.genetic.utils.observers;

import etu.polytech.optim.genetic.lang.Chromosome;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface GeneticAlgorithmObserver {

    /**
     * Dispatched when the generation started
     */
    void onGenerationStarted();

    /**
     * Dispatched when a new iteration start
     */
    void onGenerationProgress();

    /**
     * Dispatched when a new solution, better than the previous, is found
     * @param iteration
     * @param chromosome
     */
    void onBetterSolutionFound(long iteration, @NotNull final Chromosome chromosome);
}
