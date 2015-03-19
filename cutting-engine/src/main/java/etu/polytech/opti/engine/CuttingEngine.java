package etu.polytech.opti.engine;

import etu.polytech.opti.engine.lang.GenerationState;
import etu.polytech.opti.engine.strategies.GenerationStrategy;
import etu.polytech.opti.layout.CuttingLayoutManager;
import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.api.lang.CuttingSheet;
import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.api.observers.CuttingEngineObservable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Morgan on 13/03/2015.
 */
public final class CuttingEngine extends CuttingEngineObservable {

    private static final Marker GENERATION_MARKER = MarkerManager.getMarker("[GENERATION]");
    private static final Logger LOGGER = LogManager.getLogger();


    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * Entry point for the solution generation
     * @param generator
     * @return
     */
    public Future<?> generateSolution(@NotNull final CuttingGenerator generator){
        return EXECUTOR_SERVICE.submit(new CuttingTask(generator));
    }

    /**
     * Thread task to run the generation
     */
    private class CuttingTask implements Runnable{

        private final CuttingGenerator generator;

        private CuttingSolution bestSolution;

        private CuttingTask(@NotNull final CuttingGenerator generator) {
            this.generator = generator;
        }

        @Override
        public void run() {
            fireGenerationStarted();

            generator.forEachRemaining((solution) -> {
                fireGenerationProgress(generator.state().iteration().get());

                if(bestSolution == null || bestSolution.fitness() < solution.fitness()) {
                    bestSolution = solution;

                    LOGGER.info(GENERATION_MARKER, "New solution found (fitness : {})", bestSolution.fitness());

                    fireNewSolution(solution);
                }
            });

            fireGenerationFinished(bestSolution);
        }
    }

}
