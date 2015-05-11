package etu.polytech.optim.cutting.lang.stop;

import etu.polytech.optim.genetic.strategies.stopping.DurationStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by Morgan on 17/04/2015.
 */
public class DurationStrategyObservable extends DurationStrategy implements StoppingConditionObservable{

    public DurationStrategyObservable(long duration, @NotNull TimeUnit unit) {
        super(duration, unit);
    }

    @Override
    public double progression() {
        return ((double)endingTime() - (double)System.nanoTime()) / (double)endingTime();
    }
}
