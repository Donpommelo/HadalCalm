package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Scattergun;
import com.mygdx.hadal.equip.Speargun;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class PlayerBodyData extends BodyData {

	public int maxFuel = 100;
	public float currentFuel = 100;
	public float fuelRegen = 0.10f;
	
	public int numExtraJumps = 1;
	public int extraJumpsUsed = 0;
	public float jumpPow = 5.0f;
	public float extraJumpPow = 5.0f;
	
	public float fastFallPow = 6.0f;

	public int hoverCost = 5;
	public float hoverPow = 0.8f;
	
	public int airblastCost = 20;
	public float airblastPow = 7.5f;
	
	public int itemSlots = 6;
	public Equipable[] multitools;
	public int currentSlot = 0;
	public int lastSlot = 0;
	public Equipable currentTool;
	
	public PlayerBodyData(World world, Schmuck body) {
		super(world, body);
		multitools = new Equipable[itemSlots];
		multitools[0] = new Scattergun(body);
		multitools[1] = new Speargun(body);
		this.currentTool = multitools[currentSlot];
	}
	
	public void switchWeapon(int slot) {
		if (multitools.length > slot) {
			if (multitools[slot - 1] != null) {
				lastSlot = currentSlot;
				currentSlot = slot - 1;
				currentTool = multitools[currentSlot];
			}
		}
	}
	
	public void switchToLast() {
		int tempSlot = lastSlot;
		lastSlot = currentSlot;
		currentSlot = tempSlot;
		currentTool = multitools[currentSlot];
	}

}
