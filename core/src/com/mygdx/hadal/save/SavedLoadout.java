package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;

import java.util.Arrays;

import static com.mygdx.hadal.managers.GameStateManager.json;
import static com.mygdx.hadal.managers.GameStateManager.reader;
import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH_TOTAL;

/**
 * A record represents the player's last loadout.
 * @author Thoroth Twoman
 */
public class SavedLoadout {

	//This is the player's current loadout that they will start with
	private String[] equips;
	private String[] artifacts;
	private String active, character;
	private String team;

	//This is the player's starting name
	private String name;

	//the name used if the name field is left empty
	public SavedLoadout() {}

	public SavedLoadout(SavedLoadout loadout) {
		equips = Arrays.copyOf(loadout.equips, loadout.equips.length);
		artifacts = Arrays.copyOf(loadout.artifacts, loadout.artifacts.length);
		active = loadout.active;
		character = loadout.character;
		team = loadout.team;
		name = "";
	}

	/**
	 * This simple saves the record in a designated file
	 */
	public void saveLoadout() {
		Gdx.files.local("save/Loadout.json").writeString(json.prettyPrint(this), false);
	}
	
	/**
	 * a new loadout is created if no valid loadout is found
	 * This new loadout has default values for all fields
	 */
	public static void createNewLoadout() {
		SavedLoadout newLoadout = new SavedLoadout();

		newLoadout.equips = new String[] {"SPEARGUN", "NOTHING", "NOTHING", "NOTHING"};
		newLoadout.artifacts = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newLoadout.active = "NOTHING";
		newLoadout.character = "MOREAU";
		newLoadout.team = "NONE";
		newLoadout.name = "";
		
		Gdx.files.local("save/Loadout.json").writeString(json.prettyPrint(newLoadout), false);
	}

	/**
	 * This retrieves the player's loadout at the start of the game
	 * @return the player's saved loadout (or a default loadout if file is missing or malformed)
	 */
	public static SavedLoadout retrieveLoadout() {
		SavedLoadout tempLoadout;
		try {
			tempLoadout = json.fromJson(SavedLoadout.class, reader.parse(Gdx.files.internal("save/Loadout.json")).toJson(JsonWriter.OutputType.json));
		} catch (SerializationException e) {
			SavedLoadout.createNewLoadout();
			tempLoadout = json.fromJson(SavedLoadout.class, reader.parse(Gdx.files.internal("save/Loadout.json")).toJson(JsonWriter.OutputType.json));
		}
		return tempLoadout;
	}
	
	public void setEquips(int index, String equip) {
		this.equips[index] = equip;
		saveLoadout();
	}
	
	public void setArtifact(int index, String artifact) {
		this.artifacts[index] = artifact;
		saveLoadout();
	}
	
	public void setActive(String active) {
		this.active = active;
		saveLoadout();
	}
	
	public void setCharacter(String character) {
		this.character = character;
		saveLoadout();
	}

	public void setTeam(String team) {
		this.team = team;
		saveLoadout();
	}

	public void setName(String name) {
		this.name = name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH_TOTAL));
		saveLoadout();
	}

	public void setLoadout(SavedLoadout loadout) {
		this.equips = loadout.equips;
		this.artifacts = loadout.artifacts;
		this.active = loadout.active;
		this.character = loadout.character;
		this.team = loadout.team;
		saveLoadout();
	}

	public String[] getEquips() {return equips;}

	public String[] getArtifacts() { return artifacts; }

	public String getActive() {	return active; }

	public String getCharacter() { return character; }

	public String getTeam() { return team; }

	public String getName() { return name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH_TOTAL)); }
}
