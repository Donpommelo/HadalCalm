package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The AboutState is selected from the title screen and gives information about the game
 * @author Snujube Spambly
 */
public class AboutState extends GameState {

	//This table contains the ui elements of the pause screen
	private Table options, details;

	//options that the player can view
	private Text aboutOption, miscOption, tipsOption, creditsOption, exitOption;
	
	//Dimensions of the setting menu
	private static final int optionsX = -1125;
	private static final int optionsY = 100;
	private static final int optionsXEnabled = 25;
	private static final int optionsYEnabled = 100;
	private static final int optionsWidth = 300;
	private static final int optionsHeight = 600;

	private static final int detailsX = -830;
	private static final int detailsY = 100;
	private static final int detailsXEnabled = 320;
	private static final int detailsYEnabled = 100;
	private static final int detailsWidth = 800;
	private static final int detailsHeight = 600;
	
	private static final float optionsScale = 0.5f;
	private static final float optionHeight = 35.0f;
	private static final float optionPad = 15.0f;
	private static final float detailsScale = 0.3f;
	
	private static final float titlePad = 25.0f;
	private static final int detailsTextWidth = 750;

	//this is the state underneath this state.
	private final GameState peekState;

	/**
	 * Constructor will be called when the player enters the about state from the title menu.
	 */
	public AboutState(final GameStateManager gsm, GameState peekState) {
		super(gsm);
		this.peekState = peekState;
	}
	
	@Override
	public void show() {
		
		stage = new Stage() {
			{
				options = new WindowTable();
				options.setPosition(optionsX, optionsY);
				options.setSize(optionsWidth, optionsHeight);
				options.top();
				addActor(options);
				
				details = new WindowTable();
				details.setPosition(detailsX, detailsY);
				details.setSize(detailsWidth, detailsHeight);
				details.top();
				addActor(details);
				
				aboutOption = new Text("ABOUT", 0, 0, true);
				aboutOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						aboutSelected();
			        }
			    });
				aboutOption.setScale(optionsScale);
				
				tipsOption = new Text("TIPS", 0, 0, true);
				tipsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						tipsSelected();
			        }
			    });
				tipsOption.setScale(optionsScale);
				
				miscOption = new Text("MISC", 0, 0, true);
				miscOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						miscSelected();
			        }
			    });
				miscOption.setScale(optionsScale);
				
				creditsOption = new Text("CREDITS", 0, 0, true);
				creditsOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						creditsSelected();
			        }
			    });
				creditsOption.setScale(optionsScale);
				
				exitOption = new Text("RETURN?", 0, 0, true);
				exitOption.addListener(new ClickListener() {
					
					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
						transitionOut(() -> gsm.removeState(AboutState.class));
			        }
			    });
				exitOption.setScale(optionsScale);
				
				options.add(aboutOption).height(optionHeight).pad(optionPad).row();
				options.add(tipsOption).height(optionHeight).pad(optionPad).row();
				options.add(miscOption).height(optionHeight).pad(optionPad).row();
				options.add(creditsOption).height(optionHeight).pad(optionPad).row();
				options.add(exitOption).height(optionHeight).pad(optionPad).expand().row();
			}
		};
		app.newMenu(stage);
		transitionIn();
		
		//start off with about selected
		aboutSelected();
	}

	/**
	 * This is called whenever the player selects the ABOUT tab
	 */
	private void aboutSelected() {
		details.clearChildren();
		
		details.add(new Text("ABOUT", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text about = new Text(GameStateManager.miscText.getString("about"), 0, 0, false, true, detailsTextWidth);
		about.setScale(detailsScale);
		
		details.add(about);
	}
	
	/**
	 * This is called whenever the player selects the TIPS tab
	 */
	private void tipsSelected() {
		details.clearChildren();
		
		details.add(new Text("TIPS", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text tips = new Text(GameStateManager.miscText.getString("tips"), 0, 0, false, true, detailsTextWidth);
		tips.setScale(detailsScale);
		
		details.add(tips);
	}
	
	/**
	 * This is called whenever the player selects the MISC tab
	 */
	private void miscSelected() {
		details.clearChildren();
		
		details.add(new Text("MISC", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		Text misc = new Text(GameStateManager.miscText.getString("misc"), 0, 0, false, true, detailsTextWidth);
		misc.setScale(detailsScale);
		
		details.add(misc);
	}

	/**
	 * This is called whenever the player selects the CREDITS tab
	 */
	private void creditsSelected() {
		details.clearChildren();
		
		details.add(new Text("CREDITS", 0, 0, false)).colspan(2).pad(titlePad).row();
		
		//dev and art options have url links
		Text dev = new Text("DONPOMMELO", 0, 0, true);
		dev.setScale(detailsScale);
		dev.setColor(Color.RED);
		
		dev.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://donpommelo.itch.io/");
	        }
	    });
		
		Text art = new Text("SHOEBANFOO", 0, 0, true);
		art.setScale(detailsScale);
		art.setColor(Color.RED);
		
		art.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent e, float x, float y) {
				SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
				Gdx.net.openURI("https://www.instagram.com/shoebanfoo/");
	        }
	    });
		
		Text sfx = new Text(GameStateManager.miscText.getString("credits"), 0, 0, false, true, detailsTextWidth);
		sfx.setScale(detailsScale);
		
		details.add(dev).height(optionHeight).row();
		details.add(art).height(optionHeight).row();
		details.add(sfx).height(optionHeight);
	}

	private static final float transitionDuration = 0.25f;
	private static final Interpolation intp = Interpolation.fastSlow;
	private void transitionOut(Runnable runnable) {
		options.addAction(Actions.moveTo(optionsX, optionsY, transitionDuration, intp));
		details.addAction(Actions.sequence(Actions.moveTo(detailsX, detailsY, transitionDuration, intp), Actions.run(runnable)));
	}

	private void transitionIn() {
		options.addAction(Actions.moveTo(optionsXEnabled, optionsYEnabled, transitionDuration, intp));
		details.addAction(Actions.moveTo(detailsXEnabled, detailsYEnabled, transitionDuration, intp));
	}

	@Override
	public void update(float delta) {
		peekState.update(delta);
	}

	@Override
	public void render(float delta) {
		peekState.render(delta);
		peekState.stage.getViewport().apply();
		peekState.stage.act();
		peekState.stage.draw();
	}
	
	@Override
	public void dispose() {	
		stage.dispose(); 
	}
}
