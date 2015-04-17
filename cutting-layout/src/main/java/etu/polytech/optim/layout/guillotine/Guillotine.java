package etu.polytech.optim.layout.guillotine;

import etu.polytech.optim.layout.guillotine.choice.RectChoiceHeuristic;
import etu.polytech.optim.layout.guillotine.split.SplitHeuristic;
import etu.polytech.optim.layout.lang.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
    }

    public Optional<Rectangle> insert(final double width, final double height){
        final Optional<Rectangle> free = freeRects.parallelStream()
                .filter(r -> (r.width() >= width && r.height() >= height) || r.width() >= height && r.height() >= width) //Filter for rectangle that can hold the piece
                .collect(Collectors.groupingBy(rect -> {
                    if (rect.width() == width && rect.height() == height)
                        return Double.MIN_VALUE;
                    else if (rect.height() == width && rect.width() == height)
                        return Double.MIN_VALUE;
                    else
                        return rectChoice.score(rect, width, height);
                })) //Map score -> Rectangle[]
                .entrySet().parallelStream().sorted((o1, o2) -> Double.compare(o1.getKey(), o2.getKey())) //Order by best fitness
                .findFirst() //Get the best
                .map(best -> best.getValue().get(RANDOM.nextInt(best.getValue().size()))); //Get the a rectangle in the list

        free.map(rect -> {
            if(width > rect.width() || height > rect.height()){
                //Rotate
                return new Rectangle(rect.x(), rect.y(), height, width);
            }else{
                return new Rectangle(rect.x(), rect.y(), width, height);
            }
        }).ifPresent(placed -> {
            split(placed, free.get());
            freeRects.remove(free.get());
            merge();
        });

        return free;
    }

    public void addWaste(Rectangle r) {
        freeRects.add(r);
    }

    /**
     * Split the remaining space into two rectangles
     * @param placed
     * @param free
     */
    private void split(Rectangle placed, Rectangle free) {

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


        assert a.width() >= 0 : "Invalid a.width < 0";
        assert b.width() >= 0 : "Invalid b.width < 0";
        assert a.height() >= 0 : "Invalid a.height < 0";
        assert b.height() >= 0 : "Invalid b.height < 0";

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

                if (r.width() == r2.width() && r.x() == r2.x()) {
                    if (r.y() == r2.y() + r2.height()) {
                        freeRects.add(new Rectangle(r.x(), r.y() - r2.height(), r.width(), r.height() + r2.height()));
                        freeRects.remove(j);
                        --j;
                    } else if (r.y() + r.height() == r2.y()) {
                        freeRects.add(new Rectangle(r.x(), r.y(), r.width(), r.height() + r2.height()));
                        freeRects.remove(r2);
                        --j;
                    }
                } else if (r.height() == r2.height() && r.y() == r2.y()) {
                    if (r.x() == r2.x() + r2.width()) {
                        freeRects.add(new Rectangle(r.x()- r2.width(), r.y(), r.width() + r2.width(), r.height()));
                        freeRects.remove(r2);
                        --j;
                    } else if (r.x() + r.width() == r2.x()) {
                        freeRects.add(new Rectangle(r.x(), r.y(), r.width()+ r2.width(), r.height()));
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
     * Return the heuristic used to chose a rectangle
     * @return
     */
    public RectChoiceHeuristic rectChoiceHeuristic(){ return rectChoice; }

    /**
     * Return the heuristic used to spliter rectangles
     * @return
     */
    public SplitHeuristic splitHeuristic(){ return spliter; }
}
