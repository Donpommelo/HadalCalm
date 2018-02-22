package com.mygdx.hadal.schmucks.userdata;


import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.misc.Nothing;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.statuses.Status;

/**
 * This is the data for a player and contains player-specific fields like airblast, jump stats, etc.
 * @author Zachary Tu
 *
 */
public class PlayerBodyData extends BodyData {
	
	private int numExtraJumps = 1;
	private int extraJumpsUsed = 0;
	private float jumpPow = 5.0f;
	
	private float fastFallPow = 6.0f;

	private int hoverCost = 5;
	private float hoverPow = 0.8f;
	
	private int airblastCost = 30;
	
	private Equipable[] multitools;
	private Artifact[] artifacts;
	private int currentSlot = 0;
	private int lastSlot = 0;
	private Equipable currentTool;
	
	private Player player;
	
	public PlayerBodyData(World world, Player body, Loadout loadout) {
		super(world, body);
		this.player = body;
		multitools = loadout.multitools.clone();
		for (Equipable e : multitools) {
			if (e != null) {
				e.setUser(player);
			}
		}
		artifacts = loadout.artifacts.clone();
		for (Artifact a : artifacts) {
			if (a != null) {
				for (Status s : a.loadEnchantments(player.getState(), world, player.getState().camera,
						player.getState().getRays(), this)) {
					addStatus(s);
				}
			}
		}

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
	
	public void switchWeapon(int slot) {
		if (multitools.length >= slot && schmuck.getShootDelayCount() <= 0) {
			if (multitools[slot - 1] != null && !(multitools[slot - 1] instanceof Nothing)) {
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
		if (schmuck.getShootDelayCount() <= 0) {
			if (multitools[lastSlot] != null && !(multitools[lastSlot] instanceof Nothing)) {
				int tempSlot = lastSlot;
				lastSlot = currentSlot;
				currentSlot = tempSlot;
				setEquip();
			}
		}
	}
	
	public void switchUp() {
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
	
	public void switchDown() {
		for (int i = 1; i <= multitools.length; i++) {
			if (multitools[(currentSlot - i) % multitools.length] != null &&
					!(multitools[(currentSlot - i) % multitools.length] instanceof Nothing)) {
				lastSlot = currentSlot;
				currentSlot = (currentSlot - i) % multitools.length;
				setEquip();
				return;
			}
		}
	}
	
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
	
	public void replaceSlot(Equipable equip, int slot) {
		multitools[slot] = equip;
		multitools[slot].setUser(player);
		currentSlot = slot;
		setEquip();
	}
	
	public void replaceSlot(Artifact artifact, int slot) {
		
		if (artifacts[slot] != null) {
			for (Status s : artifacts[slot].getEnchantment()) {
				removeStatus(s);
			}
		}	
		
		artifacts[slot] = artifact;
		
		for (Status s : artifacts[slot].loadEnchantments(player.getState(), world, player.getState().camera, 
				player.getState().getRays(), this)) {
			addStatus(s);
		}
	}
	
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
