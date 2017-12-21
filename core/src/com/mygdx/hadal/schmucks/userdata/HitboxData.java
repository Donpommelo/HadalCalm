package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.states.PlayState;

public class HitboxData extends HadalData {

	public PlayState state;
	public Hitbox hbox;

	public Vector2 startVelo;
	public float gravityEffect;
	
	public float width, height;
	
	public HitboxData(PlayState state, World world, Hitbox proj) {
		super(world, UserDataTypes.HITBOX, proj);
		this.state = state;
		this.hbox = proj;
	}
	
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			hbox.dura = 0;
		} else if (fixB.getType().equals(UserDataTypes.BODY)){
			hbox.dura--;
		}
		if (hbox.dura <= 0) {
			hbox.queueDeletion();
		}
	}
	
	

}
