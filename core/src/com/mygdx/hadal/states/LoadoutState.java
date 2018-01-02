package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * The MenuState is pulled up by pausing in game.
 * @author Zachary Tu
 *
 */
public class LoadoutState extends GameState {

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Text slot1, slot2, slot3, slot4;
		
	private Actor exitOption, playOption;

	private ScrollPane options;
	
	private static Array<Class<? extends Equipable>> items = new Array<Class<? extends Equipable>>();
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public LoadoutState(final GameStateManager gsm) {
		super(gsm);
		
		items.add(Boomerang.class);
		items.add(BouncingBlade.class);
		items.add(ChargeBeam.class);
		items.add(GrenadeLauncher.class);
		items.add(IronBallLauncher.class);
		items.add(Machinegun.class);
		items.add(MomentumShooter.class);
		items.add(Scattergun.class);
		items.add(Speargun.class);
		items.add(Scrapripper.class);
		items.add(TorpedoLauncher.class);
		
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				exitOption = new Text(HadalGame.assetManager, "EXIT?", 100, HadalGame.CONFIG_HEIGHT - 260);
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState();
			        }
			    });
				exitOption.setScale(0.5f);	
				
				playOption = new Text(HadalGame.assetManager, "Play?",  100, HadalGame.CONFIG_HEIGHT - 350);
				playOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState();
			        	gsm.addState(State.PLAY);
			        }
			    });
				playOption.setScale(0.5f);
				
				slot1 = new Text(HadalGame.assetManager, "", 200, HadalGame.CONFIG_HEIGHT - 250);
				
				slot1.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getOptions(1);
			        }
			    });
				
				slot2 = new Text(HadalGame.assetManager, "", 200, HadalGame.CONFIG_HEIGHT - 300);
				
				slot2.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getOptions(2);
			        }
			    });
				
				slot3 = new Text(HadalGame.assetManager, "", 200, HadalGame.CONFIG_HEIGHT - 350);
				
				slot3.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getOptions(3);
			        }
			    });
				
				slot4 = new Text(HadalGame.assetManager, "", 200, HadalGame.CONFIG_HEIGHT - 400);
				
				slot4.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getOptions(4);
			        }
			    });
				
				addActor(slot1);
				addActor(slot2);
				addActor(slot3);
				addActor(slot4);
				
				addActor(exitOption);				
				addActor(playOption);				
			}
		};
		refreshLoadout();
		app.newMenu(stage);
	}
	
	public void getOptions(final int slot) {
		
		if (options != null) {
			options.remove();
		}
		
		VerticalGroup weapons = new VerticalGroup();
		
		weapons.addActor(new Text(HadalGame.assetManager, "SLOT: " + slot, 0, 0));
		
		for (Class<? extends Equipable> c: items) {
			
			final Class<? extends Equipable> selected = c;
			
			Text itemChoose = new Text(HadalGame.assetManager, selected.getSimpleName(), 0, 0);
			
			itemChoose.addListener(new ClickListener() {
		        public void clicked(InputEvent e, float x, float y) {
		        	switch(slot) {
		        	case 1:
		        		gsm.getLoadout().slot1 = selected;
		        		break;
		        	case 2:
		        		gsm.getLoadout().slot2 = selected;
		        		break;
		        	case 3:
		        		gsm.getLoadout().slot3 = selected;
		        		break;
		        	case 4:
		        		gsm.getLoadout().slot4 = selected;
		        		break;
		        	}

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
	
	public void refreshLoadout() {
		slot1.setText("SLOT 1: " + gsm.getLoadout().slot1.getSimpleName());
		slot2.setText("SLOT 2: " + gsm.getLoadout().slot2.getSimpleName());
		slot3.setText("SLOT 3: " + gsm.getLoadout().slot3.getSimpleName());
		slot4.setText("SLOT 4: " + gsm.getLoadout().slot4.getSimpleName());
		
	}
	
	/**
	 * 
	 */
	@Override
	public void update(float delta) {
		stage.act();
	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(hud.combined);
		batch.begin();
		stage.draw();
		batch.end();
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

}
