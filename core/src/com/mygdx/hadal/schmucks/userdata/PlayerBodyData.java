package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.misc.Nothing;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.statuses.Status;

public class PlayerBodyData extends BodyData {

	
	
	public int numExtraJumps = 1;
	public int extraJumpsUsed = 0;
	public float jumpPow = 5.0f;
	
	public float fastFallPow = 6.0f;

	public int hoverCost = 5;
	public float hoverPow = 0.8f;
	
	public int airblastCost = 30;
	
	public Equipable[] multitools;
	public Artifact[] artifacts;
	public int currentSlot = 0;
	public int lastSlot = 0;
	public Equipable currentTool;
	
	public Player player;
	
	public PlayerBodyData(World world, Player body, Loadout loadout) {
		super(world, body);
		this.player = body;
		multitools = loadout.multitools.clone();
		for (Equipable e : multitools) {
			if (e != null) {
				e.user = player;
			}
		}
		artifacts = loadout.artifacts;
		for (Artifact a : artifacts) {
			if (a != null) {
				for (Status s : a.getEnchantment(this)) {
					addStatus(s);
				}
			}
		}

		currentHp = getMaxHp();
		currentFuel = getMaxHp();
		currentSlot = 0;
		setEquip();
	}
	
	public void switchWeapon(int slot) {
		if (multitools.length >= slot && schmuck.shootDelayCount <= 0) {
			if (multitools[slot - 1] != null) {
				lastSlot = currentSlot;
				currentSlot = slot - 1;
				setEquip();
			}
		}
	}
	
	public void hardSwitchWeapon(int slot) {
		if (multitools.length >= slot) {
			if (multitools[slot - 1] != null) {
				lastSlot = currentSlot;
				currentSlot = slot - 1;
				setEquip();
			}
		}
	}
	
	public void switchToLast() {
		if (schmuck.shootDelayCount <= 0) {
			int tempSlot = lastSlot;
			lastSlot = currentSlot;
			currentSlot = tempSlot;
			setEquip();
		}
	}
	
	public Equipable pickup(Equipable equip) {
		
		if (multitools[currentSlot] instanceof Nothing) {
			multitools[currentSlot] = equip;
			multitools[currentSlot].user = player;
			setEquip();
			return null;
		}
		
		for (int i = 0; i < Loadout.getNumSlots(); i++) {
			if (multitools[i] == null) {
				multitools[i] = equip;
				multitools[i].user = player;
				currentSlot = i;
				setEquip();
				return null;
			}
		}
		
		Equipable old = multitools[currentSlot];
		
		multitools[currentSlot] = equip;
		multitools[currentSlot].user = player;
		setEquip();
		
		return old;
	}
	
	public void replaceSlot(Equipable equip, int slot) {
		multitools[slot] = equip;
		multitools[slot].user = player;
		currentSlot = slot;
		setEquip();
	}
	
	public void setEquip() {
		currentTool = multitools[currentSlot];
		if (player.getArmSprite().isFlipX() != currentTool.getEquipSprite().isFlipX()) {
			currentTool.getEquipSprite().flip(true, false);
			player.setToolSprite(currentTool.getEquipSprite());
			
		} else {
			player.setToolSprite(currentTool.getEquipSprite());
		}
	}
	
	public void fuelSpend(float cost) {
		currentFuel -= cost;
		if (currentFuel < 0) {
			currentFuel = 0;
		}
	}
	
	public void fuelGain(float fuelRegen2) {
		currentFuel += fuelRegen2;
		if (currentFuel > getMaxFuel()) {
			currentFuel = getMaxFuel();
		}
	}
	
	public void die() {
		schmuck.state.gameOver(false);
		schmuck.queueDeletion();
	}

}
