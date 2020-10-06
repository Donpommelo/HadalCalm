package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

/**
 * This ui element appears in the bottom middle of the screen and displays a list of the player's artifacts
 * These statuses are in the form of an Artifact Tag, an object that allows the player to get more info by mousing over
 * 
 * @author Zachary Tu
 */
public class UIArtifacts {

	private final PlayState state;
	
	private final ArrayList<ArtifactIcon> artifacts;
	private final Table table;
	
	private static final int tagWidth = 25;
	private static final int tagHeight = 25;
	
	private static final int tableX = 475;
	private static final int tableY = 10;
	
	private static final float artifactTagOffsetX = 10.0f;
	private static final float artifactTagOffsetY = 45.0f;
	private static final float artifactTagTargetWidth = 200.0f;
	
	public UIArtifacts(PlayState state) {
		this.state = state;
		this.table = new Table().left().top();

		this.artifacts = new ArrayList<>();

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
	 * This methods adds a artifact Tag to the ui. This puts in the visible ui element.
	 * @param u: new artifact
	 * @return corresponding status tag
	 */
	private ArtifactIcon addTag(UnlockArtifact u) {
		ArtifactIcon newTag = new ArtifactIcon(u, u.getInfo().getName() + "\n" + u.getInfo().getDescription(), artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
		newTag.setWidth(tagWidth);
		newTag.setHeight(tagHeight);
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
