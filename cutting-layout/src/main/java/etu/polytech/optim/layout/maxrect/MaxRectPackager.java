package etu.polytech.optim.layout.maxrect;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.AbstractCuttingPackager;
import etu.polytech.optim.layout.exceptions.LayoutException;
import etu.polytech.optim.layout.lang.Rectangle;
import etu.polytech.optim.layout.maxrect.choice.FreeRectChoiceHeuristic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.omg.CORBA.DoubleHolder;

import java.util.*;

/**
 * Created by Morgan on 27/04/2015.
 */
public class MaxRectPackager extends AbstractCuttingPackager {

    private static final Logger LOGGER = LogManager.getLogger();

    private FreeRectChoiceHeuristic choiceHeuristic;

    private List<Rectangle> usedRectangles;
    private List<Rectangle> freeRectangles;

    public MaxRectPackager(CuttingConfiguration configuration, FreeRectChoiceHeuristic choiceHeuristic) {
        super(configuration);

        this.choiceHeuristic = choiceHeuristic;
    }

    @Override
    public List<Collection<CuttingLayoutElement>> layout(@NotNull int[] generation) throws LayoutException {
        List<List<Rectangle>> patterns = new ArrayList<>();

        List<CuttingElement> remaining = new ArrayList<>();

        usedRectangles = new ArrayList<>();
        freeRectangles = new ArrayList<>();

        //Add a free rectangle which is of the size of the pattern sheet
        freeRectangles.add(new Rectangle(0, 0, binWidth(), binHeight()));


        for (CuttingElement element : configuration.elements()) {
            for (int i = 0; i < generation[element.id()]; i++) {
                remaining.add(element);
            }
        }

        assert Arrays.stream(generation).sum() == remaining.size() : "Not the same number of generated element in remaining";

        //Shuffle the list
        Collections.shuffle(remaining);

        while (!remaining.isEmpty()){

            //Using Native Type Object representation to use as reference and not value as usual with native types
            DoubleHolder bestScore1 = new DoubleHolder(Double.MAX_VALUE);
            DoubleHolder bestScore2 = new DoubleHolder(Double.MAX_VALUE);
            int bestScoreIndex = -1;

            Rectangle bestNode = null;

            for (int i = 0; i < remaining.size(); i++) {

                CuttingElement current = remaining.get(i);

                //Same as previously
                DoubleHolder score1 = new DoubleHolder(Double.MAX_VALUE);
                DoubleHolder score2 = new DoubleHolder(Double.MAX_VALUE);

                Rectangle newNode =  choiceHeuristic.select(freeRectangles, current.width(), current.height(), score1, score2);

                if(score1.value < bestScore1.value || (score1 == bestScore1 && score2.value < bestScore2.value)){
                    bestScore1 = score1;
                    bestScore2 = score2;
                    bestNode = newNode;
                    bestScoreIndex = i;
                }
            }

            //Add a new pattern, and retry
            if(bestNode == null) {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Adding new bin (occupancy : " + String.format("%.2f", occupancy() * 100.0d) + "% )");

                freeRectangles.clear();
                freeRectangles.add(new Rectangle(0, 0, binWidth(), binHeight()));

                patterns.add(usedRectangles);
                usedRectangles = new ArrayList<>();
            }else {
                int old = usedRectangles.size();

                placeRect(bestNode);

                assert usedRectangles.size() == old + 1 : "placeRect didn't add the element";

                remaining.remove(bestScoreIndex);
            }

            assert remaining.size() + patterns.stream().mapToInt(List::size).sum() + usedRectangles.size() == Arrays.stream(generation).sum() : "An element has been skip";
        }

        if(!patterns.contains(usedRectangles) && usedRectangles.size() > 0)
            patterns.add(usedRectangles);

        assert Arrays.stream(generation).sum() == patterns.parallelStream().mapToInt(List::size).sum():
                "Not the same number of elements : " + Arrays.stream(generation).sum() + "/" + patterns.parallelStream().mapToInt(List::size).sum();

        return pack(patterns);
    }

