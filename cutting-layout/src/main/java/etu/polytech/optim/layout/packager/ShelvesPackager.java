package etu.polytech.optim.layout.packager;

import com.google.common.util.concurrent.AtomicDouble;
import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.AbstractCuttingPackager;
import etu.polytech.optim.layout.exceptions.LayoutException;
import etu.polytech.optim.layout.guillotine.Guillotine;
import etu.polytech.optim.layout.guillotine.choice.RectChoiceHeuristic;
import etu.polytech.optim.layout.guillotine.split.SplitHeuristic;
import etu.polytech.optim.layout.lang.Rectangle;
import etu.polytech.optim.layout.shelf.Shelf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Import Enum as static fields *
 */

/**
 * Created by Morgan on 10/04/2015.
 */
public class ShelvesPackager extends AbstractCuttingPackager {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ArrayDeque<Shelf> shelves;
    private final Guillotine wasteMap;

    private final AtomicDouble usedArea;

    /**
     * Constructor without waste map
     * @param config

    public ShelvesPackager(@NotNull final CuttingConfiguration config) {
        this(config, null);
    }
    */

    /**
     * Constructor with waste map initialization
     * @param config
     * @param rectChoice
     * @param split
     */
    public ShelvesPackager(@NotNull final CuttingConfiguration config,
                           @NotNull final RectChoiceHeuristic rectChoice,
                           @NotNull final SplitHeuristic split){
        this(config, new Guillotine(config.sheet().width(), config.sheet().height(), rectChoice, split));
    }

    /**
     * Contructor with waste map reference
     * @param config
     * @param wastemap
     */
    public ShelvesPackager(@NotNull final CuttingConfiguration config, @NotNull Guillotine wastemap){
        super(config);

        this.shelves = new ArrayDeque<>();
        this.wasteMap = wastemap;
        this.usedArea = new AtomicDouble(0.0d);

        LOGGER.info("Initialized with waste map [{}, {}]", wastemap.rectChoiceHeuristic().name(), wastemap.splitHeuristic().name());
    }

    /**
     * Reset this packager
     */
    public void reset(){
        shelves.clear();
        wasteMap.reset();
    }

    @Override
    public Collection<Collection<CuttingLayoutElement>> layout(@NotNull final int[] generation) throws LayoutException {
        configuration
        .elements()
        .stream()
        .filter(element -> generation[element.id()] > 0)
        .forEach(element -> {
            int occurrence = generation[element.id()];

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Trying to place {} x {}", occurrence, element);

            for (int i = 0; i < occurrence; i++) {

                Rectangle dest = insert(element).orElseGet(() -> {
                    //No space in the waste map, need to add a shelf
                    //TODO : Peut être swap width et height
                    Shelf shelf = new Shelf(element.height());

                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("No waste to place {}, adding a new shelf {}", element, shelf);

                    //Add the space of the shelf in the waste map
                    wasteMap.addWaste(0d, shelves.stream().mapToDouble(Shelf::height).sum(), configuration.sheet().width(), shelf.height());

                    //Add to the shelf
                    shelves.add(shelf);

                    //Retry to insert
                    return insert(element).orElseThrow(() -> new RuntimeException("Unable to add the piece " + element + " after adding a new shelf"));
                });


                assert dest != null : "Invalid null rectangle";

                addRectToShelf(dest, element);

                /**
                 * Update used area
                 */
                usedArea.addAndGet(dest.area());
            }
        });

        return buildLayout();   
    }

    private Collection<Collection<CuttingLayoutElement>> buildLayout() {
        List<Collection<CuttingLayoutElement>> patterns = new ArrayList<>();

        List<CuttingLayoutElement> pattern = new ArrayList<>();

        double patternHeight = 0.0d;

        for(Shelf shelf : shelves){
            if(patternHeight + shelf.height() > configuration.sheet().height()){
                patterns.add(pattern);
                pattern = new ArrayList<>();
                patternHeight = 0.0d;
            }

            pattern.addAll(shelf.items());
        }

        patterns.add(pattern);

        return patterns;
    }

    private void addRectToShelf(Rectangle dest, CuttingElement element) {

        double height = 0.0d;
        Iterator<Shelf> it = shelves.descendingIterator();
        Shelf selected = null;

        while (it.hasNext() && height <= dest.y()){
            selected = it.next();
            height += selected.height();
        }

        assert selected != null : "No shelf selected ! (shelves.size() =" + shelves.size()+")";
        selected.insert(dest, element);
    }

    /**
     * Total area used
     * @return
     */
    public double usedAreaOnShelf(){
        return usedArea.get() / shelves.parallelStream().mapToDouble(shelf -> shelf.height() * configuration.sheet().width()).sum();
    }

    /**
     * Try to insert the specified element in the available space
     * @param element
     * @return
     */
    private Optional<Rectangle> insert(final CuttingElement element){
        return wasteMap.insert(element.width(), element.height());
    }

    /**
     * Utility method indicating if swapping the width and height may help to fit in the specified shelf
     * @param shelf Shelf where we want to store the piece
     * @param width Width of the piece
     * @param height Height of the piece
     * @return

    private boolean isBetterWithRotation(@NotNull Shelf shelf, final double width, final double height){
        return (width > height && width > configuration.sheet().width() - shelf.currentX()) ||
                (width > height && width < shelf.height()) ||
                (width < height && height > shelf.height() && height <= configuration.sheet().width() - shelf.currentX());
    }
    */
}
