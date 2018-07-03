package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class UIStatuses {

	private AssetManager assetManager;
	
	private ArrayList<StatusTag> statuses;
	private Table table; 
	
	private final int tagWidth = 25;
	private final int tagHeight = 25;
	
	
	public UIStatuses(AssetManager assetManager, PlayState state) {
		this.assetManager = assetManager;
		this.table = new Table().left();
		
		this.statuses = new ArrayList<StatusTag>();
		
		state.getStage().addActor(table);
		table.setPosition(0, HadalGame.CONFIG_HEIGHT - 50);
		table.setWidth(HadalGame.CONFIG_WIDTH);
		table.setHeight(tagHeight);
	}
	
	public StatusTag addTag(Status s) {
		StatusTag newTag = new StatusTag(assetManager, s);
		newTag.setWidth(tagWidth);
		newTag.setWidth(tagHeight);
		table.add(newTag).width(tagWidth).height(tagHeight);
		return newTag;
	}
	
	public void addStatus(Status s) {
		if (s.isVisible()) {
			statuses.add(addTag(s));
		}
	}
	
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

	public void clearStatus() {
		table.clear();
		statuses.clear();
	}
}
