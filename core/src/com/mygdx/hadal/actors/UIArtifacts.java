package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This ui element appears in the top left corner of the screen and displays a list of the player's artifacts
 * These statuses are in the form of an Artifact Tag, an object that allows the player to get more info by mousing over
 * 
 * @author Zachary Tu
 *
 */
public class UIArtifacts {

	private PlayState state;
	private Player player;
	
	private ArrayList<ArtifactTag> artifacts;
	private Table table; 
	
	private final int tagWidth = 25;
	private final int tagHeight = 25;
	
	private final static int tableX = 475;
	private final static int tableY = 10;
	
	public UIArtifacts(PlayState state, Player player) {
		this.state = state;
		this.player = player;
		this.table = new Table().left().top();
		
		this.artifacts = new ArrayList<ArtifactTag>();
		
		addTable();
		
		//When starting up normally, the player's data is not loaded yet, and we do not need to sync. 
		if (player.getPlayerData() != null) {
			syncArtifact();
		}
	}
	
	/**
	 * This method simply synchronizes the displayed statuses with the player's current statuses.
	 */
	public void syncArtifact() {
		table.clear();
		artifacts.clear();
		if (player.getPlayerData() != null) {
			
			for (UnlockArtifact a : player.getPlayerData().getLoadout().artifacts) {
				if (!a.equals(UnlockArtifact.NOTHING)) {
					artifacts.add(addTag(a));
				}
			}
		}
	}
	
	/**
	 * This methods adds a Status Tag to the ui. This puts in the visible ui element.
	 * @param s: new Status
	 * @return corresponding status tag
	 */
	private ArtifactTag addTag(UnlockArtifact u) {
		ArtifactTag newTag = new ArtifactTag(u);
		newTag.setWidth(tagWidth);
		newTag.setWidth(tagHeight);
		table.add(newTag).width(tagWidth).height(tagHeight);
		return newTag;
	}
	
	public void setPlayer(Player player) { this.player = player; }
	
	public void addTable() {
		state.getStage().addActor(table);
		table.setPosition(tableX, tableY);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(tagHeight);
	}
}
