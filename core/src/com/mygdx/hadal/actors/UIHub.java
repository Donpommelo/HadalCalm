package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.event.hub.HubEvent;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.SavedLoadout;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH_TOTAL;
import static com.mygdx.hadal.utils.Constants.TRANSITION_DURATION_SLOW;

/**
 * The UiHub is an actor that pops up whenever the player interacts with hub elements that pop up a ui window
 * @author Postein Phunkykong
 */
public class UIHub {

	private final PlayState state;
	
	private final Table tableOuter, tableTop, tableSearch, tableLeft, tableRight, tableOptions, tableExtra;
	private ScrollPane options;
	private TextField searchName;
	private SelectBox<String> tagFilter, slotsFilter;
	
	//These fields pertain to the extra info window that pops up when mousing over stuff.
	private Text titleInfo;
	private String info, title = "";
	private static final int infoWidth = 400;
	private static final int infoHeight = 350;
	public static final int infoPad = 20;

	private static final float tableX = HadalGame.CONFIG_WIDTH;
	private static final float tableY = 0.0f;
	
	public static final int titleHeight = 90;
	public static final int titlePad = 25;
	
	private static final int optionsWidthOuter = 1280;
	private static final int optionsHeightOuter = 720;
	private static final int searchWidth = 240;
	public static final int optionsHeight = 40;
	public static final int optionHeight = 35;
	public static final int optionHeightLarge = 45;
	public static final int optionPad = 3;
	private static final int scrollWidth = 880;
	private static final int scrollHeight = 620;

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

		this.tableOuter = new Table() {

			@Override
			public void draw(Batch batch, float alpha) {
				GameStateManager.getSimplePatch().draw(batch, getX(), getY(), optionsWidthOuter, optionsHeightOuter);
				super.draw(batch, alpha);
			}
		};

		this.tableTop = new Table();
		this.tableSearch = new Table();

		this.tableLeft = new Table();
		this.tableExtra = new Table();

		this.tableRight = new Table();
		this.tableOptions = new Table();

