package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;

public class PlayerSchmuck extends HadalSchmuck{

	public float maxGroundXSpeed = 15;
	public float maxAirXSpeed = 10;
	
	//Accelerating on the ground/air
	public float groundXAccel = 0.10f;
	public float airXAccel = 0.05f;
	public float groundXDeaccel = 0.50f;
	public float airXDeaccel = 0.25f;
	
	public int numExtraJumps = 1;
	public int extraJumpsUsed = 0;
	public float jumpPow = 5.0f;
	public float extraJumpPow = 2.5f;

	public float fastFallPow = 6.0f;
	
	public int maxFuel = 100;
	public float currentFuel = 100;
	public float fuelRegen = 0.25f;
	
	public int hoverCost = 5;
	public float hoverPow = 0.5f;
	
	public PlayerSchmuck(World world) {
		super(world, UserDataTypes.BODY);
	}

}
