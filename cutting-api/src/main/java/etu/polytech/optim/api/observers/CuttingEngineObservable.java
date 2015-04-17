package etu.polytech.optim.api.observers;

import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Morgan on 13/03/2015.
 */
public abstract class CuttingEngineObservable {

    private final Collection<CuttingEngineObserver> observers;

    protected CuttingEngineObservable() {
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
        observers.forEach(CuttingEngineObserver::onGenerationStarted);
    }

    /**
     * Notify about the progression of the generation
     * @param progress
     */
    protected void fireGenerationProgress(final ProgressObservable progress){
        observers.forEach(o -> o.onGenerationProgress(progress));
    }

    /**
     * Notify observers about the discovery of a better solution
     * @param fitness
     */
    protected void fireNewSolution(final long iteration, final double fitness){
        observers.forEach(o -> o.onNewSolution(iteration, fitness));
    }

    /**
     * Notify about the end of the generation
     * @param bestSolution
     */
    protected void fireGenerationFinished(@NotNull final CuttingSolution bestSolution){
        observers.forEach(o -> o.onGenerationFinished(bestSolution));
    }
}
