package etu.polytech.optim.cutting.lang.stop;

import etu.polytech.optim.genetic.strategies.stopping.IterationStrategy;

/**
 * Created by Morgan on 17/04/2015.
 */
public class IterationStrategyObservable extends IterationStrategy implements StoppingConditionObservable {

    public IterationStrategyObservable(long maxIterations) {
        super(maxIterations);
    }

    @Override
    public double progression() {
        return (double)currentIteration() / (double)maxIterations();
    }
}
