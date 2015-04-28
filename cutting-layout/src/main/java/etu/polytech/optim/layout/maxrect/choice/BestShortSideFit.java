package etu.polytech.optim.layout.maxrect.choice;

import etu.polytech.optim.layout.lang.Rectangle;
import org.omg.CORBA.DoubleHolder;

import java.util.List;

import static java.lang.Math.*;

/**
 * Created by Morgan on 27/04/2015.
 */
public class BestShortSideFit implements FreeRectChoiceHeuristic {

    @Override
    public Rectangle select(List<Rectangle> freeRectangles, double width, double height, DoubleHolder bestShortSideFit, DoubleHolder bestLongSideFit) {
        Rectangle bestNode  = null;

        bestShortSideFit.value = Double.MAX_VALUE;

        for(int i = 0; i < freeRectangles.size(); ++i)
        {
            Rectangle free = freeRectangles.get(i);

            // Try to place the rectangle in upright (non-flipped) orientation.
            if (free.width() >= width && free.height() >= height)
            {
                double leftoverHoriz = abs(free.width() - width);
                double leftoverVert = abs(free.height() - height);
                double shortSideFit = min(leftoverHoriz, leftoverVert);
                double longSideFit = max(leftoverHoriz, leftoverVert);

                if (shortSideFit < bestShortSideFit.value || (shortSideFit == bestShortSideFit.value && longSideFit < bestLongSideFit.value))
                {
                    bestNode = new Rectangle(free.x(), free.y(), width, height);
                    bestShortSideFit.value = shortSideFit;
                    bestLongSideFit.value = longSideFit;
                }
            }

            if (free.width() >= height && free.height() >= width)
            {
                double flippedLeftoverHoriz = abs(free.width() - height);
                double flippedLeftoverVert = abs(free.height() - width);
                double flippedShortSideFit = min(flippedLeftoverHoriz, flippedLeftoverVert);
                double flippedLongSideFit = max(flippedLeftoverHoriz, flippedLeftoverVert);

                if (flippedShortSideFit < bestShortSideFit.value || (flippedShortSideFit == bestShortSideFit.value && flippedLongSideFit < bestLongSideFit.value))
                {
                    bestNode = new Rectangle(free.x(), free.y(), height, width);
                    bestShortSideFit.value = flippedShortSideFit;
                    bestLongSideFit.value = flippedLongSideFit;
                }
            }
        }
        return bestNode;
    }
}
