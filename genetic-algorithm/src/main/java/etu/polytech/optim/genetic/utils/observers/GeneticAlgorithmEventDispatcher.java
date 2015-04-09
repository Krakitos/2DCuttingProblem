package etu.polytech.optim.genetic.utils.observers;

import etu.polytech.optim.genetic.lang.Chromosome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morgan on 08/04/2015.
 */
public abstract class GeneticAlgorithmEventDispatcher {

    private final List<GeneticAlgorithmObserver> observers;

    public GeneticAlgorithmEventDispatcher() {
        observers = new ArrayList<>();
    }

    public void addObserver(@NotNull final GeneticAlgorithmObserver observer){
        observers.add(observer);
    }

    public void removeObserver(@NotNull final GeneticAlgorithmObserver observer){
        observers.remove(observer);
    }

    public void removeObservers(){
        observers.clear();
    }

    /**
     * Dispatch a notification indicating the first generation
     */
    protected void fireGenerationStarted(){
        observers.forEach(GeneticAlgorithmObserver::onGenerationStarted);
    }

    /**
     * Dispatch a notification about the start of a new iteration
     * @param iteration
     */
    protected void fireNewIteration(final long iteration){
        observers.forEach(o -> o.onGenerationNewIteration(iteration));
    }

    /**
     * Dispatch a notification indicating that a new solution has been found
     * @param chromosome
     */
    protected void fireNewSolutionFound(@NotNull final Chromosome chromosome){
        observers.forEach(o -> o.onBetterSolutionFound(chromosome));
    }

    /**
     * Dispatch the end of iterations
     * @param chromosome
     */
    protected void fireGenerationFinished(@NotNull final Chromosome chromosome){
        observers.forEach(o -> o.onGenerationFinished(chromosome));
    }
}
