package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * This ui element appears in the top left corner of the screen and displays a list of the player's visible statuses
 * These statuses are in the form of a Status Tag, an object that allows the player to get more info by mousing over
 * 
 * This class contains the means to dynamically synchronize the player's statuses with the visible ui.
 * @author Zachary Tu
 *
 */
public class UIStatuses {

	private Player player;
	
	private AssetManager assetManager;
	
	private ArrayList<StatusTag> statuses;
	private Table table; 
	
	private final int tagWidth = 25;
	private final int tagHeight = 25;
	
	public UIStatuses(AssetManager assetManager, PlayState state, Player player) {
		this.assetManager = assetManager;
		this.player = player;
		this.table = new Table().left();
		
		this.statuses = new ArrayList<StatusTag>();
		
		state.getStage().addActor(table);
		table.setPosition(0, HadalGame.CONFIG_HEIGHT - 50);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(tagHeight);
		
		//When the player unpauses the game, this ui is reloaded, so we must resync.
		//When starting up normally, the player's data is not loaded yet, and we do not need to sync. 
		if (player.getPlayerData() != null) {
			syncStatus();
		}
	}
	
	/**
	 * This method simply synchronizes the displayed statuses with the player's current statuses.
	 */
	private void syncStatus() {
		clearStatus();
		for (Status s : player.getPlayerData().getStatuses()) {
			addStatus(s);
		}
		for (Status s : player.getPlayerData().getStatusesChecked()) {
			addStatus(s);
		}
	}
	
	/**
	 * This methods adds a Status Tag to the ui. This puts in the visible ui element.
	 * @param s: new Status
	 * @return corresponding status tag
	 */
	private StatusTag addTag(Status s) {
		StatusTag newTag = new StatusTag(assetManager, s);
		newTag.setWidth(tagWidth);
		newTag.setWidth(tagHeight);
		table.add(newTag).width(tagWidth).height(tagHeight);
		return newTag;
	}
	
	/**
	 * This method checks if a status is visible and adds it if so.
	 * @param s: new Status
	 */
	public void addStatus(Status s) {
		if (s.isVisible()) {
			statuses.add(addTag(s));
		}
	}
	
	/**
	 * This method removes a status from the list of ui elements.
	 * @param s
	 */
	public void removeStatus(Status s) {		
		ArrayList<Status> keeps= new ArrayList<Status>();

		for (StatusTag tag : statuses) {
			if (!tag.getStatus().equals(s)) {
				keeps.add(tag.getStatus());
			}
		}
		
		clearStatus();
		
		for (Status tag : keeps) {
			statuses.add(addTag(tag));
		}
	}

	/**
	 * This clears the table of status tags.
	 */
	public void clearStatus() {
		table.clear();
		statuses.clear();
	}
}
