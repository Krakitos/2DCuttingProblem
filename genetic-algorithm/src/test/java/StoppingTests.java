import etu.polytech.optim.genetic.strategies.StoppingCondition;
import etu.polytech.optim.genetic.strategies.stopping.DurationStrategy;
import etu.polytech.optim.genetic.strategies.stopping.IterationStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by Morgan on 08/04/2015.
 */
public class StoppingTests {

    @Test
    public void testIterations(){
        final int MAX_ITERATIONS = 1000;
        int iterations = 1;

        StoppingCondition stop = new IterationStrategy(MAX_ITERATIONS);
        while (!stop.isReached()){
            ++iterations;
        }

        Assert.assertEquals(iterations, MAX_ITERATIONS);
    }

    @Test
    public void testDuration(){
        final long DURATION =  5;
        final StoppingCondition stop = new DurationStrategy(DURATION, TimeUnit.SECONDS);
        final long start = System.nanoTime();

        while(!stop.isReached()){
            System.out.println("blablabla");
        }

        Assert.assertEquals(DURATION, TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start));
    }
}
