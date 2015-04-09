package etu.polytech.optim.genetic.strategies.stopping;

import etu.polytech.optim.genetic.strategies.StoppingCondition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by Morgan on 08/04/2015.
 */
public class DurationStrategy implements StoppingCondition {

    private final long duration;
    private long endingTime = -1;

    public DurationStrategy(final long duration, @NotNull final TimeUnit unit) {
        this.duration = unit.toNanos(duration);
    }

    @Override
    public boolean isReached() {
        if(endingTime == -1)
            endingTime = System.nanoTime() + duration;

        return System.nanoTime() < endingTime;
    }
}
