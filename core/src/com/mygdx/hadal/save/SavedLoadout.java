package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A record represents the player's last loadout.
 * @author Zachary Tu
 *
 */
public class SavedLoadout {

	//This is the player's current loadout that they will start with
	private String[] equips;
	private String[] artifacts;
	private String active, character;
	
	//This is the player's starting name
	private String name;

	//max size of name createable in title screen
	private static final int maxNameLength = 50;
	
	public SavedLoadout() {}
	
	/**
	 * This simple saves the record in a designated file
	 */
	public void saveLoadout() {
		Gdx.files.local("save/Loadout.json").writeString(GameStateManager.json.prettyPrint(this), false);
	}
	
	/**
	 * a new loadout is created if no valid loadout is found
	 * This new loadout has default values for all fields
	 */
	public static void createNewLoadout() {
		SavedLoadout newLoadout = new SavedLoadout();

		newLoadout.equips = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newLoadout.artifacts = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newLoadout.active = "NOTHING";
		newLoadout.character = "MOREAU";
		newLoadout.name = "";
		
		Gdx.files.local("save/Loadout.json").writeString(GameStateManager.json.prettyPrint(newLoadout), false);
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
	
	public void setName(String name) {
		this.name = name.substring(0, Math.min(name.length(), maxNameLength));
		saveLoadout();
	}
	
	public String[] getEquips() {return equips;}

	public String[] getArtifacts() { return artifacts; }

	public String getActive() {	return active; }

	public String getCharacter() { return character; }

	public String getName() { return name.substring(0, Math.min(name.length(), maxNameLength)); }
}
