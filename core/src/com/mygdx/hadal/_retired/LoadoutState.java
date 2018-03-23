package com.mygdx.hadal._retired;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockEquip.EquipTag;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.GameState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.TitleState;

/**
 * The MenuState is pulled up by pausing in game.
 * @author Zachary Tu
 *
 */
public class LoadoutState extends GameState {

	private Stage stage;
		
	private Actor exitOption, playOption;

	private ScrollPane options;
	
	private static Map<String, String> levels = new HashMap<String, String>();
	
	private Array<Text> slotButtons = new Array<Text>();
	private Array<Text> artifactButtons = new Array<Text>();
	
	private Text characterSelect, levelSelect;
	
	private PlayState playState;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public LoadoutState(final GameStateManager gsm) {
		super(gsm);
		
		playState = new PlayState(gsm, gsm.getLoadout(), UnlockLevel.HUB, false);	
		
		levels.clear();
		levels.put("Maps/test_map_large.tmx", "Level_1");
		levels.put("Maps/tutorial.tmx", "Tutorial");
		levels.put("Maps/test_map.tmx", "Sandbox");
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				exitOption = new Text(HadalGame.assetManager, "EXIT?", 100, HadalGame.CONFIG_HEIGHT - 260, Color.WHITE);
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(LoadoutState.class);
			        }
			    });
				exitOption.setScale(0.5f);	
				
				playOption = new Text(HadalGame.assetManager, "PLAY?",  100, HadalGame.CONFIG_HEIGHT - 350, Color.WHITE);
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(LoadoutState.class);
			        	getGsm().addState(State.PLAY, TitleState.class);
			        }
			    });
				playOption.setScale(0.5f);
				
				characterSelect = new Text(HadalGame.assetManager, "",  200, HadalGame.CONFIG_HEIGHT - 250, Color.WHITE);
				characterSelect.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getCharOptions();
			        }
				});
				
				levelSelect = new Text(HadalGame.assetManager, "",  200, HadalGame.CONFIG_HEIGHT - 300, Color.WHITE);
				levelSelect.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getLevelOptions();
			        }
				});
				
				for (int i = 0; i < Loadout.getNumSlots(); i++) {
					
					final int slotNum = i;
					
					Text nextSlot = new Text(HadalGame.assetManager, "", 200, HadalGame.CONFIG_HEIGHT - 350  - 50 * i, Color.WHITE);
					nextSlot.addListener(new ClickListener() {
							public void clicked(InputEvent e, float x, float y) {
								getEquipOptions(slotNum);
//								playState.getPlayer().getPlayerData().hardSwitchWeapon(slotNum + 1);
							}
					});
					nextSlot.setScale(0.5f);
					slotButtons.add(nextSlot);
					addActor(nextSlot);
				};
				
