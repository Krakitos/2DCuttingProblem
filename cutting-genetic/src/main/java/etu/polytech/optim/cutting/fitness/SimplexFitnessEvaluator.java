package etu.polytech.optim.cutting.fitness;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.cutting.lang.GeneticSolution;
import etu.polytech.optim.genetic.fitness.FitnessEvaluator;
import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.chromosomes.FixedSizeChromosome;
import etu.polytech.optim.layout.CuttingPackager;
import etu.polytech.optim.layout.exceptions.LayoutException;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Morgan on 08/04/2015.
 */
public class SimplexFitnessEvaluator implements FitnessEvaluator<GeneticSolution> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SIMPLEX_MARKER = MarkerManager.getMarker("[SIMPLEX]");
    private static final Marker SIMPLEX_OBJ_FUNC_MARKER = MarkerManager.getMarker("[SIMPLEX_FUNC_OBJ]");
    private static final Marker SIMPLEX_CONSTRAINTS_MARKER = MarkerManager.getMarker("[SIMPLEX_CONSTRAINTS]");

    private static final Double INVALID_FITNESS = Double.MAX_VALUE;

    private final CuttingConfiguration configuration;

    private final CuttingPackager packager;

    public SimplexFitnessEvaluator(@NotNull final CuttingConfiguration configuration, @NotNull final CuttingPackager packager) {
        this.configuration = configuration;
        this.packager = packager;
    }

    @Override
    @Nullable
    public GeneticSolution computeFitness(@NotNull Chromosome c) {

        final int[] rep = getGenomes(c);
        final String repToString = Arrays.toString(rep).replaceAll(", ", "");

        GeneticSolution solution = null;

        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Computing fitness with Chromosome {}", repToString);

        try {
            if(LOGGER.isTraceEnabled())
                LOGGER.trace("Trying to layout {}", repToString);

            List<Collection<CuttingLayoutElement>> layout = packager.layout(rep);
            solution = doSimplex(layout);

            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Computed fitness for Chromosome {} is {}", repToString, solution.fitness());

        } catch (LayoutException e) {
            if(LOGGER.isTraceEnabled())
                LOGGER.trace(e.getMessage());
        }

        return solution;
    }

    /**
     * Do the Simplex Algorithm
     * @param layout
     * @return
     */
    private GeneticSolution doSimplex(final List<Collection<CuttingLayoutElement>> layout) {
        final LinearConstraintSet constraints = computeConstraints(layout);
        final LinearObjectiveFunction objectiveFunction = computeObjFunction(layout);

        double fitness = -1;
        try {
            final PointValuePair optimal = new SimplexSolver().optimize(objectiveFunction, constraints, GoalType.MINIMIZE, new NonNegativeConstraint(true));

            double[] optimalPoint = new double[optimal.getPoint().length];
            Arrays.setAll(optimalPoint, i -> Math.ceil(optimal.getPoint()[i]));

            if (LOGGER.isDebugEnabled())
                LOGGER.debug(SIMPLEX_MARKER, "Simplex done, best fit is {}", Arrays.toString(optimalPoint));


            //Represent the chromosome of the solution
            int[] rep = new int[configuration.elements().size()];

            for (int i = 0; i < optimalPoint.length; i++) {
                if(optimalPoint[i] >= 0.0d){
                    fitness += optimalPoint[i];
                    fitness += configuration.sheet().price();

                    for (CuttingLayoutElement element : layout.get(i)) {
                        ++rep[element.element().id()];
                    }
                }
            }

            return new GeneticSolution(new CuttingSolution(optimal.getPoint(), layout, fitness), new FixedSizeChromosome(rep));
        }catch (NoFeasibleSolutionException e){
            LOGGER.warn(e);
            return new GeneticSolution(new CuttingSolution(null, layout, INVALID_FITNESS));
        }

    }

    /**
     * Compute the objective function for the simplex
     * @param layout
     * @return
     */
    private LinearObjectiveFunction computeObjFunction(Collection<Collection<CuttingLayoutElement>> layout) {
        if(LOGGER.isTraceEnabled())
            LOGGER.trace(SIMPLEX_OBJ_FUNC_MARKER, "Computing objective function");

        double[] coeffs = new double[layout.size()];
        Arrays.fill(coeffs, 1d);

        if(LOGGER.isDebugEnabled())
            LOGGER.debug(SIMPLEX_OBJ_FUNC_MARKER, "Computed objective function coefs {}", Arrays.toString(coeffs));

        return new LinearObjectiveFunction(coeffs, 0);
    }

    /**
     * Compute constraints according to the layout provided
     * @param layout
     * @return
     */
    private LinearConstraintSet computeConstraints(List<Collection<CuttingLayoutElement>> layout) {
        if(LOGGER.isTraceEnabled())
            LOGGER.trace(SIMPLEX_CONSTRAINTS_MARKER, "Computing Simplex Constraints");

        final Collection<LinearConstraint> constraintList = new ArrayList<>(configuration.elements().size());

        for (CuttingElement element : configuration.elements()) {
            double[] coeffs = new double[layout.size()];

            for (int i = 0; i < layout.size(); i++) {
                Collection<CuttingLayoutElement> pattern = layout.get(i);

                coeffs[i] = pattern.parallelStream().filter(e -> e.element().id() == element.id()).count();
            }

            assert Arrays.stream(coeffs).sum() > 0.0d : "No pattern have the element " + element.id();

            constraintList.add(new LinearConstraint(coeffs, Relationship.GEQ, element.asking()));
        }

        assert constraintList.size() == configuration.elements().size() : "Invalid constraints set size "
                + constraintList.size() + " / " + configuration.elements().size();


        if(LOGGER.isDebugEnabled())
            LOGGER.debug(SIMPLEX_CONSTRAINTS_MARKER, "Computed {} constraints for {} pieces", constraintList.size(), configuration.elements().size());

        return new LinearConstraintSet(constraintList);
    }

    /**
     * Transform a Chromosome to a array of genome
     * @param c
     * @return
     */
    private static int[] getGenomes(@NotNull final Chromosome c){
        int[] rep = new int[c.length()];

        for (int i = 0; i < rep.length; i++) {
            rep[i] = c.genomeAt(i);
        }

        return rep;
    }
}
