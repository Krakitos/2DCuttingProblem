package etu.polytech.optim.genetic.strategies.stopping;

import etu.polytech.optim.genetic.strategies.StoppingCondition;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Morgan on 08/04/2015.
 */
public class IterationStrategy implements StoppingCondition {

    private final AtomicLong iterations;
    private final long maxIterations;

    public IterationStrategy(long maxIterations) {
        this.maxIterations = maxIterations;
        this.iterations = new AtomicLong(0);
    }

    @Override
    public boolean isReached() {
        return iterations.incrementAndGet() == maxIterations;
    }

    public long maxIterations(){
        return maxIterations;
    }

    public long currentIteration(){
        return iterations.get();
    }
}
