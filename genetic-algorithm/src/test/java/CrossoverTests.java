import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.chromosomes.FixedSizeChromosome;
import etu.polytech.optim.genetic.strategies.CrossoverPolicy;
import etu.polytech.optim.genetic.strategies.crossover.SinglePointCrossover;
import etu.polytech.optim.genetic.utils.ChromosomePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Morgan on 08/04/2015.
 */
public class CrossoverTests {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SINGLE_POINT_CROSSOVER_MARKER = MarkerManager.getMarker("[SinglePointCrossOver]");

    private static final Chromosome FIRST = new FixedSizeChromosome(new int[]{1,2,3,4,5});
    private static final Chromosome SECOND = new FixedSizeChromosome(new int[]{6,7,8,9,10});
    private static final Chromosome THIRD = new FixedSizeChromosome(new int[]{11, 12, 13, 14,15 ,16});

    private CrossoverPolicy policy;

    @Test
    public void testSinglePoint(){
        LOGGER.info(SINGLE_POINT_CROSSOVER_MARKER, "Testing ...");

        policy = new SinglePointCrossover();

        ChromosomePair pair1 = policy.crossover(new ChromosomePair(FIRST, SECOND));
        ChromosomePair pair2 = policy.crossover(new ChromosomePair(FIRST, THIRD));
        ChromosomePair pair3 = policy.crossover(new ChromosomePair(SECOND, THIRD));


        int[] rep1 = getChromosomeRep(pair1.first());
        int[] rep2 = getChromosomeRep(pair1.second());
        int[] rep3 = getChromosomeRep(pair2.first());
        int[] rep4 = getChromosomeRep(pair2.second());
        int[] rep5 = getChromosomeRep(pair3.first());
        int[] rep6 = getChromosomeRep(pair3.second());

        Assert.assertArrayEquals(rep1, new int[]{1, 2, 8, 9, 10});
        Assert.assertArrayEquals(rep2, new int[]{6, 7, 3, 4, 5});

        Assert.assertArrayEquals(rep3, new int[]{1, 2, 13, 14 ,15 , 16});
        Assert.assertArrayEquals(rep4, new int[]{11, 12, 3, 4, 5});

        Assert.assertArrayEquals(rep5, new int[]{6, 7, 13, 14, 15, 16});
        Assert.assertArrayEquals(rep6, new int[]{11, 12, 8, 9, 10});
    }

    private int[] getChromosomeRep(final Chromosome c){
        int[] rep = new int[c.length()];
        for (int i = 0; i < rep.length; i++) {
            rep[i] = c.genomeAt(i);
        }

        return rep;
    }
}
