package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 * This ui element appears in the bottom middle of the screen and displays a list of the player's artifacts
 * These statuses are in the form of an Artifact Tag, an object that allows the player to get more info by mousing over
 * 
 * @author Kranella Kleffalo
 */
public class UIArtifacts {

	private final PlayState state;
	
	private final Table table;
	
	private static final int tagWidth = 40;
	private static final int tagHeight = 40;
	
	private static final int tableX = 475;
	private static final int tableY = 10;
	
	private static final float artifactTagOffsetX = 10.0f;
	private static final float artifactTagOffsetY = 45.0f;
	private static final float artifactTagTargetWidth = 200.0f;
	
	public UIArtifacts(PlayState state) {
		this.state = state;
		this.table = new Table().left().top();

		addTable();

		syncArtifact();
	}
	
	/**
	 * This method simply synchronizes the displayed statuses with the player's current statuses.
	 */
	public void syncArtifact() {
		table.clear();

		//When starting up normally, the player's data is not loaded yet, and we do not need to sync.
		//data will also be null if the player spawns in as a spectator
		if (state.getPlayer().getPlayerData() != null) {
			for (UnlockArtifact a : state.getPlayer().getPlayerData().getLoadout().artifacts) {
				if (!a.equals(UnlockArtifact.NOTHING)) {
					addTag(a);
				}
			}
		}
	}
	
	/**
	 * This methods adds a artifact Tag to the ui. This puts in the visible ui element.
	 * @param u : new artifact
	 */
	private void addTag(UnlockArtifact u) {
		ArtifactIcon newTag = new ArtifactIcon(u, u.getInfo().getName() + "\n" + u.getInfo().getDescription(),
			artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
		newTag.setWidth(tagWidth);
		newTag.setHeight(tagHeight);
		table.add(newTag).width(tagWidth).height(tagHeight);
	}
	
	public void addTable() {
		state.getStage().addActor(table);
		table.setPosition(tableX, tableY);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(tagHeight);
	}
}
