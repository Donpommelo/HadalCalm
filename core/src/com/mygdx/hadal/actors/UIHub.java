package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 * The UiHub is an actor that pops up whenever the player interacts with hub elements that pop up a ui window
 * @author Zachary Tu
 *
 */
public class UIHub {

	private PlayState state;
	
	private Table tableOptions, tableOuter, tableInfo, tableExtra;
	private ScrollPane options;
	
	//These fields pertain to the extra info window that pops up when mousing over stuff.
	private Text titleInfo, extraInfo;
	private String info, title = "";
	private final static int infoWidth = 400;
	private final static int infoHeight = 300;
	
	private static final int tableX = 1080;
	private static final int tableY = 120;
	
	private static final int optionsWidthOuter = 720;
	private static final int optionsHeightOuter = 510;
	private static final int optionsHeightInner = 450;
	private static final int optionsWidth = 320;
	public static final int optionsHeight = 40;
	
	public static final float optionsScale = 0.30f;
	
	private hubTypes type = hubTypes.NONE;
	
	public UIHub(PlayState state) {
		this.state = state;
		
		this.tableOptions = new Table();
		this.tableOuter = new Table();
		this.tableInfo = new Table();
		this.tableExtra = new Table();
		
		addTable();
	}
	
	/**
	 * This adds the table to the stage.
	 * It is called when the actor is instantiated
	 */
	public void addTable() {
		this.info = "";
		
		titleInfo = new Text(title, 0, 0);
		
		tableOuter.add(titleInfo).center().height(optionsHeight).colspan(2);
		tableOuter.row();
		
		extraInfo = new Text("", 0, 0) {
			
			@Override
		    public void draw(Batch batch, float alpha) {
				super.draw(batch, alpha);
				font.getData().setScale(0.30f);
				state.getGsm().getSimplePatch().draw(batch, getX(), getY(), optionsWidthOuter, optionsHeightOuter);
				state.getGsm().getSimplePatch().draw(batch, getX(), getY(), infoWidth, infoHeight);
			    font.draw(batch, info, getX(), getY() + infoHeight - 25, infoWidth, -1, true);
		    }
		};
		
		tableInfo.add(tableExtra).row();
		tableInfo.add(extraInfo).width(infoWidth).height(infoHeight);
		
		tableOuter.add(tableInfo).bottom();
		
		this.options = new ScrollPane(tableOptions, state.getGsm().getSkin());
		options.setFadeScrollBars(false);
		
		tableOuter.add(options).bottom().width(optionsWidth).height(optionsHeightInner);
	}
	
	/**
	 * This is run when the player interacts with the event. Pull up an extra menu with options specified by the child.
	 */
	public void enter() {

		tableOptions.clear();
		tableExtra.clear();
		
		tableOuter.setPosition(tableX, tableY);
		
		tableOuter.setSize(optionsWidthOuter, optionsHeightOuter);
		
		state.getStage().setScrollFocus(options);
		
		state.getStage().addActor(tableOuter);
		
		tableOuter.addAction(Actions.moveTo(tableX - optionsWidthOuter, tableY, .5f, Interpolation.pow5Out));
		
		info = "";
	}
	
	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		tableOuter.addAction(Actions.moveTo(tableX, tableY, .5f, Interpolation.pow5Out));
		
		if (state.getStage() != null) {
			if (state.getStage().getScrollFocus().equals(options)) {
				state.getStage().setScrollFocus(null);
			}
		}
	}
	
	public void refreshHub() {
		switch(type) {
		case RELIQUARY:
			refreshReliquary();
			break;
		default:
			break;
		}
	}
	
	public void refreshReliquary() {
		tableExtra.clear();
		for (UnlockArtifact c: state.getPlayer().getPlayerData().getLoadout().artifacts) {
			
			if (!c.equals(UnlockArtifact.NOTHING)) {
				final ArtifactTag newTag = new ArtifactTag(c);
				
				newTag.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						if (state.isServer()) {
							state.getPlayer().getPlayerData().removeArtifact(newTag.getArtifact());
						} else {
							state.getPlayer().getPlayerData().syncClientLoadoutRemoveArtifact(newTag.getArtifact());
						}
						
						refreshHub();
			        }
					
					@Override
					public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						info = newTag.getArtifact().getInfo().getName() + "\nCOST: " + newTag.getArtifact().getArtifact().getSlotCost() + "\n" + newTag.getArtifact().getInfo().getDescription() + " \n \n" + 
						newTag.getArtifact().getInfo().getDescriptionLong();
					}
			    });
				tableExtra.add(newTag).width(40).height(40);
			}
		}
		tableExtra.row();
		Text slotsInfo = null;
		slotsInfo = new Text("SLOTS REMAINING: " + state.getPlayer().getPlayerData().getArtifactSlotsRemaining(), 0, 0);
		
		slotsInfo.setScale(0.5f);
		tableExtra.add(slotsInfo).colspan(12).row();
	}
	
	public void setTitle(String title) { 
		this.title = title;
		titleInfo.setText(title);
	}
	
	public void setInfo(String info) { this.info = info; }
	
	public void setType(hubTypes type) { this.type = type; }

	public Table getTableOptions() { return tableOptions; }

	public Table getTableExtra() { return tableExtra; }
	
	public enum hubTypes {
		NONE,
		ARMORY,
		RELIQUARY,
		DISPENSARY,
		DORMITORY,
		NAVIGATIONS,
		QUARTERMASTER,
		CODEX
	}
}
