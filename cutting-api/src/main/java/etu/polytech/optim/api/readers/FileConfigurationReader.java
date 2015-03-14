package etu.polytech.optim.api.readers;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by Morgan on 14/03/2015.
 */
public class FileConfigurationReader extends ConfigurationReader {

    private final File file;

    public FileConfigurationReader(@NotNull final File file){
        this.file = file;
    }

    @Override
    protected Reader getReader() throws IOException {
        return new FileReader(file);
    }
}
