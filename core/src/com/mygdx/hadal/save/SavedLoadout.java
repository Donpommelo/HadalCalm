package com.mygdx.hadal.save;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.managers.GameStateManager;

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

	//max size of name creatable in title screen
	public static final int maxNameLength = 40;
	
	//the name used if the nme field is left empty
	private static final String defaultName = "Anonymous";
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

		newLoadout.equips = new String[] {"SPEARGUN", "NOTHING", "NOTHING", "NOTHING"};
		newLoadout.artifacts = new String[] {"NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING", "NOTHING"};
		newLoadout.active = "NOTHING";
		newLoadout.character = "MOREAU";
		newLoadout.team = "NONE";
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

	public void setTeam(String team) {
		this.team = team;
		saveLoadout();
	}

	public void setName(String name) {
		
		//prevent players from entering with no name
		if (name.isEmpty()) {
			this.name = defaultName;
		} else {
			this.name = name.substring(0, Math.min(name.length(), maxNameLength));
		}
		
		saveLoadout();
	}
	
	public String[] getEquips() {return equips;}

	public String[] getArtifacts() { return artifacts; }

	public String getActive() {	return active; }

	public String getCharacter() { return character; }

	public String getTeam() { return team; }

	public String getName() { return name.substring(0, Math.min(name.length(), maxNameLength)); }
}
