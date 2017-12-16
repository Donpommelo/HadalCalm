package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Speargun;;

public class BodyData extends HadalData{

	private Schmuck body;
	
	public float maxGroundXSpeed = 15;
	public float maxAirXSpeed = 10;
	
	//Accelerating on the ground/air
	public float groundXAccel = 0.10f;
	public float airXAccel = 0.05f;
	public float groundXDeaccel = 0.05f;
	public float airXDeaccel = 0.01f;
	
	public int numExtraJumps = 1;
	public int extraJumpsUsed = 0;
	public float jumpPow = 5.0f;
	public float extraJumpPow = 2.5f;

	public float fastFallPow = 6.0f;
	
	public int maxFuel = 100;
	public float currentFuel = 100;
	public float fuelRegen = 0.10f;
	
	public int maxHp = 100;
	public float currentHp = 100;
	public float hpRegen = 0.0f;
	
	public int hoverCost = 5;
	public float hoverPow = 0.8f;
	
	public int airblastCost = 20;
	public float airblastPow = 7.5f;
	
	public int itemSlots = 6;
	public Equipable[] multitools;
	public int currentSlot = 0;
	public Equipable currentTool;
	
	public BodyData(World world, Schmuck body) {
		super(world, UserDataTypes.BODY);
		this.body = body;
		multitools = new Equipable[itemSlots];
		multitools[0] = new Speargun();
		this.currentTool = multitools[currentSlot];
		
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
