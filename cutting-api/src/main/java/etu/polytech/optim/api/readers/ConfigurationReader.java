package etu.polytech.optim.api.readers;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingSheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Morgan on 14/03/2015.
 */
public abstract class ConfigurationReader {

    /**
     * Read the configuration
     * @return
     * @throws IOException
     */
    public CuttingConfiguration readConfiguration() throws IOException{
        int sheetWith = 0, sheetHeight = 0;
        Map<CuttingElement, Integer> elements;

        try(BufferedReader reader = new BufferedReader(getReader())){
            for (int i = 0; i < 2; i++) {
                String[] line = reader.readLine().split("=");
                if(line.length == 2){
                    if(line[0].equalsIgnoreCase("lx"))
                        sheetWith = Integer.parseInt(line[1]);
                    else if(line[0].equalsIgnoreCase("ly"))
                        sheetHeight = Integer.parseInt(line[1]);
                    else
                        throw new IOException("Invalid format, unrecognized element " + line[0]);
                }else{
                    throw new IOException("Invalid format, missing LX/LY declaration");
                }
            }

            int nbItems = Integer.parseInt(reader.readLine().split("=")[1]);
            elements = new HashMap<>(nbItems);

            for (int i = 0; i < nbItems; i++) {
                String[] elementDeclaration = reader.readLine().split("\\t");
                if(elementDeclaration.length == 3) {
                    float eWidth = Float.parseFloat(elementDeclaration[0]);
                    float eHeight = Float.parseFloat(elementDeclaration[1]);
                    int count = Integer.parseInt(elementDeclaration[2]);

                    elements.put(new CuttingElement(eWidth, eHeight), count);
                }
            }
        }

        return new CuttingConfiguration(new CuttingSheet(sheetWith, sheetHeight), elements);
    }

    protected abstract Reader getReader() throws IOException;
}
