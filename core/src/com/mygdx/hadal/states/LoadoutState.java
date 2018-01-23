package com.mygdx.hadal.states;

import java.util.Arrays;
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
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.*;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * The MenuState is pulled up by pausing in game.
 * @author Zachary Tu
 *
 */
public class LoadoutState extends GameState {

	private Stage stage;
		
	private Actor exitOption, playOption;

	private ScrollPane options;
	
	private static Array<Equipable> items = new Array<Equipable>();
	private static Array<Artifact> artifacts = new Array<Artifact>();
	private static Map<String, String> characters = new HashMap<String, String>();
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
		
		playState = new PlayState(gsm, gsm.getLoadout(), "Maps/test_map.tmx", 0.40f, false);
		
		items.clear();
		items.add(new Boomerang(null));
		items.add(new BouncingBlade(null));
		items.add(new ChargeBeam(null));
		items.add(new GrenadeLauncher(null));
		items.add(new IronBallLauncher(null));
		items.add(new Machinegun(null));
		items.add(new MomentumShooter(null));
		items.add(new Scattergun(null));
		items.add(new Speargun(null));
		items.add(new Scrapripper(null));
		items.add(new TorpedoLauncher(null));
		
		artifacts.clear();
		artifacts.add(new EelskinCover());
		artifacts.add(new GoodHealth());
		artifacts.add(new ImprovedHovering());
		artifacts.add(new ImprovedPropulsion());
		artifacts.add(new LoamskinTalisman());
		artifacts.add(new NiceShoes());
		artifacts.add(new SkateWings());
		artifacts.add(new RecklessMark());
		artifacts.add(new NuclearPunchThrusters());
		artifacts.add(new RootBoots());
		artifacts.add(new TriggerFinger());
		artifacts.add(new RingofTesting());
		
		
		characters.clear();
		characters.put(AssetList.PLAYER_MOREAU_ATL.toString(), "Moreau");
		characters.put(AssetList.PLAYER_TAKA_ATL.toString(), "Takanori");
		characters.put(AssetList.PLAYER_TELE_ATL.toString(), "Telemachus");
		
		levels.clear();
		levels.put("Maps/test_map_large.tmx", "Level_1");
		levels.put("Maps/tutorial.tmx", "Tutorial");
		levels.put("Maps/test_map.tmx", "Sandbox?");
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				exitOption = new Text(HadalGame.assetManager, "EXIT?", 100, HadalGame.CONFIG_HEIGHT - 260, Color.WHITE);
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState(LoadoutState.class);
			        }
			    });
				exitOption.setScale(0.5f);	
				
				playOption = new Text(HadalGame.assetManager, "PLAY?",  100, HadalGame.CONFIG_HEIGHT - 350, Color.WHITE);
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState(LoadoutState.class);
			        	gsm.addState(State.PLAY, TitleState.class);
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
								playState.getPlayer().getPlayerData().hardSwitchWeapon(slotNum + 1);
							}
					});
					nextSlot.setScale(0.5f);
					slotButtons.add(nextSlot);
					addActor(nextSlot);
				};
				
				for (int i = 0; i < Loadout.getNumArtifacts(); i++) {
					
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
				};
							
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
		
		VerticalGroup weapons = new VerticalGroup();
		
		weapons.addActor(new Text(HadalGame.assetManager, "WEAPON SLOT: " + slot, 0, 0));
		
		for (Equipable c: items) {
			
			final Equipable selected = c;
			
			Text itemChoose = new Text(HadalGame.assetManager, selected.name , 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

		        	gsm.getLoadout().multitools[slot] = selected;

		        	playState.player.getPlayerData().replaceSlot(selected, slot);
		        	
		        	refreshLoadout();

		        }
		    });
			
			weapons.addActor(itemChoose);
		}
		
		options = new ScrollPane(weapons, gsm.getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
		
	}
	
	public void getArtifactOptions(final int slot) {
		
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup items = new VerticalGroup();
		
		items.addActor(new Text(HadalGame.assetManager, "ARTIFACT: " + slot, 0, 0));
		
		for (Artifact a: artifacts) {
			
			final Artifact selected = a;
			
			Text itemChoose = new Text(HadalGame.assetManager, selected.name , 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

		        	if (!Arrays.asList(gsm.getLoadout().artifacts).contains(selected)) {
		        		gsm.getLoadout().artifacts[slot] = selected;
		        		
		        		playState.player.getPlayerData().replaceSlot(selected, slot);
		        		
			        	refreshLoadout();
		        	}
		        }
		    });
			
			items.addActor(itemChoose);
		}
		
		options = new ScrollPane(items, gsm.getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
		
	}

	public void getCharOptions() {
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup people = new VerticalGroup();
		
		people.addActor(new Text(HadalGame.assetManager, "CHARACTERS", 0, 0));
		
		for (String s : characters.keySet()) {
			
			final String selected = s;
			
			Text itemChoose = new Text(HadalGame.assetManager, characters.get(s) , 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

		        	gsm.getLoadout().playerSprite = selected;

		        	playState.player.setBodySprite(selected);
		        	
		        	refreshLoadout();

		        }
		    });
			
			people.addActor(itemChoose);
		}
		options = new ScrollPane(people, gsm.getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
	}
	
	public void getLevelOptions() {
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup people = new VerticalGroup();
		
		people.addActor(new Text(HadalGame.assetManager, "LEVELS", 0, 0));
		
		for (String s : levels.keySet()) {
			
			final String selected = s;
			
			Text itemChoose = new Text(HadalGame.assetManager, levels.get(s) , 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {

		        	gsm.setLevel(selected);
		        	refreshLoadout();

		        }
		    });
			
			people.addActor(itemChoose);
		}
		options = new ScrollPane(people, gsm.getSkin());
		options.setPosition(HadalGame.CONFIG_WIDTH - 500, 0);
		options.setSize(500, HadalGame.CONFIG_HEIGHT);
		
		stage.addActor(options);
	}
	
	public void refreshLoadout() {
		
		characterSelect.setText("Character: " + characters.get(gsm.getLoadout().playerSprite));
		levelSelect.setText("Level: " + levels.get(gsm.getLevel()));
		
		for (int i = 0; i < slotButtons.size; i++) {
			if (gsm.getLoadout().multitools[i] != null) {
				slotButtons.get(i).setText("SLOT " + (i + 1) + ": " + gsm.getLoadout().multitools[i].name);
			} else {
				slotButtons.get(i).setText("SLOT " + (i + 1) + ": EMPTY");
			}
		}
		
		for (int i = 0; i < artifactButtons.size; i++) {
			if (gsm.getLoadout().artifacts[i] != null) {
				artifactButtons.get(i).setText("ARTIFACT " + (i + 1) + ": " + 
			gsm.getLoadout().artifacts[i].name+ " " + gsm.getLoadout().artifacts[i].descr);
			} else {
				artifactButtons.get(i).setText("ARTIFACT " + (i + 1) + ": EMPTY");
			}
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
