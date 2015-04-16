import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingSheet;
import etu.polytech.optim.layout.exceptions.LayoutException;
import etu.polytech.optim.layout.guillotine.choice.BestAreaFit;
import etu.polytech.optim.layout.guillotine.split.MinimizeArea;
import etu.polytech.optim.layout.packager.ShelvesPackager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by mfuntowicz on 16/04/15.
 */
public class ShelfTest {

    public static final CuttingSheet SHEET = new CuttingSheet(1400, 800, 20);
    public static final Collection<CuttingElement> ELEMENTS = new ArrayList<>();

    static{
        ELEMENTS.add(new CuttingElement(0, 800, 400, 1 + (int) (Math.random() * 9)));
        ELEMENTS.add(new CuttingElement(1, 300, 100, 1 + (int) (Math.random() * 9)));
        ELEMENTS.add(new CuttingElement(2, 200, 200, 1 + (int) (Math.random() * 9)));
    }

    public static void main(String[] args) {
        ShelvesPackager packager = new ShelvesPackager(new CuttingConfiguration(SHEET, ELEMENTS), new BestAreaFit(), new MinimizeArea());

        int[] generation = new int[ELEMENTS.size()];

        for (int i = 0; i < generation.length; i++) {
            generation[i] = 1 + (int) (Math.random() * 9);
        }

        System.out.println(Arrays.toString(generation));

        try {
            System.out.println(packager.layout(generation).size());
        } catch (LayoutException e) {
            e.printStackTrace();
        }
    }
}
