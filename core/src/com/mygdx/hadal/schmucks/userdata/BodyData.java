package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class BodyData extends HadalData {

	private Schmuck body;
	
	//Speed on ground
	public float maxGroundXSpeed = 15.0f;
	public float maxAirXSpeed = 10.0f;
	
	//Speed on ground
	public float maxGroundYSpeed = 10.0f;
	public float maxAirYSpeed = 7.5f;
		
	//Accelerating on the ground/air
	public float groundXAccel = 0.10f;
	public float airXAccel = 0.05f;
	public float groundXDeaccel = 0.05f;
	public float airXDeaccel = 0.01f;
	
	public float groundYAccel = 0.10f;
	public float airYAccel = 0.50f;
	public float groundYDeaccel = 0.01f;
	public float airYDeaccel = 0.01f;
	
	public int maxHp = 100;
	public float currentHp = 100;
	public float hpRegen = 0.0f;
	
	public BodyData(World world, Schmuck body) {
		super(world, UserDataTypes.BODY);
		this.body = body;		
	}	
	
	public void receiveDamage(float basedamage) {
		currentHp -= basedamage;
		if (currentHp <= 0) {
			die();
		}
	}
	
	public void die() {
		body.queueDeletion();
	}

	public Schmuck getBody() {
		return body;
	}
}
