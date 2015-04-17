package etu.polytech.optim.layout.guillotine;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.CuttingPackager;
import etu.polytech.optim.layout.exceptions.LayoutException;
import etu.polytech.optim.layout.guillotine.choice.RectChoiceHeuristic;
import etu.polytech.optim.layout.guillotine.split.SplitHeuristic;
import etu.polytech.optim.layout.lang.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Morgan on 17/04/2015.
 */
public class GuillotinePackager implements CuttingPackager {

    private final CuttingConfiguration configuration;

    private Guillotine guillotine;
    private List<Collection<CuttingLayoutElement>> patterns;

    public GuillotinePackager(CuttingConfiguration configuration, RectChoiceHeuristic rectChoice, SplitHeuristic splitter) {
        this.configuration = configuration;

        guillotine = new Guillotine(configuration.sheet().width(), configuration.sheet().height(), rectChoice, splitter);
        patterns = new ArrayList<>();
    }

    @Override
    public List<Collection<CuttingLayoutElement>> layout(@NotNull int[] generation) throws LayoutException {
        guillotine.reset();
        guillotine.addWaste(new Rectangle(0, 0, configuration.sheet().width(), configuration.sheet().height()));

        List<CuttingElement> elements = new ArrayList<>();

        configuration.elements().forEach(element -> {
            for (int i = 0; i < generation[element.id()]; i++) {
                elements.add(element);
            }
        });

        Collections.shuffle(elements);

        List<CuttingLayoutElement> pattern = new ArrayList<>();
        patterns.add(pattern);

        for (CuttingElement element : elements) {
            Optional<Rectangle> dest = guillotine.insert(element.width(), element.height());

            if(!dest.isPresent()){
                if(!patterns.contains(pattern))
                    patterns.add(pattern);

                pattern = new ArrayList<>();
                guillotine.reset();
                guillotine.addWaste(new Rectangle(0, 0, configuration.sheet().width(), configuration.sheet().height()));

                dest = guillotine.insert(element.width(), element.height());

                assert dest.isPresent() : "Unable to insert with new pattern";
            }

            Rectangle r = dest.get();

            pattern.add(new CuttingLayoutElement(r.x(), r.y(), r.width() < element.width() || r.height() < element.height(), element));
        }

        if(!patterns.contains(pattern))
            patterns.add(pattern);

        return patterns;
    }
}
