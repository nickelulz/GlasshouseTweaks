package xyz.nickelulz.glasshousetweaks.databases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public abstract class Database<T> {
    private ArrayList<T> dataset;
    private File database;
    private Gson jsonParser;
    private Type jsonTypeFrom;

    public Database(String filename, Type jsonTypeTo, Type jsonTypeFrom, Object typeAdapter) {
        this.dataset = new ArrayList<>();
        this.database = new File(GlasshouseTweaks.getInstance().getDataFolder().getAbsolutePath() +
                "/" + filename);
        this.jsonParser = new GsonBuilder().registerTypeAdapter(jsonTypeTo, typeAdapter).setPrettyPrinting().create();
        this.jsonTypeFrom = jsonTypeFrom;

        try {
            ensureExists();
        } catch (IOException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not create database file " + filename + ".");
        }

        reload();
    }

    public void ensureExists() throws IOException {
        if (!database.getParentFile().exists())
            database.getParentFile().mkdirs();
        if (!database.exists())
            database.createNewFile();
    }

    public boolean reload() {
        try {
            ensureExists();
            FileReader in = new FileReader(database);
            T[] list = jsonParser.fromJson(in, jsonTypeFrom);
            if (list == null) {
                GlasshouseTweaks.log(Level.SEVERE, "Could not parse database " +
                        database.getName() + ". List is NULL.");
                return false;
            }
            dataset = new ArrayList<>(Arrays.asList(list));
            GlasshouseTweaks.log(Level.INFO, "Loaded database " + database.getName() + ".");
            return true;
        } catch (IOException | NullPointerException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not load database " + database.getName() +
                    " due to " + e.getClass().getName());
        }
        return false;
    }

    public boolean save() {
        try {
            ensureExists();
            FileWriter out = new FileWriter(database);
            jsonParser.toJson(dataset, out);
            out.flush();
            out.close();
            GlasshouseTweaks.log(Level.INFO, "Saved database " + database.getName() + ".");
            return true;
        } catch (IOException | NullPointerException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not save database " + database.getName() +
                    " due to " + e.getClass().getName());
        }
        return false;
    }


    public int size() {
        return dataset.size();
    }

    public boolean remove(T input) {
        return dataset.remove(input);
    }

    public boolean add(T input) {
        if (contains(input) || input == null)
            return false;
        else {
            boolean success = dataset.add(input) && save();
            if (success)
                GlasshouseTweaks.log(Level.INFO, "Appended data to database " + database.getName() + ".");
            else
                GlasshouseTweaks.log(Level.WARNING, "Could not append data to database " + database.getName() + ".");
            return success;
        }
    }

    public boolean contains(T input) {
        return dataset.contains(input);
    }

    public ArrayList<T> getDataset() {
        return dataset;
    }

    public File getDatabase() {
        return database;
    }

    public Gson getJsonParser() {
        return jsonParser;
    }
}
