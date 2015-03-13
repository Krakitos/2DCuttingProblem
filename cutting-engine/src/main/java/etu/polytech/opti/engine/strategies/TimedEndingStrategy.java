package etu.polytech.opti.engine.strategies;

import etu.polytech.opti.engine.lang.GenerationState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by Morgan on 13/03/2015.
 */
public class TimedEndingStrategy implements GenerationStrategy{

    private final int time;
    private final TimeUnit unit;

    private long endingTime;

    public TimedEndingStrategy(final int time, @NotNull final TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }


    @Override
    public void handleStart() {
        endingTime = System.nanoTime() + unit.convert(time, TimeUnit.NANOSECONDS);
    }

    @Override
    public boolean canContinue(@NotNull final GenerationState state) {
        return System.nanoTime() < endingTime;
    }
}
