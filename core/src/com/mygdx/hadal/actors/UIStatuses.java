package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class UIStatuses {

	private AssetManager assetManager;
	
	private ArrayList<StatusTag> statuses;
	private Table table; 
	
	private final int tagWidth = 300;
	private final int tagHeight = 25;
	
	
	public UIStatuses(AssetManager assetManager, PlayState state, Player player) {
		this.assetManager = assetManager;
		this.table = new Table();
		
		this.statuses = new ArrayList<StatusTag>();
		
		state.getStage().addActor(table);
		table.setPosition(200, HadalGame.CONFIG_HEIGHT);
	}
	
	public StatusTag addTag(Status s) {
		StatusTag newTag = new StatusTag(assetManager, s);
		newTag.setWidth(tagWidth);
		newTag.setWidth(tagHeight);
		table.add(newTag).width(tagWidth).height(tagHeight);
		table.row();
		
		return newTag;
	}
	
	public void addStatus(Status s) {
		statuses.add(addTag(s));
	}
	
	public void removeStatus(Status s) {
		table.clear();
		
		StatusTag toRemove = null;
		
		for (StatusTag tag : statuses) {
			if (tag.getStatus().equals(s)) {
				toRemove = tag;
			} else {
				addTag(s);
			}
		}
		
		if (toRemove != null) {
			statuses.remove(toRemove);
		}
	}

	public void clearStatus() {
		table.clear();
		statuses.clear();
	}
	
}
