package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Projectile;
import com.mygdx.hadal.states.PlayState;

public class ProjectileData extends HadalData {

	public PlayState state;
	public Projectile proj;

	public Vector2 startVelo;
	public float gravityEffect;
	public int durability;
	
	public float width, height;
	
	public ProjectileData(PlayState state, World world, Projectile proj) {
		super(world, UserDataTypes.PROJECTILE);
		this.state = state;
		this.proj = proj;
	}
	
	public void onHit(HadalData fixB) {
		proj.queueDeletion();
	}

}