		tableOuter.setTouchable(Touchable.enabled);
		addTable();
	}
	
	/**
	 * This adds the table to the stage.
	 * It is called when the actor is instantiated
	 */
	public void addTable() {
		this.info = "";
		
		titleInfo = new Text(title);
		titleInfo.setScale(0.8f);
		tableOuter.add(tableTop).height(titleHeight).colspan(2).growX().row();
		tableOuter.add(tableLeft).width(infoWidth).bottom();
		tableOuter.add(tableRight).width(scrollWidth).growY().bottom();

		tableTop.add(titleInfo).pad(titlePad).growX();
		tableTop.add(tableSearch).width(searchWidth).pad(50);

		Text extraInfo = new Text("") {

			@Override
		    public void draw(Batch batch, float alpha) {
				super.draw(batch, alpha);
				font.getData().setScale(0.3f);
				GameStateManager.getSimplePatch().draw(batch, getX(), getY(), infoWidth, infoHeight);
				font.draw(batch, info, getX() + 5, getY() + infoHeight - 25, infoWidth - 10, -1, true);
		    }
		};

		tableLeft.add(tableExtra).row();
		tableLeft.add(extraInfo).width(infoWidth).height(infoHeight).row();

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
	public void enter(boolean searchable, boolean filterTags, boolean filterCost, HubEvent hub, String... tagOptions) {
		SoundEffect.DOORBELL.play(state.getGsm(), 0.2f, false);

		active = true;

		tableOptions.clear();
		tableSearch.clear();
		tableExtra.clear();
		tableRight.clear();

		//for hubs with search bar, let players type in text
		if (searchable) {
			Text search = new Text(UIText.SEARCH.text());
			search.setScale(optionsScale);

			searchName = new TextField("", GameStateManager.getSkin()) {

				@Override
				protected InputListener createInputListener () {

					//when typing, filter results accordingly
					return new TextFieldClickListener() {

						@Override
						public boolean keyUp(InputEvent event, int keycode) {
							if (keycode == PlayerAction.EXIT_MENU.getKey()) {
								leave();
							} else {
								tableOptions.clear();
								hub.addOptions(sanitizeSearchInput(searchName.getText()), indexToFilterSlot(), indexToFilterTag());
							}
							return super.keyUp(event, keycode);
						}
					};
				}
			};
			searchName.setMessageText(UIText.SEARCH_OPTIONS.text());
			searchName.setText(hub.getLastSearch());
			tableSearch.add(search);
			tableSearch.add(searchName).padBottom(optionPad).row();
		}

		//if the player can add extra tags to search artifacts, add dropdown for this
		if (filterTags) {
			Text searchTags = new Text(UIText.FILTER_TAGS.text());
			searchTags.setScale(optionsScale);

			tagFilter = new SelectBox<>(GameStateManager.getSkin());
			tagFilter.setItems(tagOptions);

			if (hub.getLastTag() != null) {
				for (String tagName : tagOptions) {
					if (tagName.equals(hub.getLastTag().name())) {
						tagFilter.setSelected(tagName);
					}
				}
			}

			tagFilter.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					tableOptions.clear();
					hub.addOptions(sanitizeSearchInput(searchName.getText()), indexToFilterSlot(), indexToFilterTag());
				}
			});

			tableSearch.add(searchTags);
			tableSearch.add(tagFilter).padBottom(optionPad).row();
		}
		if (filterCost) {
			Text searchCost = new Text(UIText.FILTER_COST.text());
			searchCost.setScale(optionsScale);

			slotsFilter = new SelectBox<>(GameStateManager.getSkin());
			slotsFilter.setItems(UIText.FILTER_COST_OPTIONS.text().split(","));
			slotsFilter.setSelectedIndex(hub.getLastSlot() + 1);

			slotsFilter.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					tableOptions.clear();
					hub.addOptions(sanitizeSearchInput(searchName.getText()), indexToFilterSlot(), indexToFilterTag());
				}
			});

			tableSearch.add(searchCost);
			tableSearch.add(slotsFilter).padBottom(optionPad).row();
		}

		this.options = new ScrollPane(tableOptions, GameStateManager.getSkin());
		options.setFadeScrollBars(false);
		options.setScrollingDisabled(false, true);

		options.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				state.getStage().setScrollFocus(options);
			}
		});

		tableRight.add(options).height(scrollHeight).width(scrollWidth);

		tableOuter.setPosition(tableX, tableY);
		tableOuter.setSize(optionsWidthOuter, optionsHeightOuter);

		state.getStage().setScrollFocus(options);
		state.getStage().addActor(tableOuter);

		tableOuter.addAction(Actions.moveTo(tableX - optionsWidthOuter, tableY, .5f, Interpolation.pow5Out));
		
		info = "";
	}

	private int currentRow;
	private VerticalGroup currentVerticalGroup = new VerticalGroup();
	public void addActor(Actor actor, int width, int rowNum) {
		currentVerticalGroup.addActor(actor);
		currentVerticalGroup.top();
		currentVerticalGroup.setWidth(width);
		currentVerticalGroup.setHeight(scrollHeight);
		currentVerticalGroup.space(optionPad);

		currentRow++;
		if (currentRow >= rowNum) {
			currentRow = 0;

			tableOptions.add(currentVerticalGroup).growY();
			currentVerticalGroup = new VerticalGroup();
		}
	}

	public void addActorFinish() {
		tableOptions.add(currentVerticalGroup).growY();
		currentVerticalGroup = new VerticalGroup();
		currentRow = 0;
	}

	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		SoundEffect.WOOSH.play(state.getGsm(), 1.0f, 0.8f, false);

		active = false;

		ModeSettingSelection.leave(state);

		tableOuter.addAction(Actions.sequence(Actions.moveTo(tableX, tableY, TRANSITION_DURATION_SLOW, Interpolation.pow5Out),
			Actions.run(() -> {
				if (state.getStage() != null) {
					if (state.getStage().getScrollFocus() == options) {
						state.getStage().setScrollFocus(null);
					}
					state.getStage().setKeyboardFocus(null);
				}
			})
		));
	}

	/**
	 * This refreshes the ui element when a selection is made.
	 * atm, this only affects the reliquary due to having to update the artifact slots.
	 */
	public void refreshHub(HubEvent hub) {
		if (type == hubTypes.RELIQUARY) {
			refreshReliquary(hub);
		}
		if (type == hubTypes.OUTFITTER) {
			refreshOutfitter(hub);
		}
	}
	
	/**
	 * When the player equips/unequips an artifact, this is run, displaying the new artifacts and remaining slots in the info table.
	 */
	public void refreshReliquary(HubEvent hub) {
		tableExtra.clear();
		
		Text slotsTitle = new Text(UIText.CURRENT_ARTIFACTS.text());
		slotsTitle.setScale(0.5f);
		tableExtra.add(slotsTitle).colspan(12).pad(infoPad).row();
		
		boolean artifactsEmpty = true;

		if (state.getPlayer().getPlayerData() != null) {
			for (UnlockArtifact c : state.getPlayer().getPlayerData().getLoadout().artifacts) {

				//display all equipped artifacts and give option to unequip
				if (!c.equals(UnlockArtifact.NOTHING)) {
					artifactsEmpty = false;
					final ArtifactIcon newTag = new ArtifactIcon(c, UIText.UNEQUIP.text(c.getName()),
						artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);

					newTag.addListener(new ClickListener() {

						@Override
						public void clicked(InputEvent e, float x, float y) {
							if (state.isServer()) {
								state.getPlayer().getPlayerData().removeArtifact(newTag.getArtifact());
							} else {
								HadalGame.client.sendTCP(new PacketsLoadout.SyncArtifactRemoveClient(newTag.getArtifact()));
							}
							refreshHub(hub);
						}

						@Override
						public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
							super.enter(event, x, y, pointer, fromActor);
							info = UIText.ARTIFACT_INFO.text(newTag.getArtifact().getName(),
									Integer.toString(newTag.getArtifact().getArtifact().getSlotCost()),
									newTag.getArtifact().getDesc(), newTag.getArtifact().getDescLong());
						}
					});
					tableExtra.add(newTag).width(artifactTagSize).height(artifactTagSize);
				}
			}

			if (artifactsEmpty) {
				Text slotsEmpty = new Text(UIText.NA.text());
				slotsEmpty.setScale(0.5f);
				tableExtra.add(slotsEmpty).height(artifactTagSize).colspan(12);
			}

			tableExtra.row();

			Text slotsInfo = new Text(UIText.SLOTS_REMAINING.text(
					Integer.toString(state.getPlayer().getPlayerData().getArtifactSlotsRemaining())));
			slotsInfo.setScale(0.5f);
			tableExtra.add(slotsInfo).pad(infoPad).colspan(12).row();
		}
	}

	public void refreshOutfitter(HubEvent hub) {
		tableExtra.clear();
		TextField outfitName = new TextField("", GameStateManager.getSkin());
		outfitName.setMaxLength(MAX_NAME_LENGTH_TOTAL);
		outfitName.setMessageText(UIText.OUTFIT_NAME.text());

		Text outfitSave = new Text(UIText.OUTFIT_SAVE.text()).setButton(true);
		outfitSave.setScale(optionsScale);

		outfitSave.addListener(new ClickListener() {

			   @Override
			   public void clicked(InputEvent e, float x, float y) {
				if (!outfitName.getText().isEmpty()) {
					state.getGsm().getSavedOutfits().addOutfit(outfitName.getText(), new SavedLoadout(state.getGsm().getLoadout()));
					hub.enter();
					refreshHub(hub);
				}
			   }
		});

		String[] outfitOptions = new String[state.getGsm().getSavedOutfits().getOutfits().size];
		for (int i = 0; i < outfitOptions.length; i++) {
			outfitOptions[i] = state.getGsm().getSavedOutfits().getOutfits().keys().toArray().get(i);
		}

		SelectBox<String> outfits = new SelectBox<>(GameStateManager.getSkin());
		outfits.setItems(outfitOptions);

		Text outfitDelete = new Text(UIText.OUTFIT_DELETE.text()).setButton(true);
		outfitDelete.setScale(optionsScale);

		outfitDelete.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				if (outfits.getSelected() != null) {
					state.getGsm().getSavedOutfits().removeOutfit(outfits.getSelected());
				}
				hub.enter();
				refreshHub(hub);
			}
		});

		tableExtra.add(outfitName).pad(infoPad).height(optionHeightLarge);
		tableExtra.add(outfitSave).pad(infoPad).height(optionHeightLarge).row();
		tableExtra.add(outfits).pad(infoPad).height(optionHeightLarge);
		tableExtra.add(outfitDelete).pad(infoPad).height(optionHeightLarge).row();
	}

	/**
	 * Helper method that returns a tag depending on which hub event is being used
	 */
	private UnlockTag indexToFilterTag() {
		if (tagFilter == null) {
			return UnlockTag.ALL;
		} else {
			return switch (tagFilter.getSelectedIndex()) {
				default -> UnlockTag.ALL;
				case 1 -> UnlockTag.OFFENSE;
				case 2 -> UnlockTag.DEFENSE;
				case 3 -> UnlockTag.MOBILITY;
				case 4 -> UnlockTag.FUEL;
				case 5 -> UnlockTag.HEAL;
				case 6 -> UnlockTag.MAGIC;
				case 7 -> UnlockTag.AMMO;
				case 8 -> UnlockTag.WEAPON_DAMAGE;
				case 9 -> UnlockTag.PASSIVE_DAMAGE;
				case 10 -> UnlockTag.PROJECTILE_MODIFIER;
				case 11 -> UnlockTag.GIMMICK;
			};
		}
	}

	//this converts slot cost filter to actual slot cost (because 0 indexing)
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

	/**
	 * This removes problematic characters from search queries
	 */
	private String sanitizeSearchInput(String input) {
		return input.toLowerCase().replace("\\","\\\\");
	}

	public boolean isActive() { return active; }
	
	public void setInfo(String info) { this.info = info; }
	
	public void setType(hubTypes type) { this.type = type; }

	public Table getTableOptions() { return tableOptions; }

	public ScrollPane getOptions() { return options; }

	public enum hubTypes {
		NONE,
		ARMORY,
		RELIQUARY,
		DISPENSARY,
		DORMITORY,
		NAVIGATIONS,
		QUARTERMASTER,
		PAINTER,
		HABERDASHER,
		WALLPAPER,
		OUTFITTER,
		MISC
	}
}
