package etu.polytech.optim.layout.guillotine.choice;

import etu.polytech.optim.layout.lang.Rectangle;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Created by Morgan on 12/04/2015.
 */
public class BestShortSideFit implements RectChoiceHeuristic {

    private static final String NAME = "Best SHort Side Fit";

    @Override
    public double score(Rectangle dest, double width, double height) {
        final double leftOverH = abs(dest.width() - width);
        final double leftOverY = abs(dest.height() - height);
        return min(leftOverH, leftOverY);
    }

    @Override
    public String name() {
        return NAME;
    }
}
