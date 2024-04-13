package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.users.User;

import java.util.Arrays;
import java.util.Objects;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH_TOTAL;
import static com.mygdx.hadal.managers.JSONManager.JSON;
import static com.mygdx.hadal.managers.JSONManager.READER;

/**
 * A record represents the player's last loadout.
 * @author Thoroth Twoman
 */
public class SavedLoadout {

	//This is the player's current loadout that they will start with
	private String[] equip, artifact, cosmetic;
	private String active, character;
	private String team;

	//This is the player's starting name
	private String name;

	private String version;

	//the name used if the name field is left empty
	public SavedLoadout() {}

	public SavedLoadout(SavedLoadout loadout) {
		equip = Arrays.copyOf(loadout.equip, loadout.equip.length);
		artifact = Arrays.copyOf(loadout.artifact, loadout.artifact.length);
		cosmetic = Arrays.copyOf(loadout.cosmetic, loadout.cosmetic.length);
		active = loadout.active;
		character = loadout.character;
		team = loadout.team;
		name = "";
		version = loadout.version;
	}

	/**
	 * This simple saves the record in a designated file
	 */
	public void saveLoadout(User user) {
		if (null != user) {
			user.getLoadoutManager().setSavedLoadout(new Loadout(this));
		}
		Gdx.files.local("save/Loadout.json").writeString(JSON.prettyPrint(this), false);
	}
	
	/**
	 * a new loadout is created if no valid loadout is found
	 * This new loadout has default values for all fields
	 */
	public static void createAndSaveNewLoadout() {
		Gdx.files.local("save/Loadout.json").writeString(JSON.prettyPrint(createNewLoadout()), false);
	}

	public static SavedLoadout createNewLoadout() {
		SavedLoadout newLoadout = new SavedLoadout();
		newLoadout.equip = new String[Loadout.MAX_WEAPON_SLOTS];
		Arrays.fill(newLoadout.equip, "NOTHING");
		newLoadout.equip[0] = "SPEARGUN";

		newLoadout.artifact = new String[Loadout.MAX_ARTIFACT_SLOTS];
		Arrays.fill(newLoadout.artifact, "NOTHING");

		newLoadout.cosmetic = new String[Loadout.MAX_COSMETIC_SLOTS];
		Arrays.fill(newLoadout.cosmetic, "NOTHING");

		newLoadout.active = "NOTHING";
		newLoadout.character = "MOREAU";
		newLoadout.team = "NONE";
		newLoadout.name = "";
		newLoadout.version = HadalGame.VERSION;

		return newLoadout;
	}

	/**
	 * This retrieves the player's loadout at the start of the game
	 * @return the player's saved loadout (or a default loadout if file is missing or malformed)
	 */
	public static SavedLoadout retrieveLoadout() {
		SavedLoadout tempLoadout;
		try {
			tempLoadout = JSON.fromJson(SavedLoadout.class, READER.parse(Gdx.files.local("save/Loadout.json")).toJson(JsonWriter.OutputType.json));
			if (!Objects.equals(tempLoadout.version, HadalGame.VERSION) && HadalGame.SAVE_RESET) {
				SavedLoadout.createAndSaveNewLoadout();
				tempLoadout = JSON.fromJson(SavedLoadout.class, READER.parse(Gdx.files.local("save/Loadout.json")).toJson(JsonWriter.OutputType.json));
			}
		} catch (SerializationException e) {
			SavedLoadout.createAndSaveNewLoadout();
			tempLoadout = JSON.fromJson(SavedLoadout.class, READER.parse(Gdx.files.local("save/Loadout.json")).toJson(JsonWriter.OutputType.json));
		}
		if (tempLoadout.equip == null || tempLoadout.artifact == null || tempLoadout.cosmetic == null ||
				tempLoadout.active == null || tempLoadout.character == null || tempLoadout.team == null) {
			SavedLoadout.createAndSaveNewLoadout();
			tempLoadout = JSON.fromJson(SavedLoadout.class, READER.parse(Gdx.files.local("save/Loadout.json")).toJson(JsonWriter.OutputType.json));
		}
		return tempLoadout;
	}
	
	public void setEquips(User user, int index, String equip) {
		this.equip[index] = equip;
		saveLoadout(user);
	}
	
	public void setArtifact(User user, int index, String artifact) {
		this.artifact[index] = artifact;
		saveLoadout(user);
	}

	public void setCosmetic(User user, int index, String cosmetic) {
		this.cosmetic[index] = cosmetic;
		saveLoadout(user);
	}

	public void setActive(User user, String active) {
		this.active = active;
		saveLoadout(user);
	}
	
	public void setCharacter(User user, String character) {
		this.character = character;
		saveLoadout(user);
	}

	public void setTeam(User user, String team) {
		this.team = team;
		saveLoadout(user);
	}

	public void setName(String name) {
		this.name = name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH_TOTAL));
		saveLoadout(null);
	}

	public void setLoadout(User user, SavedLoadout loadout) {
		this.equip = loadout.equip;
		this.artifact = loadout.artifact;
		this.cosmetic = loadout.cosmetic;
		this.active = loadout.active;
		this.character = loadout.character;
		this.team = loadout.team;
		saveLoadout(user);
	}

	public String[] getEquip() {return equip;}

	public String[] getArtifact() { return artifact; }

	public String[] getCosmetic() { return cosmetic; }

	public String getActive() {	return active; }

	public String getCharacter() { return character; }

	public String getTeam() { return team; }

	public String getName() { return name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH_TOTAL)); }
}
