package com.mygdx.hadal.schmucks.userdata;



import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.misc.Nothing;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.UnlocktoItem;

/**
 * This is the data for a player and contains player-specific fields like airblast, jump stats, etc.
 * @author Zachary Tu
 *
 */
public class PlayerBodyData extends BodyData {
	
	private int numExtraJumps = 1;
	private int extraJumpsUsed = 0;
	private float jumpPow = 25.0f;
	
	private float fastFallPow = 25.0f;

	private int hoverCost = 5;
	private float hoverPow = 4.0f;
	
	private int airblastCost = 30;
	
	private Equipable[] multitools;
	private ArrayList<Artifact> artifacts;
	private Artifact artifactStart;
	private int currentSlot = 0;
	private int lastSlot = 1;
	private Equipable currentTool;
	
	private Player player;
	
	public PlayerBodyData(World world, Player body, Loadout loadout) {
		super(world, body);
		this.player = body;
		this.multitools = new Equipable[loadout.multitools.length];
		for (int i = 0; i < loadout.multitools.length; i++) {
			if (loadout.multitools[i] != null) {
				multitools[i] = UnlocktoItem.getUnlock(loadout.multitools[i], player);
			}
		}
	
		this.artifacts = new ArrayList<Artifact>();
		artifactStart = addArtifact(loadout.artifact);

		currentHp = getMaxHp();
		currentFuel = getMaxHp();
		currentSlot = 0;
		setEquip();
	}
	
	/**
	 * This is run when transitioning the player into a new map/world
	 * @param newPlayer
	 * @param newWorld
	 */
	public void resetData(Player newPlayer, World newWorld) {
		this.setEntity(newPlayer);
		this.schmuck = newPlayer;
		this.player = newPlayer;
		this.world = newWorld;
		statuses.clear();
		statusesChecked.clear();
		for (Equipable e : multitools) {
			if (e != null) {
				e.setUser(player);
			}
		}
		for (Artifact a : artifacts) {
			if (a != null) {
				for (Status s : a.loadEnchantments(player.getState(), world, player.getState().camera, 
						player.getState().getRays(), this)) {
					addStatus(s);
				}
			}
		}
	}
	
	/**
	 * Player switches to a specified weapon slot
	 * @param slot: new weapon slot.
	 */
	public void switchWeapon(int slot) {
		if (multitools.length >= slot && schmuck.getShootDelayCount() <= 0) {
			if (multitools[slot - 1] != null && !(multitools[slot - 1] instanceof Nothing)) {
				lastSlot = currentSlot;
				currentSlot = slot - 1;
				setEquip();
			}
		}
	}
	
	/**
	 * Player switches to last used weapon slot
	 */
	public void switchToLast() {
		if (schmuck.getShootDelayCount() <= 0) {
			if (lastSlot < multitools.length) {
				if (multitools[lastSlot] != null && !(multitools[lastSlot] instanceof Nothing)) {
					int tempSlot = lastSlot;
					lastSlot = currentSlot;
					currentSlot = tempSlot;
					setEquip();
				}
			}
		}
	}
	
	/**
	 * Player switches to a weapon slot above current slot, wrapping to end of slots if at first slot. (ignore empty slots)
	 * This is also called automatically when running out of a consumable equip.
	 */
	public void switchDown() {
		for (int i = 1; i <= multitools.length; i++) {
			if (multitools[(currentSlot + i) % multitools.length] != null &&
					!(multitools[(currentSlot + i) % multitools.length] instanceof Nothing)) {
				lastSlot = currentSlot;
				currentSlot = (currentSlot + i) % multitools.length;
				setEquip();
				return;
			}
		}
	}
	
	/**
	 * Player switches to a weapon slot below current slot, wrapping to end of slots if at last slot. (ignore empty slots)
	 */
	public void switchUp() {
		for (int i = 1; i <= multitools.length; i++) {
			if (multitools[(multitools.length + (currentSlot - i)) % multitools.length] != null &&
					!(multitools[(multitools.length + (currentSlot - i)) % multitools.length] instanceof Nothing)) {
				lastSlot = currentSlot;
				currentSlot = (multitools.length + (currentSlot - i)) % multitools.length;
				setEquip();
				return;
			}
		}
	}
	
	/**
	 * Player picks up new weapon.
	 * @param equip: The new equip to switch in. Replaces current slot if inventory is full.
	 * @return: If a weapon is dropped to make room for new weapon, return it, otherwise return null.
	 */
	public Equipable pickup(Equipable equip) {
		
		for (int i = 0; i < Loadout.getNumSlots(); i++) {
			if (multitools[i] == null || multitools[i] instanceof Nothing) {
				multitools[i] = equip;
				multitools[i].setUser(player);
				currentSlot = i;
 				setEquip();
				return null;
			}
		}
		
		Equipable old = multitools[currentSlot];
		
		multitools[currentSlot] = equip;
		multitools[currentSlot].setUser(player);
		setEquip();
		
		return old;
	}
	
	/**
	 * Replaces slot slot with new equip. Used in loadout state and also when using last charge of consumable weapon.
	 */
	public void replaceSlot(UnlockEquip equip, int slot) {
		multitools[slot] = UnlocktoItem.getUnlock(equip, player);
		multitools[slot].setUser(player);
		currentSlot = slot;
		setEquip();
	}
	
	/**
	 * Replaces starting artifact with input artifact. This only runs in the loadout state.
	 */
	public void replaceSlot(UnlockArtifact artifact) {
		
		artifacts.clear();
		
		if (artifactStart != null) {
			for (Status s : artifactStart.getEnchantment()) {
				removeStatus(s);
			}
		}	
		
		artifactStart = addArtifact(artifact);
	}
	
	/**
	 * Add a new artifact. Return the new artifact
	 */
	public Artifact addArtifact(UnlockArtifact artifact) {
		
		Artifact newArtifact =  UnlocktoItem.getUnlock(artifact);
		
		for (Status s : newArtifact.loadEnchantments(player.getState(), world, player.getState().camera, 
				player.getState().getRays(), this)) {
			addStatus(s);
		}
		
		artifacts.add(newArtifact);
		return newArtifact;
	}
	
	/**
	 * This helper function is called when weapon switching to ensure the correct weapon sprite is drawn and that the 
	 * current weapon is kept track of.
	 */
	public void setEquip() {
		currentTool = multitools[currentSlot];
		player.setToolSprite(currentTool.getEquipSprite());
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
	
	@Override
	public void die(BodyData perp) {
		schmuck.getState().gameOver(false);
		super.die(perp);
	}
	
	public Player getPlayer() {
		return player;
	}

	public int getExtraJumps() {
		return numExtraJumps + (int)getBonusJumpNum();
	}
	
	public float getJumpPower() {
		return jumpPow * (1 + getBonusJumpPower());
	}
	
	public float getFastFallPower() {
		return fastFallPow;
	}
	
	public float getHoverPower() {
		return hoverPow * (1 + getBonusHoverPower());
	}
	
	public float getHoverCost() {
		return hoverCost * (1 + getBonusHoverCost());
	}

	public float getAirblastCost() {
		return airblastCost * (1 + getBonusAirblastCost());
	}
	
	public int getExtraJumpsUsed() {
		return extraJumpsUsed;
	}

	public void setExtraJumpsUsed(int extraJumpsUsed) {
		this.extraJumpsUsed = extraJumpsUsed;
	}

	public Equipable[] getMultitools() {
		return multitools;
	}

	public int getCurrentSlot() {
		return currentSlot;
	}

	public Equipable getCurrentTool() {
		return currentTool;
	}	
}
