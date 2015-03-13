package etu.polytech.optim.api.observers;

import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Morgan on 13/03/2015.
 */
public abstract class CuttingEngineObservable {
    private Collection<CuttingEngineObserver> observers;

    public CuttingEngineObservable() {
        observers = new ArrayList<>();
    }

    /**
     * Add an observers
     * @param observer
     */
    public void addObserver(@NotNull final CuttingEngineObserver observer){
        observers.add(observer);
    }

    /**
     * Remove an observers
     * @param observer
     */
    public void removeObserver(@NotNull final CuttingEngineObserver observer){
        observers.remove(observer);
    }

    /**
     * Remove all observers
     */
    public void removeObservers(){
        observers.clear();
    }

    /**
     * Notify about the starting of the generation
     */
    protected void fireGenerationStarted(){
        observers.forEach(o -> o.onGenerationStarted());
    }

    /**
     * Notify about the progression of the generation
     * @param iteration
     */
    protected void fireGenerationProgress(final int iteration){
        observers.forEach(o -> o.onGenerationProgress(iteration));
    }

    /**
     * Notify observers about the discovery of a better solution
     * @param solution
     */
    protected void fireNewSolution(@NotNull final CuttingSolution solution){
        observers.forEach(o -> o.onNewSolution(solution));
    }

    /**
     * Notify about the end of the generation
     * @param bestSolution
     */
    protected void fireGenerationFinished(@NotNull final CuttingSolution bestSolution){
        observers.forEach(o -> o.onGenerationFinished(bestSolution));
    }
}