    public double occupancy(){
        double usedSurfaceArea = 0;
        for (Rectangle usedRectangle : usedRectangles) {
            usedSurfaceArea += usedRectangle.area();
        }

        return usedSurfaceArea / (binWidth() * binHeight());
    }

    /**
     * Place the rectangle into the bin
     * @param rectangle
     */
    private void placeRect(Rectangle rectangle){
        int numRectanglesToProcess = freeRectangles.size();
        for(int i = 0; i < numRectanglesToProcess; ++i)
        {
            if (splitFreeNode(freeRectangles.get(i), rectangle))
            {
                freeRectangles.remove(i);
                --i;
                --numRectanglesToProcess;
            }
        }

        pruneFreeRects();

        usedRectangles.add(rectangle);
    }

    /**
     * Indicate if the free node was split
     * @param freeNode
     * @param usedNode
     * @return
     */
    private boolean splitFreeNode(Rectangle freeNode, Rectangle usedNode){
        if (usedNode.x() >= freeNode.x() + freeNode.width() || usedNode.x() + usedNode.width() <= freeNode.x() ||
                usedNode.y() >= freeNode.y() + freeNode.height() || usedNode.y() + usedNode.height() <= freeNode.y())
            return false;

        if (usedNode.x() < freeNode.x() + freeNode.width() && usedNode.x() + usedNode.width() > freeNode.x())
        {
            // New node at the top side of the used node.
            if (usedNode.y() > freeNode.y() && usedNode.y() < freeNode.y() + freeNode.height())
            {
                Rectangle newNode = new Rectangle(freeNode.x(), freeNode.y(), freeNode.width(), usedNode.y() - freeNode.y());
                freeRectangles.add(newNode);
            }

            // New node at the bottom side of the used node.
            if (usedNode.y() + usedNode.height() < freeNode.y() + freeNode.height())
            {
                Rectangle newNode = new Rectangle(freeNode.x(), usedNode.y() + usedNode.height(), usedNode.width(), freeNode.y() + freeNode.height() - (usedNode.y() + usedNode.height()));
                freeRectangles.add(newNode);
            }
        }

        if (usedNode.y() < freeNode.y() + freeNode.height() && usedNode.y() + usedNode.height() > freeNode.y())
        {
            // New node at the left side of the used node.
            if (usedNode.x() > freeNode.x() && usedNode.x() < freeNode.x() + freeNode.width())
            {
                Rectangle newNode = new Rectangle(freeNode.x(), freeNode.y(), usedNode.x() - freeNode.x(), freeNode.height());
                freeRectangles.add(newNode);
            }

            // New node at the right side of the used node.
            if (usedNode.x() + usedNode.width() < freeNode.x() + freeNode.width())
            {
                Rectangle newNode = new Rectangle(usedNode.x() + usedNode.width(), freeNode.y(), freeNode.x() + freeNode.width() - (usedNode.x() + usedNode.width()), freeNode.height());
                freeRectangles.add(newNode);
            }
        }

        return true;
    }

    /**
     * Identify redundant entries
     */
    private void pruneFreeRects(){
        for(int i = 0; i < freeRectangles.size(); ++i) {
            for (int j = i + 1; j < freeRectangles.size(); ++j) {
                if (isContainedIn(freeRectangles.get(i), freeRectangles.get(j))) {
                    freeRectangles.remove(i);
                    --i;
                    break;
                }
                if (isContainedIn(freeRectangles.get(j), freeRectangles.get(i))) {
                    freeRectangles.remove(j);
                    --j;
                }
            }
        }
    }

    private static boolean isContainedIn(Rectangle r1, Rectangle r2){
        return r2.x() >= r1.x() && r2.y() >= r1.y() && r2.width() <= r1.width() && r2.height() <= r1.height();
    }
}
