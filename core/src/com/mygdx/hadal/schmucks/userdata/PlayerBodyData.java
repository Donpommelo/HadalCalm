package com.mygdx.hadal.schmucks.userdata;

import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class PlayerBodyData extends BodyData {

	public int maxFuel = 100;
	public float currentFuel = 100;
	public float fuelRegen = 5.0f;
	
	public int numExtraJumps = 1;
	public int extraJumpsUsed = 0;
	public float jumpPow = 5.0f;
	
	public float fastFallPow = 6.0f;

	public int hoverCost = 5;
	public float hoverPow = 0.8f;
	
	public int airblastCost = 30;
	public float airblastPow = 7.5f;
	
	public int itemSlots = 4;
	public Equipable[] multitools;
	public int currentSlot = 0;
	public int lastSlot = 0;
	public Equipable currentTool;
	
	public Player player;
	
	public PlayerBodyData(World world, Player body, Loadout loadout) {
		super(world, body);
		this.player = body;
		multitools = new Equipable[itemSlots];
		try {
			multitools[0] = loadout.slot1.getConstructor(Schmuck.class).newInstance(body);
			multitools[1] = loadout.slot2.getConstructor(Schmuck.class).newInstance(body);
			multitools[2] = loadout.slot3.getConstructor(Schmuck.class).newInstance(body);
			multitools[3] = loadout.slot4.getConstructor(Schmuck.class).newInstance(body);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		this.currentTool = multitools[currentSlot];
	}
	
	public void switchWeapon(int slot) {
		if (multitools.length >= slot && schmuck.shootDelayCount <= 0) {
			if (multitools[slot - 1] != null) {
				lastSlot = currentSlot;
				currentSlot = slot - 1;
				currentTool = multitools[currentSlot];
			}
		}
	}
	
	public void switchToLast() {
		if (schmuck.shootDelayCount <= 0) {
			int tempSlot = lastSlot;
			lastSlot = currentSlot;
			currentSlot = tempSlot;
			currentTool = multitools[currentSlot];
		}
	}
	
	public Equipable pickup(Equipable equip) {
		
		for (int i = 0; i < itemSlots; i++) {
			if (multitools[i] == null) {
				multitools[i] = equip;
				multitools[i].user = player;
				currentSlot = i;
				currentTool = multitools[currentSlot];
				return null;
			}
		}
		
		Equipable old = multitools[currentSlot];
		
		multitools[currentSlot] = equip;
		multitools[currentSlot].user = player;
		currentTool = multitools[currentSlot];
		
		return old;
	}
	
	public void fuelSpend(float cost) {
		currentFuel -= cost;
		if (currentFuel < 0) {
			currentFuel = 0;
		}
	}
	
	public void fuelGain(float fuelRegen2) {
		currentFuel += fuelRegen2;
		if (currentFuel > maxFuel) {
			currentFuel = maxFuel;
		}
	}

}
