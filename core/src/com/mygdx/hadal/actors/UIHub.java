package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.event.hub.HubEvent;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.states.PlayState;

/**
 * The UiHub is an actor that pops up whenever the player interacts with hub elements that pop up a ui window
 * @author Zachary Tu
 */
public class UIHub {

	private final PlayState state;
	
	private final Table tableSearch, tableOptions, tableOuter, tableInfo, tableExtra;
	private ScrollPane options;
	private TextField searchName;
	private SelectBox<String> tagFilter, slotsFilter;
	
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
	private static final int scrollWidth = 330;

	public static final float optionsScale = 0.3f;
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

		this.tableSearch = new Table();
		this.tableOptions = new Table();
		this.tableOuter = new Table();
		this.tableInfo = new Table();
		this.tableExtra = new Table();

		tableOuter.setTouchable(Touchable.enabled);

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
		tableOuter.add(tableSearch).width(optionsWidth).height(optionsHeightInner);

		extraInfo.toBack();
		titleInfo.toFront();

		tableOuter.addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!(event.getTarget() instanceof TextField)) {
					state.getStage().setKeyboardFocus(null);
				}
				return false;
			}
		});
	}
	
	/**
	 * This is run when the player interacts with the event. Pull up an extra menu with options specified by the child.
	 */
	public void enter(boolean searchable, boolean extraFilters, HubEvent hub) {
		active = true;

		tableOptions.clear();
		tableSearch.clear();
		tableExtra.clear();

		if (searchable) {
			Text search = new Text("SEARCH: ", 0, 0, false);
			search.setScale(optionsScale);

			searchName = new TextField("", GameStateManager.getSkin()) {

				@Override
				protected InputListener createInputListener () {

					return new TextFieldClickListener() {

						@Override
						public boolean keyUp(InputEvent event, int keycode) {
							if (keycode == PlayerAction.EXIT_MENU.getKey()) {
								leave();
							} else {
								tableOptions.clear();
								hub.addOptions(searchName.getText(), indexToFilterSlot(), indexToFilterTag());
							}

							return super.keyUp(event, keycode);
						}
					};
				}
			};
			searchName.setMessageText("SEARCH OPTIONS");
			tableSearch.add(search);
			tableSearch.add(searchName).padBottom(optionsPadding).row();
		}

		if (extraFilters) {
			Text searchTags = new Text("FILTER TAGS: ", 0, 0, false);
			searchTags.setScale(optionsScale);

			tagFilter = new SelectBox<>(GameStateManager.getSkin());
			tagFilter.setItems("ALL", "OFFENSE", "DEFENSE", "MOBILITY", "FUEL", "HEAL", "ACTIVE ITEM", "AMMO",
					"WEAPON DAMAGE", "PASSIVE DAMAGE", "PROJECTILE_MODIFER", "MISC + DUMB GIMMICKS");
			tagFilter.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					tableOptions.clear();
					hub.addOptions(searchName.getText(), indexToFilterSlot(), indexToFilterTag());
				}
			});

			Text searchCost = new Text("FILTER COST: ", 0, 0, false);
			searchCost.setScale(optionsScale);

			slotsFilter = new SelectBox<>(GameStateManager.getSkin());
			slotsFilter.setItems("ALL", "0-COST", "1-COST", "2-COST", "3-COST");
			slotsFilter.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					tableOptions.clear();
					hub.addOptions(searchName.getText(), indexToFilterSlot(), indexToFilterTag());
				}
			});

			tableSearch.add(searchTags);
			tableSearch.add(tagFilter).padBottom(optionsPadding).row();
			tableSearch.add(searchCost);
			tableSearch.add(slotsFilter).padBottom(optionsPadding).row();
		}

		this.options = new ScrollPane(tableOptions, GameStateManager.getSkin());
		options.setFadeScrollBars(false);

		if (searchable) {
			tableSearch.add(options).colspan(2).expandY().width(scrollWidth);
		} else {
			tableSearch.add(options).expandY().width(scrollWidth);
		}

		tableOuter.setPosition(tableX, tableY);
		tableOuter.setSize(optionsWidthOuter, optionsHeightOuter);
		
		state.getStage().setScrollFocus(options);
		state.getStage().addActor(tableOuter);
		
		tableOuter.addAction(Actions.moveTo(tableX - optionsWidthOuter, tableY, .5f, Interpolation.pow5Out));
		
		info = "";
		
		SoundEffect.DOORBELL.play(state.getGsm(), 0.25f, false);
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
			state.getStage().setKeyboardFocus(null);
		}
		
		SoundEffect.DOORBELL.play(state.getGsm(), 0.25f, false);
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

	private UnlockTag indexToFilterTag() {
		if (tagFilter == null) {
			return UnlockTag.GIMMICK;
		} else {
			switch (tagFilter.getSelectedIndex()) {
				case 0:
				default:
					return UnlockTag.RELIQUARY;
				case 1:
					return UnlockTag.OFFENSE;
				case 2:
					return UnlockTag.DEFENSE;
				case 3:
					return UnlockTag.MOBILITY;
				case 4:
					return UnlockTag.FUEL;
				case 5:
					return UnlockTag.HEAL;
				case 6:
					return UnlockTag.ACTIVE_ITEM;
				case 7:
					return UnlockTag.AMMO;
				case 8:
					return UnlockTag.WEAPON_DAMAGE;
				case 9:
					return UnlockTag.PASSIVE_DAMAGE;
				case 10:
					return UnlockTag.PROJECTILE_MODIFIER;
				case 11:
					return UnlockTag.GIMMICK;
			}
		}
	}

	private int indexToFilterSlot() {
		if (slotsFilter == null) {
			return -1;
		} else {
			return slotsFilter.getSelectedIndex() - 1;
		}
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
		PAINTER,
		MISC
	}
}
