package etu.polytech.optim.layout.utils;

import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.CuttingPackager;
import etu.polytech.optim.layout.exceptions.LayoutException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Morgan on 12/05/2015.
 */
public class ExecutionTimeMonitor extends PackagerMonitor {

    private List<Long> executionTimes;

    public ExecutionTimeMonitor(CuttingPackager packager) {
        super(packager);

        executionTimes = new LinkedList<>();
    }

    @Override
    public List<Collection<CuttingLayoutElement>> layout(@NotNull int[] generation) throws LayoutException {

        long start = System.nanoTime();

        List<Collection<CuttingLayoutElement>> result = packager.layout(generation);

        long duration = System.nanoTime() - start;

        //Convert in ms
        long msDuration = TimeUnit.NANOSECONDS.toMillis(duration);

        executionTimes.add(msDuration);

        return result;
    }

    /**
     * Return all the summary of the stream (holding successive execution time)
     * @return
     */
    public LongSummaryStatistics statistics(){
        return executionTimes.parallelStream().collect(Collectors.summarizingLong(e -> e));
    }
    
}
