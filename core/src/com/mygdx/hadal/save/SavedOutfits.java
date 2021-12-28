package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

import static com.mygdx.hadal.managers.GameStateManager.json;
import static com.mygdx.hadal.managers.GameStateManager.reader;

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
        Gdx.files.local("save/Outfits.json").writeString(json.prettyPrint(this), false);
    }

    public static void createNewOutfits() {
        SavedOutfits newOutfits = new SavedOutfits();
        newOutfits.outfits = new ObjectMap<>();
        Gdx.files.local("save/Outfits.json").writeString(json.prettyPrint(newOutfits), false);
    }

    public static SavedOutfits retrieveOutfits() {
        SavedOutfits tempOutfits;
        try {
            tempOutfits = json.fromJson(SavedOutfits.class, reader.parse(Gdx.files.internal("save/Outfits.json")).toJson(JsonWriter.OutputType.json));
        } catch (SerializationException e) {
            SavedOutfits.createNewOutfits();
            tempOutfits = json.fromJson(SavedOutfits.class, reader.parse(Gdx.files.internal("save/Outfits.json")).toJson(JsonWriter.OutputType.json));
        }
        return tempOutfits;
    }

    public ObjectMap<String, SavedLoadout> getOutfits() { return outfits; }
}
