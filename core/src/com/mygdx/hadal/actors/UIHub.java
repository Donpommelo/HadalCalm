package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 * The UiHub is an actor that pops up whenever the player interacts with hub elements that pop up a ui window
 * @author Zachary Tu
 */
public class UIHub {

	private final PlayState state;
	
	private final Table tableOptions, tableOuter, tableInfo, tableExtra;
	private ScrollPane options;
	
	//These fields pertain to the extra info window that pops up when mousing over stuff.
	private Text titleInfo;
	private String info, title = "";
	private static final int infoWidth = 400;
	private static final int infoHeight = 350;
	public static final int infoPadding = 20;

	private static final float tableX = HadalGame.CONFIG_WIDTH;
	private static final float tableY = 50.0f;
	
	public static final int titleHeight = 60;
	public static final int titlePadding = 25;
	
	private static final int optionsWidthOuter = 720;
	private static final int optionsHeightOuter = 580;
	private static final int optionsHeightInner = 520;
	private static final int optionsWidth = 320;
	public static final int optionsHeight = 40;
	public static final int optionsPadding = 10;
	
	public static final float optionsScale = 0.40f;
	public static final float optionsScaleSmall = 0.25f;
	
	public static final float artifactTagSize = 50.0f;
	private static final float artifactTagOffsetX = 10.0f;
	private static final float artifactTagOffsetY = 60.0f;
	private static final float artifactTagTargetWidth = 120.0f;
			
	private hubTypes type = hubTypes.NONE;
	
	//is this window currently visible?
	private boolean active;
		
	public UIHub(PlayState state) {
		this.state = state;
		this.active = false;
		
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
		
		titleInfo = new Text(title, 0, 0, false);
		titleInfo.setScale(0.8f);
		
		tableOuter.add(titleInfo).pad(titlePadding).height(titleHeight).colspan(2);
		tableOuter.row();
		
		Text extraInfo = new Text("", 0, 0, false) {
			
			@Override
		    public void draw(Batch batch, float alpha) {
				super.draw(batch, alpha);
				font.getData().setScale(0.30f);
				GameStateManager.getSimplePatch().draw(batch, getX(), getY(), optionsWidthOuter, optionsHeightOuter);
				GameStateManager.getSimplePatch().draw(batch, getX(), getY(), infoWidth, infoHeight);
			    font.draw(batch, info, getX() + 5, getY() + infoHeight - 25, infoWidth - 10, -1, true);
		    }
		};
		
		tableInfo.add(tableExtra).row();
		tableInfo.add(extraInfo).width(infoWidth).height(infoHeight);
		
		tableOuter.add(tableInfo).bottom();
		
		this.options = new ScrollPane(tableOptions, GameStateManager.getSkin());
		options.setFadeScrollBars(false);
		
		tableOuter.add(options).bottom().width(optionsWidth).height(optionsHeightInner);
		
		extraInfo.toBack();
		titleInfo.toFront();
	}
	
	/**
	 * This is run when the player interacts with the event. Pull up an extra menu with options specified by the child.
	 */
	public void enter() {
		active = true;
		
		tableOptions.clear();
		tableExtra.clear();
		
		tableOuter.setPosition(tableX, tableY);
		
		tableOuter.setSize(optionsWidthOuter, optionsHeightOuter);
		
		state.getStage().setScrollFocus(options);
		
		state.getStage().addActor(tableOuter);
		
		tableOuter.addAction(Actions.moveTo(tableX - optionsWidthOuter, tableY, .5f, Interpolation.pow5Out));
		
		info = "";
		
		SoundEffect.DOORBELL.play(state.getGsm(), 0.4f, false);
	}
	
	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		active = false;

		tableOuter.addAction(Actions.moveTo(tableX, tableY, .5f, Interpolation.pow5Out));
		
		if (state.getStage() != null) {
			if (state.getStage().getScrollFocus() == options) {
				state.getStage().setScrollFocus(null);
			}
		}
		
		SoundEffect.DOORBELL.play(state.getGsm(), 0.4f, false);
	}
	
	/**
	 * This refreshes the ui element when a selection is made.
	 * atm, this only affects the reliquary due to having to update the artifact slots.
	 */
	public void refreshHub() {
		if (type == hubTypes.RELIQUARY) {
			refreshReliquary();
		}
	}
	
	/**
	 * When the player equips/unequips an artifact, this is run, displaying the new artifacts and remaining slots in the info table.
	 */
	public void refreshReliquary() {
		tableExtra.clear();
		
		Text slotsTitle = new Text("CURRENT ARTIFACTS:", 0, 0, false);
		slotsTitle.setScale(0.5f);
		tableExtra.add(slotsTitle).colspan(12).pad(infoPadding).row();
		
		boolean artifactsEmpty = true;
		
		for (UnlockArtifact c: state.getPlayer().getPlayerData().getLoadout().artifacts) {
			
			if (!c.equals(UnlockArtifact.NOTHING)) {
				artifactsEmpty = false;
				final ArtifactIcon newTag = new ArtifactIcon(c, "UNEQUIP?\n" + c.getInfo().getName(), artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
				
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
				tableExtra.add(newTag).width(artifactTagSize).height(artifactTagSize);
			}
		}
		if (artifactsEmpty) {
			Text slotsEmpty = new Text("N / A", 0, 0, false);
			slotsEmpty.setScale(0.5f);
			tableExtra.add(slotsEmpty).height(artifactTagSize).colspan(12);
		}
		
		tableExtra.row();
		
		Text slotsInfo = new Text("SLOTS REMAINING: " + state.getPlayer().getPlayerData().getArtifactSlotsRemaining(), 0, 0, false);
		slotsInfo.setScale(0.5f);
		tableExtra.add(slotsInfo).pad(infoPadding).colspan(12).row();
	}
	
	/**
	 * This sets the title text when the player enters the hub event
	 */
	public void setTitle(String title) { 
		this.title = title;
		titleInfo.setText(title);
	}
	
	public boolean isActive() { return active; }
	
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
		CODEX,
		MISC
	}
}
