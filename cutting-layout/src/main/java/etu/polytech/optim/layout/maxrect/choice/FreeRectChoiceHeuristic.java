package etu.polytech.optim.layout.maxrect.choice;

import etu.polytech.optim.layout.lang.Rectangle;
import org.omg.CORBA.DoubleHolder;

import java.util.List;

/**
 * Created by Morgan on 27/04/2015.
 */
public interface FreeRectChoiceHeuristic {
    Rectangle select(List<Rectangle> freeRectangles, double width, double height, DoubleHolder bestShortSideFit, DoubleHolder bestLongSideFit);
}
