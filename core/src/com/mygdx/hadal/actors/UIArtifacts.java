package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
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
	}
	
	/**
	 * This method synchronizes the displayed artifacts with the player's current artifacts.
	 */
	public void syncArtifact() {
		table.clear();
		if (state.isSpectatorMode()) { return; }

		//When starting up normally, the player's data is not loaded yet, and we do not need to sync.
		//data will also be null if the player spawns in as a spectator
		if (state.getPlayer().getPlayerData() != null) {
			for (UnlockArtifact a : state.getPlayer().getPlayerData().getLoadout().artifacts) {
				if (!UnlockArtifact.NOTHING.equals(a)) {
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
		if (!u.isInvisible()) {
			ArtifactIcon newTag = new ArtifactIcon(u, u.getName() + "\n" + u.getDesc(),
				artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
			newTag.setWidth(tagWidth);
			newTag.setHeight(tagHeight);
			table.add(newTag).width(tagWidth).height(tagHeight);
		}
	}
	
	public void addTable(Stage stage) {
		stage.addActor(table);
		table.setPosition(tableX, tableY);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(tagHeight);
		syncArtifact();
	}
}
