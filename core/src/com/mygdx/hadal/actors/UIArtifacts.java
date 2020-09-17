package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 * This ui element appears in the bottom middle of the screen and displays a list of the player's artifacts
 * These statuses are in the form of an Artifact Tag, an object that allows the player to get more info by mousing over
 * 
 * @author Zachary Tu
 */
public class UIArtifacts {

	private PlayState state;
	
	private ArrayList<ArtifactIcon> artifacts;
	private Table table; 
	
	private final int tagWidth = 25;
	private final int tagHeight = 25;
	
	private final static int tableX = 475;
	private final static int tableY = 10;
	
	private final static float artifactTagOffsetX = 10.0f;
	private final static float artifactTagOffsetY = 45.0f;
	private final static float artifactTagTargetWidth = 200.0f;
	
	public UIArtifacts(PlayState state) {
		this.state = state;
		this.table = new Table().left().top();
		
		this.artifacts = new ArrayList<ArtifactIcon>();
		
		addTable();
		
		//When starting up normally, the player's data is not loaded yet, and we do not need to sync. 
		if (state.getPlayer().getPlayerData() != null) {
			syncArtifact();
		}
	}
	
	/**
	 * This method simply synchronizes the displayed statuses with the player's current statuses.
	 */
	public void syncArtifact() {
		table.clear();
		artifacts.clear();
		if (state.getPlayer().getPlayerData() != null) {
			
			for (UnlockArtifact a : state.getPlayer().getPlayerData().getLoadout().artifacts) {
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
	private ArtifactIcon addTag(UnlockArtifact u) {
		ArtifactIcon newTag = new ArtifactIcon(u, u.getInfo().getName() + "\n" + u.getInfo().getDescription(), artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
		newTag.setWidth(tagWidth);
		newTag.setWidth(tagHeight);
		table.add(newTag).width(tagWidth).height(tagHeight);
		return newTag;
	}
	
	public void addTable() {
		state.getStage().addActor(table);
		table.setPosition(tableX, tableY);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(tagHeight);
	}
}
