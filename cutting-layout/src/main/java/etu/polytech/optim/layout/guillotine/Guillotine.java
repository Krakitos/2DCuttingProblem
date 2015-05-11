package etu.polytech.optim.layout.guillotine;

import etu.polytech.optim.layout.guillotine.choice.RectChoiceHeuristic;
import etu.polytech.optim.layout.guillotine.split.SplitHeuristic;
import etu.polytech.optim.layout.lang.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.omg.CORBA.DoubleHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Morgan on 10/04/2015.
 */
public class Guillotine {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Random RANDOM = new Random();

    private double width;
    private double height;

    private final RectChoiceHeuristic rectChoice;
    private final SplitHeuristic spliter;

    private List<Rectangle> freeRects;


    public Guillotine(final double width, final double height, final RectChoiceHeuristic rectChoice, final SplitHeuristic split){
        this.width = width;
        this.height = height;

        this.rectChoice = rectChoice;
        this.spliter = split;

        this.freeRects = new LinkedList<>();
        freeRects.add(new Rectangle(0, 0, width, height));
    }

    public Rectangle findBestRectangle(double width, double height, DoubleHolder score){
        score.value = Double.MAX_VALUE;
        Rectangle best = null;

        for (Rectangle freeRect : freeRects) {
            if(canFit(freeRect, width, height)) {
                double freeRectScore = rectChoice.score(freeRect, width, height);
                if (freeRectScore < score.value) {
                    best = freeRect;
                    score.value = freeRectScore;
                }
            }
        }

        return best;
    }

    private boolean canFit(Rectangle freeRect, double width, double height) {
        return width <= freeRect.width && height <= freeRect.height || height <= freeRect.width && width <= freeRect.height;
    }

    public void insert(double width, double height, Rectangle selectedRect, List<Rectangle> dest) {
        boolean bestFlipped = false;
        
        // If this rectangle is a perfect match, we pick it instantly.
        if (width == selectedRect.width && height == selectedRect.height)
            bestFlipped = false;
        
        // If flipping this rectangle is a perfect match, pick that then.
        else if (height == selectedRect.width && width == selectedRect.height)
           bestFlipped = true;

        else {
            boolean canFit = width <= selectedRect.width && height <= selectedRect.height;
            boolean canFitRotated = height <= selectedRect.width && width <= selectedRect.height;

            // Try if we can fit the rectangle upright.
            if (canFit && canFitRotated) {
                //Score not rotated
                double score = rectChoice.score(selectedRect, width, height);

                //If score not rotated < score rotated, don't rotate, otherwise rotate
                if(score < rectChoice.score(selectedRect, height, width))
                    bestFlipped = false;
                else
                    bestFlipped = true;
            }else if(canFit){
                bestFlipped = false;
            }else if(canFitRotated){
                bestFlipped = true;
            }else{
                assert false : "WTF cannot fit";
            }
        }

        Rectangle newRect = new Rectangle(selectedRect.x, selectedRect.y, 0, 0);
        if(bestFlipped) {
            newRect.width = height;
            newRect.height = width;
        }else{
            newRect.width = width;
            newRect.height = height;
        }

        freeRects.remove(selectedRect);
        split(newRect, selectedRect);

        merge();

        dest.add(newRect);
    }

    /**
     * Split the remaining space into two rectangles
     * @param placed
     * @param free
     */
    public void split(Rectangle placed, Rectangle free) {

        //Adding a rectangle will result of two new empties area
        Rectangle a,b;

        SplitHeuristic.SplitDirection direction = spliter.split(free, placed);

        if(direction == SplitHeuristic.SplitDirection.HORIZONTAL){
            a = new Rectangle(free.x(), free.y() + placed.height(), free.width(), free.height() - placed.height());
            b = new Rectangle(free.x() + placed.width(), free.y(), free.width() - placed.width(), placed.height());
        }else{
            a = new Rectangle(free.x(), free.y() + placed.height(), placed.width(), free.height() - placed.height());
            b = new Rectangle(free.x() + placed.width(), free.y(), free.width() - placed.width(), free.height());
        }

        if(a.width() > 0 && a.height() > 0)
            freeRects.add(a);

        if(b.width() > 0 && b.height() > 0)
            freeRects.add(b);
    }

    /**
     * Try to merge unused rectangles
     */
    public void merge() {

        for (int i = 0; i < freeRects.size(); ++i){
            Rectangle r = freeRects.get(i);

            for (int j = i + 1; j < freeRects.size(); ++j) {
                Rectangle r2 = freeRects.get(j);

                if (r.width == r2.width && r.x == r2.x) {
                    if (r.y == r2.y + r2.height) {
                        freeRects.add(new Rectangle(r.x, r.y - r2.height, r.width, r.height + r2.height));
                        freeRects.remove(j);
                        --j;
                    } else if (r.y + r.height == r2.y) {
                        freeRects.add(new Rectangle(r.x, r.y, r.width, r.height + r2.height));
                        freeRects.remove(r2);
                        --j;
                    }
                } else if (r.height == r2.height && r.y == r2.y) {
                    if (r.x == r2.x + r2.width) {
                        freeRects.add(new Rectangle(r.x- r2.width, r.y, r.width + r2.width, r.height));
                        freeRects.remove(r2);
                        --j;
                    } else if (r.x + r.width == r2.x) {
                        freeRects.add(new Rectangle(r.x, r.y, r.width+ r2.width, r.height));
                        freeRects.remove(r2);
                        --j;
                    }
                }
            }
        }
    }

    /**
     * Reset the guillotine
     */
    public void reset() {
        freeRects.clear();
    }

    /**
     * Return the heuristic used to spliter rectangles
     * @return
     */
    public SplitHeuristic splitHeuristic(){ return spliter; }
}
