package etu.polytech.optim.layout.guillotine.choice;

import etu.polytech.optim.layout.lang.Rectangle;

/**
 * Created by Morgan on 12/04/2015.
 */
public interface RectChoiceHeuristic {
    /**
     * Score the destination according the specified with and height
     * @param dest Rectangle where we may place the piece
     * @param width Width of the piece we want to place
     * @param height Height of the piece we want to place
     * @return
     */
    double score(final Rectangle dest, final double width, final double height);

    /**
     * Name of this heuristic
     * @return
     */
    String name();
}
