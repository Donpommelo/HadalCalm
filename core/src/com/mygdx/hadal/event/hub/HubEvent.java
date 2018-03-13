package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class HubEvent extends Event {

	protected Table tableInner, tableOuter;
	protected ScrollPane options;
	protected boolean open;
	
	protected static final float optionsWidth = 600.0f;
	protected static final float optionsHeight = 50.0f;
	
	public HubEvent(PlayState state, World world, OrthographicCamera camera, RayHandler rays, String name, int width, int height,
			int x, int y, String title) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.tableInner = new Table();
		this.tableOuter = new Table();
		
		tableOuter.add(new Text(HadalGame.assetManager, title, 0, 0)).width(optionsWidth).height(optionsHeight);
		tableOuter.row();
		this.options = new ScrollPane(tableInner, state.getGsm().getSkin());
		
		tableOuter.add(options).width(optionsWidth).height(HadalGame.CONFIG_HEIGHT / 2);
		
		this.open = false;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (open && eventData.getSchmucks().isEmpty()) {
			leave();
			open = false;
		}
		if (!open && !eventData.getSchmucks().isEmpty()) {
			enter();
			open = true;
		}
	}
	
	public void enter() {

		tableInner.clear();
		
		tableOuter.setPosition(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT / 4);
		
		tableOuter.setSize(optionsWidth, HadalGame.CONFIG_HEIGHT / 2);
		
		state.getStage().setScrollFocus(options);
		
		state.getStage().addActor(tableOuter);
		
		tableOuter.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH - optionsWidth , HadalGame.CONFIG_HEIGHT / 4, .5f, Interpolation.pow5Out));
		
	}
	
	public void leave() {
		tableOuter.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT / 4, .5f, Interpolation.pow5Out));
	}

}
