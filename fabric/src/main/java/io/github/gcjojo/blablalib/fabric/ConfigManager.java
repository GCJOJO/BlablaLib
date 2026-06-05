package io.github.gcjojo.blablalib.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gcjojo.blablalib.BlablaLib;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

// Merci copislop
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/blablalib.json");

    public static BlablaLibFabricConfig config = new BlablaLibFabricConfig();

    public static void load() {
        try {
            if (!FILE.exists()) {
                save(); // crée le fichier par défaut
                return;
            }

            FileReader reader = new FileReader(FILE);
            config = GSON.fromJson(reader, BlablaLibFabricConfig.class);
            reader.close();
        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTrace -> BlablaLib.getLogger().error(stackTrace.toString()));
        }
    }

    public static void save() {
        try {
            FILE.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(config, writer);
            writer.close();
        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTrace -> BlablaLib.getLogger().error(stackTrace.toString()));
        }
    }
}
