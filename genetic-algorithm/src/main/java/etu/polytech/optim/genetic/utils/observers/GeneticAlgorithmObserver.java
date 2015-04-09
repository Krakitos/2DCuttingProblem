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
     * @param index
     */
    void onGenerationNewIteration(final long index);

    /**
     * Dispatched when a new solution, better than the previous, is found
     * @param chromosome
     */
    void onBetterSolutionFound(@NotNull final Chromosome chromosome);

    /**
     * Dispatched when the algorithm generation finished
     * @param chromosome Fittest chromosome
     */
    void onGenerationFinished(@NotNull final Chromosome chromosome);
}
