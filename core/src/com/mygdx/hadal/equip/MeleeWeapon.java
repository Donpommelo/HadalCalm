package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import box2dLight.RayHandler;

public class MeleeWeapon extends Equipable {

	public float windup;
	public float backswing;
	public float hitboxSize;
	public float swingArc;
	
	public MeleeWeapon(Schmuck user, String name, int useCd, float windup, float backswing, float hitboxSize, float swingArc) {
		super(user, name, useCd);
		this.windup = windup;
		this.backswing = backswing;
		this.hitboxSize = hitboxSize;
		this.swingArc = swingArc;
		
	}

	@Override
	public void mouseClicked(PlayState state, BodyData shooter, short faction, int x, int y, World world,
			OrthographicCamera camera, RayHandler rays) {
		
		user.body.createFixture(FixtureBuilder.createFixtureDef(80, 80, new Vector2(0, -0.5f), true, 0,
				Constants.BIT_SENSOR, Constants.BIT_WALL, Constants.PLAYER_HITBOX));
		
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

}
