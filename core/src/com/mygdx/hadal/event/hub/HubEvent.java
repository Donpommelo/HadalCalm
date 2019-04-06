package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * The HubEvent is one of the events in the hub of the game that produces an extra ui for the player to manage
 * stuff between rounds.
 * @author Zachary Tu
 *
 */
public class HubEvent extends Event {

	protected Table tableInner, tableOuter;
	protected ScrollPane options;
	protected boolean open;
	
	//These fields pertain to the extra info window that pops up when mousing over stuff.
	private Text extraInfo;
	private String info;
	private final static int infoWidth = 300;
	private final static int infoHeight = 300;
	
	protected static final float optionsWidthOuter = 440.0f;
	protected static final float optionsWidth = 400.0f;
	protected static final float optionsHeight = 30.0f;
	
	public HubEvent(final PlayState state, String name, int width, int height, int x, int y, String title) {
		super(state, name, width, height, x, y);
		this.tableInner = new Table();
		this.tableOuter = new Table();
		
		tableOuter.add(new Text(HadalGame.assetManager, title, 0, 0)).width(optionsWidthOuter).height(optionsHeight);
		tableOuter.row();
		this.options = new ScrollPane(tableInner, state.getGsm().getSkin());
		options.setFadeScrollBars(false);
		
		tableOuter.add(options).width(optionsWidthOuter).height(HadalGame.CONFIG_HEIGHT / 2);
		
		extraInfo = new Text(HadalGame.assetManager, "", 0, 0) {
			
			@Override
		    public void draw(Batch batch, float alpha) {
				super.draw(batch, alpha);
				font.getData().setScale(0.40f);
				state.getGsm().getSimplePatch().draw(batch, getX(), getY(), infoWidth, infoHeight);
			    font.draw(batch, info, getX(), getY() + infoHeight - 25, infoWidth, -1, true);
			    font.getData().setScale(1.0f);
		    }
			
		};
		
		this.open = false;
	}
	
	@Override
	public void create() {
		this.eventData =  new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				preActivate(null, p);
			}
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (open) {
					leave();
				} else {
					enter();
				}
				open = !open;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	/**
	 * This keeps track of whether the player is in front of the event or not.
	 */
	@Override
	public void controller(float delta) {
		if (open) {
			if(getPosition().dst(state.getPlayer().getPosition()) > 3) {
				leave();
				open = false;
			}
		}
	}
	
	@Override
	public void clientController(float delta) {
		if (open) {
			if(getPosition().dst(state.getPlayer().getPosition()) > 3) {
				leave();
				open = false;
			}
		}
	}

	/**
	 * This is run when the player enters the event. Pull up an extra menu with options specified by the child.
	 */
	public void enter() {

		tableInner.clear();
		
		tableOuter.setPosition(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT / 4);
		
		tableOuter.setSize(optionsWidthOuter, HadalGame.CONFIG_HEIGHT / 2);
		
		state.getStage().setScrollFocus(options);
		
		state.getStage().addActor(tableOuter);
		
		tableOuter.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH - optionsWidthOuter , HadalGame.CONFIG_HEIGHT / 4, .5f, Interpolation.pow5Out));
		
	}
	
	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		tableOuter.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT / 4, .5f, Interpolation.pow5Out));
		mouseOut();
		if (state.getStage().getScrollFocus() != null) {
			if (state.getStage().getScrollFocus().equals(options)) {
				state.getStage().setScrollFocus(null);
			}
		}
	}
	
	/**
	 * This is triggered when the player mouses over a specific option in the ui.
	 * It pulls up an extra menu with more information.
	 * @param info: extra info.
	 */
	public void mouseIn(String info) {
		
		this.info = info;
		extraInfo.setPosition(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT / 4);
		
		state.getStage().addActor(extraInfo);
		
		extraInfo.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH - optionsWidthOuter - infoWidth, HadalGame.CONFIG_HEIGHT / 4, .75f, Interpolation.pow5Out));
	}
	
	/**
	 * Mouse moves away from the menu making the extra menu slide away.
	 */
	public void mouseOut() {
		extraInfo.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT / 4, .75f, Interpolation.pow5Out));
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(1);
	}
}