package etu.polytech.optim.genetic;

import etu.polytech.opti.engine.CuttingGenerator;
import etu.polytech.opti.engine.strategies.GenerationStrategy;
import etu.polytech.opti.layout.exceptions.LayoutException;
import etu.polytech.optim.api.lang.CuttingSolution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 19/03/2015.
 */
public class GeneticCuttingGenerator extends CuttingGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int INVALID_LAYOUT_FITNESS = Integer.MIN_VALUE;

    private final BiasedWheel<int[][]> biasedWheel = new BiasedWheel<>();
    private final int[][] generation;

    public GeneticCuttingGenerator(@NotNull GenerationStrategy strategy) {
        super(strategy);
        generation = generateInitial();
    }

    @Override
    public CuttingSolution next() {
        CuttingSolution solution = null;

        do{

        }while (evaluate() == INVALID_LAYOUT_FITNESS);

        return solution;
    }

    private int[][] generateInitial() {
        return new int[0][];
    }

    /**
     * Evaluate the current solution
     * @return
     */
    private int evaluate(){
        try {
            doLayout(generation);
            return 0;
        }catch (LayoutException e){
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Select two solutions on the biased wheel
     * @return
     */
    private int[][][] selection(){
        int[][][] selection = new int[2][][];
        selection[0] = biasedWheel.getRandom();
        selection[1] = biasedWheel.getRandom();
        return selection;
    }

    /**
     * Cross over two solutions to make two new ones
     * @return
     */
    private int[][][] crossover(int[][][] selection){
        return null;
    }

    /**
     * Mutate the selected sample to make a new one
     * @param selection
     * @return
     */
    private int[][] mutate(int[][][] selection){
        return null;
    }
}
