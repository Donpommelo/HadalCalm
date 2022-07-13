package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

import static com.mygdx.hadal.managers.GameStateManager.JSON;
import static com.mygdx.hadal.managers.GameStateManager.READER;

public class SavedOutfits {

    private ObjectMap<String, SavedLoadout> outfits;

    public SavedOutfits() {
    }

    public void addOutfit(String name, SavedLoadout outfit) {
        outfits.put(name, outfit);
        saveOutfits();
    }

    public void removeOutfit(String name) {
        outfits.remove(name);
        saveOutfits();
    }

    public void saveOutfits() {
        Gdx.files.local("save/Outfits.json").writeString(JSON.prettyPrint(this), false);
    }

    public static void createNewOutfits() {
        SavedOutfits newOutfits = new SavedOutfits();
        newOutfits.outfits = new ObjectMap<>();
        Gdx.files.local("save/Outfits.json").writeString(JSON.prettyPrint(newOutfits), false);
    }

    public static SavedOutfits retrieveOutfits() {
        SavedOutfits tempOutfits;
        try {
            tempOutfits = JSON.fromJson(SavedOutfits.class, READER.parse(Gdx.files.internal("save/Outfits.json")).toJson(JsonWriter.OutputType.json));
        } catch (SerializationException e) {
            SavedOutfits.createNewOutfits();
            tempOutfits = JSON.fromJson(SavedOutfits.class, READER.parse(Gdx.files.internal("save/Outfits.json")).toJson(JsonWriter.OutputType.json));
        }
        return tempOutfits;
    }

    public ObjectMap<String, SavedLoadout> getOutfits() { return outfits; }
}