/*				for (int i = 0; i < Loadout.getNumArtifacts(); i++) {
					
					final int slotNum = i;
					
					Text nextSlot = new Text(HadalGame.assetManager, "", 200, HadalGame.CONFIG_HEIGHT - 600  - 50 * i, Color.WHITE);
					nextSlot.addListener(new ClickListener() {
							public void clicked(InputEvent e, float x, float y) {
								getArtifactOptions(slotNum);
							}
					});
					nextSlot.setScale(0.5f);
					artifactButtons.add(nextSlot);
					addActor(nextSlot);
				};*/
							
				addActor(exitOption);				
				addActor(playOption);				
				addActor(characterSelect);				
				addActor(levelSelect);				
			}
		};
		refreshLoadout();
		
		app.newMenu(stage);
	}
	
	public void getEquipOptions(final int slot) {
		
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup weapons = new VerticalGroup().space(10);
		
		weapons.addActor(new Text(HadalGame.assetManager, "WEAPON SLOT: " + slot, 0, 0));
		
		for (UnlockEquip c: UnlockEquip.getUnlocks(true, EquipTag.ARMORY)) {
			
			final UnlockEquip selected = c;
			
			Text itemChoose = new Text(HadalGame.assetManager, selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {
		        	getGsm().getLoadout().multitools[slot] = selected;
		        	
		        	playState.getPlayer().getPlayerData().replaceSlot(selected, slot);
		        	
		        	refreshLoadout();

		        }
		    });
			itemChoose.setScale(0.75f);
			weapons.addActor(itemChoose);
		}
		
		options = new ScrollPane(weapons, getGsm().getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
		
	}
	
	public void getArtifactOptions(final int slot) {
		
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup items = new VerticalGroup().space(10);
		
		items.addActor(new Text(HadalGame.assetManager, "ARTIFACT: " + slot, 0, 0));
		
		for (UnlockArtifact c: UnlockArtifact.getUnlocks()) {
			
			final UnlockArtifact selected = c;
			
			Text itemChoose = new Text(HadalGame.assetManager, selected.getName() , 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

	/*	        	if (!Arrays.asList(getGsm().getLoadout().artifacts).contains(selected)) {
		        		getGsm().getLoadout().artifacts[slot] = selected;
		        		
		        		playState.getPlayer().getPlayerData().replaceSlot(selected, slot);
		        		
			        	refreshLoadout();
		        	}*/
		        }
		    });
			itemChoose.setScale(0.75f);
			items.addActor(itemChoose);
		}
		
		options = new ScrollPane(items, getGsm().getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
		
	}

	public void getCharOptions() {
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup people = new VerticalGroup().space(10);
		
		people.addActor(new Text(HadalGame.assetManager, "CHARACTERS", 0, 0));
		
		for (UnlockCharacter c: UnlockCharacter.getUnlocks()) {
			
			final UnlockCharacter selected = c;
			
			Text itemChoose = new Text(HadalGame.assetManager, selected.getName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

		        	getGsm().getLoadout().character = selected;

		        	playState.getPlayer().setBodySprite(selected.getSprite());
		        	
		        	refreshLoadout();

		        }
		    });
			itemChoose.setScale(0.75f);
			people.addActor(itemChoose);
		}
		options = new ScrollPane(people, getGsm().getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
	}
	
	public void getLevelOptions() {
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup people = new VerticalGroup().space(10);
		
		people.addActor(new Text(HadalGame.assetManager, "LEVELS", 0, 0));
		
		for (String s : levels.keySet()) {
			
			final String selected = s;
			
			Text itemChoose = new Text(HadalGame.assetManager, levels.get(s) , 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

		        	getGsm().setLevel(UnlockLevel.valueOf(selected));
		        	refreshLoadout();

		        }
		    });
			
			people.addActor(itemChoose);
		}
		options = new ScrollPane(people, getGsm().getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
	}
	
	public void refreshLoadout() {
		
		characterSelect.setText("Character: " + getGsm().getLoadout().character.getName());
		levelSelect.setText("Level: " + getGsm().getLevel());
		
		for (int i = 0; i < slotButtons.size; i++) {
			if (getGsm().getLoadout().multitools[i] != null) {
				slotButtons.get(i).setText("SLOT " + (i + 1) + ": " + getGsm().getLoadout().multitools[i].getName());
			} else {
				slotButtons.get(i).setText("SLOT " + (i + 1) + ": EMPTY");
			}
		}
		
		for (int i = 0; i < artifactButtons.size; i++) {
/*			if (getGsm().getLoadout().artifacts[i] != null) {
				artifactButtons.get(i).setText("ARTIFACT " + (i + 1) + ": " + 
			getGsm().getLoadout().artifacts[i].getName() + ": " + getGsm().getLoadout().artifacts[i].getDescr());
			} else {
				artifactButtons.get(i).setText("ARTIFACT " + (i + 1) + ": EMPTY");
			}*/
		}
		if (options != null) {
			options.remove();
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void update(float delta) {
		playState.update(delta);
	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		playState.render();
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		playState.dispose();
		stage.dispose();
	}

}
