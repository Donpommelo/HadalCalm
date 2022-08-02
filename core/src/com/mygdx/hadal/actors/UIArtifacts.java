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

	private static final int TAG_WIDTH = 40;
	private static final int TAG_HEIGHT = 40;
	
	private static final int TABLE_X = 475;
	private static final int TABLE_Y = 10;
	
	private static final float ARTIFACT_TAG_OFFSET_X = 10.0f;
	private static final float ARTIFACT_TAG_OFFSET_Y = 45.0f;
	private static final float ARTIFACT_TAG_TARGET_WIDTH = 200.0f;

	private final PlayState state;

	private final Table table;

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
					ARTIFACT_TAG_OFFSET_X, ARTIFACT_TAG_OFFSET_Y, ARTIFACT_TAG_TARGET_WIDTH);
			newTag.setWidth(TAG_WIDTH);
			newTag.setHeight(TAG_HEIGHT);
			table.add(newTag).width(TAG_WIDTH).height(TAG_HEIGHT);
		}
	}
	
	public void addTable(Stage stage) {
		stage.addActor(table);
		table.setPosition(TABLE_X, TABLE_Y);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(TAG_HEIGHT);
		syncArtifact();
	}
}
