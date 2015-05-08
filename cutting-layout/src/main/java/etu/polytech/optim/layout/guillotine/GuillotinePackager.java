package etu.polytech.optim.layout.guillotine;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.AbstractCuttingPackager;
import etu.polytech.optim.layout.CuttingPackager;
import etu.polytech.optim.layout.exceptions.LayoutException;
import etu.polytech.optim.layout.guillotine.choice.RectChoiceHeuristic;
import etu.polytech.optim.layout.guillotine.split.SplitHeuristic;
import etu.polytech.optim.layout.lang.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.omg.CORBA.DoubleHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Morgan on 17/04/2015.
 */
public class GuillotinePackager extends AbstractCuttingPackager {

    private RectChoiceHeuristic rectChoice;
    private SplitHeuristic splitHeuristic;

    public GuillotinePackager(CuttingConfiguration configuration, RectChoiceHeuristic rectChoice, SplitHeuristic splitHeuristic) {
        super(configuration);

        this.rectChoice = rectChoice;
        this.splitHeuristic = splitHeuristic;
    }

    @Override
    public List<Collection<CuttingLayoutElement>> layout(@NotNull int[] generation) throws LayoutException {
        List<Pattern> patterns = new ArrayList<>();
        List<CuttingElement> elements = init(generation, true);

        while(elements.size() > 0){
            DoubleHolder bestScore = new DoubleHolder(Double.MAX_VALUE);
            DoubleHolder currentScore = new DoubleHolder();

            Pattern selectedPattern = null;
            CuttingElement selectedElement = null;
            Rectangle selectedRect = null;

            for (Pattern pattern : patterns) {

                for (CuttingElement ce : elements) {
                    Rectangle best = pattern.guillotine.findBestRectangle(ce.width(), ce.height(), currentScore);
                    if(currentScore.value < bestScore.value){
                        selectedPattern = pattern;
                        selectedElement = ce;
                        selectedRect = best;

                        bestScore.value = currentScore.value;
                    }
                }
            }

            //Cannot fit the element in the guillotine, we need to create a new pattern
            if (Objects.isNull(selectedRect)) {
                patterns.add(new Pattern(binWidth(), binHeight(), rectChoice, splitHeuristic));
            }else{
                //Insert the element and split the remaining space
                selectedPattern.guillotine.insert(selectedElement.width(), selectedElement.height(), selectedRect, selectedPattern.rectangles);
                elements.remove(selectedElement);

                //assert selectedPattern.rectangles.parallelStream().mapToDouble(Rectangle::area).sum() <= binWidth() * binHeight() : "Bigger than the bin";
            }
        }

        return pack(patterns.parallelStream().map(p -> p.rectangles).collect(Collectors.toList()));
    }

    private class Pattern {
        public ArrayList<Rectangle> rectangles;
        public Guillotine guillotine;

        public Pattern(double width, double height, RectChoiceHeuristic choiceHeuristic, SplitHeuristic splitHeuristic) {
            rectangles = new ArrayList<>();
            guillotine = new Guillotine(width, height, choiceHeuristic, splitHeuristic);
        }
    }
}
