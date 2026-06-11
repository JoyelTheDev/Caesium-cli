package dev.sim0n.caesium.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public static CaesiumConfig load(File file) throws IOException {
        return MAPPER.readValue(file, CaesiumConfig.class);
    }
}
