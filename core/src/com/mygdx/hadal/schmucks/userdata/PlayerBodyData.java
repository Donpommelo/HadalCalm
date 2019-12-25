package com.mygdx.hadal.schmucks.userdata;

import java.util.Arrays;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.NothingActive;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.melee.Fisticuffs;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.Packets.SyncPlayerStats;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.WeaponModifier;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.UnlocktoItem;

/**
 * This is the data for a player and contains player-specific fields like airblast, jump stats, etc.
 * @author Zachary Tu
 *
 */
public class PlayerBodyData extends BodyData {
	
	private final static int baseHp = 100;
	
	private int numExtraJumps = 1;
	private int extraJumpsUsed = 0;
	private static final float jumpPow = 25.0f;
	
	private static final float fastFallPow = 12.0f;

	private static final int hoverCost = 5;
	private static final float hoverPow = 5.0f;
	
	private static final int airblastCost = 30;
	
	//This is the player's current loadout
	private Loadout loadout;
	
	//This is a list of the player's weapons
	private Equipable[] multitools;
	
	//This is a list of the player's artifacts
	private UnlockArtifact[] artifacts;
	
	//This is the player's active item
	private ActiveItem activeItem;
	
	//This is the slot number of the player's currently selected weapon
	private int currentSlot = 0;
	
	//This is the player's last used slot. (Used for switch-to-last-slot button)
	private int lastSlot = 1;
	
	private Player player;

	//This is ued by clients to display each player's hp percent in the ui
	private float overrideHpPercent;
	
	public PlayerBodyData(Player body, Loadout loadout) {
		super(body, baseHp);
		this.player = body;
		this.loadout = new Loadout(loadout);		
		currentSlot = 0;
	}
	
	/**
	 * This is called when creating a brand new player with a reset loadout
	 */
	public void initLoadout() {
		clearStatuses();

		//Acquire weapons from loadout
		this.multitools = new Equipable[Loadout.maxWeaponSlots];
		Arrays.fill(multitools, new NothingWeapon(player));
		for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
			multitools[i] = UnlocktoItem.getUnlock(loadout.multitools[i], player);
		}
		setEquip();

		//Reset artifacts list and acquire artifacts from loadout
		this.artifacts = new UnlockArtifact[Loadout.maxArtifactSlots];
		Arrays.fill(artifacts, UnlockArtifact.NOTHING);
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			addArtifact(loadout.artifacts[i], false);
		}
		
		//Acquire active item and acquire charge status
		this.activeItem = UnlocktoItem.getUnlock(loadout.activeItem, player);
		this.player.setBodySprite(loadout.character);
		
		//If this is the player being controlled by the user, update artifact ui
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
	}
	
	/**
	 * This is called by the client for players that receive a new loadout from the server.
	 * We give the player the new loadout information.
	 * 
	 * @param loadout: The new loadout for the player
	 */
	public void syncLoadout(Loadout loadout) {
		Loadout newLoadout = new Loadout(loadout);
		
		for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
			multitools[i] = UnlocktoItem.getUnlock(newLoadout.multitools[i], player);
		}
		setEquip();
		
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			artifacts[i] = UnlockArtifact.NOTHING;
		}
		
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			addArtifact(newLoadout.artifacts[i], true);
		}
		
		this.activeItem = UnlocktoItem.getUnlock(newLoadout.activeItem, player);
		player.setBodySprite(newLoadout.character);
		
		this.loadout = newLoadout;
		
		//If this is the player being controlled by the user, update artifact ui
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
	}
	
	/**
	 * This is run when the server receives a request from a client to make a change to their loadout.
	 * @param loadout
	 */
	public void syncLoadoutFromClient(UnlockEquip equip, UnlockArtifact artifactAdd, UnlockArtifact artifactRemove, UnlockActives active, UnlockCharacter character) {
		
		if (equip != null) {
			pickup(UnlocktoItem.getUnlock(equip, getPlayer()));
		}
		
		if (artifactAdd != null) {
			addArtifact(artifactAdd, false);
		}
		
		if (artifactRemove != null) {
			removeArtifact(artifactRemove);
		}
		
		if (active != null) {
			pickup(UnlocktoItem.getUnlock(active, getPlayer()));
		}
		
		if (character != null) {
			getPlayer().setBodySprite(character);
        	getPlayer().getPlayerData().getLoadout().character = character;
		}
	}
	
	/**
	 * This is run when transitioning the player into a new map/world or respawning
	 * @param newPlayer
	 */
	public void updateOldData(Player newPlayer, World newWorld) {
		this.setEntity(newPlayer);
		this.schmuck = newPlayer;
		this.player = newPlayer;
		
		clearStatuses();
		
		for (Equipable e : multitools) {
			e.setUser(player);
		}
		
		for (UnlockArtifact a : artifacts) {
			for (Status s : a.getArtifact().loadEnchantments(player.getState(), this)) {
				addStatus(s);
			}
		}
	}
	
	/**
	 * Player switches to a specified weapon slot
	 * @param slot: new weapon slot.
	 */
	public void switchWeapon(int slot) {
		if (getNumWeaponSlots() >= slot) {
			if (!(multitools[slot - 1] instanceof NothingWeapon)) {
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
			if (lastSlot < getNumWeaponSlots()) {
				if (!(multitools[lastSlot] instanceof NothingWeapon)) {
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
		for (int i = 1; i <= getNumWeaponSlots(); i++) {
			if (!(multitools[(currentSlot + i) % getNumWeaponSlots()] instanceof NothingWeapon)) {
				lastSlot = currentSlot;
				currentSlot = (currentSlot + i) % getNumWeaponSlots();
				setEquip();
				return;
			}
		}
	}
	
	/**
	 * Player switches to a weapon slot below current slot, wrapping to end of slots if at last slot. (ignore empty slots)
	 */
	public void switchUp() {
		for (int i = 1; i <= getNumWeaponSlots(); i++) {
			if (!(multitools[(getNumWeaponSlots() + (currentSlot - i)) % getNumWeaponSlots()] instanceof NothingWeapon)) {
				lastSlot = currentSlot;
				currentSlot = (getNumWeaponSlots() + (currentSlot - i)) % getNumWeaponSlots();
				setEquip();
				return;
			}
		}
	}
	
	/**
	 * Player picks up new weapon.
	 * @param equip: The new equip to switch in. Replaces current slot if inventory is full.
	 * @return: If a weapon is dropped to make room for new weapon, return it, otherwise return a Nothing Weapon.
	 */
	public Equipable pickup(Equipable equip) {
		
		UnlockEquip unlock = UnlockEquip.getUnlockFromEquip(equip.getClass());
		
		for (WeaponModifier s : equip.getWeaponMods()) {
			addStatus(s);
		}
		
		for (int i = 0; i < getNumWeaponSlots(); i++) {
			if (multitools[i] instanceof NothingWeapon) {
				multitools[i] = equip;
				multitools[i].setUser(player);
				currentSlot = i;
 				setEquip();
 				
 				loadout.multitools[currentSlot] = unlock;
 				syncServerLoadoutChange();
 				return new NothingWeapon(player);
			}
		}
		
		Equipable old = multitools[currentSlot];
		
		for (WeaponModifier s : old.getWeaponMods()) {
			removeStatus(s);
		}
		
		multitools[currentSlot] = equip;
		multitools[currentSlot].setUser(player);
		setEquip();
		
		loadout.multitools[currentSlot] = unlock;
		syncServerLoadoutChange();
		
		return old;
	}
	
	/**
	 * Player picks up a new Active Item. 
	 * @param item: Old item if nonempty and a Nothing Item otherwise
	 * @return
	 */
	public ActiveItem pickup(ActiveItem item) {
		
		UnlockActives unlock = UnlockActives.getUnlockFromActive(item.getClass());

		if (activeItem instanceof NothingActive) {
			activeItem = item;
			return new NothingActive(player);
		}
		
		ActiveItem old = activeItem;
		activeItem = item;
		
		loadout.activeItem = unlock;
		syncServerLoadoutChange();

		return old;
	}
	
	/**
	 * empties a slot. Used when running out of ammunition
	 */
	public void emptySlot(int slot) {
		
		for (Status s : multitools[slot].getWeaponMods()) {
			removeStatus(s);
		}
		
		multitools[slot] = new NothingWeapon(player);
		multitools[slot].setUser(player);
		
		currentSlot = slot;
		setEquip();
		
		loadout.multitools[currentSlot] = UnlockEquip.NOTHING;
		
		boolean allEmpty = true;
		for (int i = 0; i < getNumWeaponSlots(); i++) {
			if (!loadout.multitools[i].equals(UnlockEquip.NOTHING)) {
				allEmpty = false;
			}
		}
		
		if (allEmpty) {
			multitools[0] = new Fisticuffs(player);
			multitools[0].setUser(player);
			switchWeapon(1);
		}
		
		syncServerLoadoutChange();
		
		switchDown();
	}
	
	/**
	 * Add a new artifact.
	 */
	public boolean addArtifact(UnlockArtifact artifactUnlock, boolean override) {
//		System.out.println(artifactUnlock.getName() + " " + override);
		Artifact newArtifact =  artifactUnlock.getArtifact();
		int slotsUsed = 0;
		
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			if (!(artifacts[i].equals(UnlockArtifact.NOTHING))) {
				slotsUsed += artifacts[i].getArtifact().getSlotCost();
				
				//new artifact fails to add if a repeat, locked or slot cost is too high
				if (artifacts[i].equals(artifactUnlock)){
					return false;
				} 
				
				if (!artifactUnlock.isUnlocked()){
					return false;
				} 
				
				if (slotsUsed + newArtifact.getSlotCost() > getNumArtifactSlots() && !override) {
					return false;
				}
			} else {
				for (Status s : newArtifact.loadEnchantments(player.getState(), this)) {
					addStatus(s);
				}
				artifacts[i] = artifactUnlock;
				loadout.artifacts[i] = artifactUnlock;
				
				for (int j = i + 1; j < Loadout.maxArtifactSlots; j++) {
					artifacts[j] = UnlockArtifact.NOTHING;
					loadout.artifacts[j] = UnlockArtifact.NOTHING;
				}
				
				if (player.equals((player.getState().getPlayer()))) {
					player.getState().getUiArtifact().syncArtifact();
				}
				
				saveArtifacts();
				syncServerLoadoutChange();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Remove a designated artifact. 
	 */
	public void removeArtifact(UnlockArtifact artifact) {
		
		int indexRemoved = -1;
		
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			if (artifacts[i].equals(artifact)) {
				indexRemoved = i;
				break;
			}
		}

		if (indexRemoved != -1) {
			if (artifacts[indexRemoved] != null) {
				for (Status s : artifacts[indexRemoved].getArtifact().getEnchantment()) {
					removeStatus(s);
				}
			}
			
			for (int i = indexRemoved; i < Loadout.maxArtifactSlots - 1; i++) {
				artifacts[i] = artifacts[i + 1];
				loadout.artifacts[i] = loadout.artifacts[i + 1];
			}
			artifacts[Loadout.maxArtifactSlots - 1] = UnlockArtifact.NOTHING;
			loadout.artifacts[Loadout.maxArtifactSlots - 1] = UnlockArtifact.NOTHING;
		}
		
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
		
		saveArtifacts();
		syncServerLoadoutChange();
	}
	
	/**
	 * This helper function is called when weapon switching to ensure the correct weapon sprite is drawn and that the 
	 * current weapon is kept track of.
	 */
	public void setEquip() {
		currentTool = multitools[currentSlot];
		player.setToolSprite(currentTool.getWeaponSprite().getFrame());
		
		currentTool.setReloading(false);
		currentTool.setCharging(false);
		currentTool.setReloadCd(0);
		currentTool.setChargeCd(0);
		
		//This recalcs stats that are tied to weapons. ex: "player receives 50% more damage when x is equipped".
		calcStats();
	}
	
	/**
	 * This method saves the player's current artifacts into records
	 */
	public void saveArtifacts() {
		for(int i = 0; i < Loadout.maxArtifactSlots; i++) {
			player.getState().getGsm().getRecord().setArtifact(i, artifacts[i].toString());
		}
	}
	
	/**
	 * We override this method so that player-specific fields can adjust properly when stats are modified.
	 * atm, this is only used for weapon slot number changes
	 */
	@Override
	public void calcStats() {
		super.calcStats();
		
		if (player == null) {
			return;
		}
		
		if (currentSlot >= getNumWeaponSlots()) {
			currentSlot = getNumWeaponSlots() - 1;
			setEquip();
		}
		
		if (player.getState().isServer()) {
			if (player.getConnID() == 0) {
				
			} else {
				HadalGame.server.sendPacketToPlayer(player, new SyncPlayerStats(getCurrentTool().getClipSize(), getStat(Stats.MAX_HP), getStat(Stats.MAX_FUEL), getAirblastCost(), getNumWeaponSlots()));
			}
		}
	}
	
	public int getNumWeaponSlots() {
		return Math.min((int) (Loadout.baseWeaponSlots + getStat(Stats.WEAPON_SLOTS)), Loadout.maxWeaponSlots);
	}
	
	public int getNumArtifactSlots() {
		return Math.min((int) (Loadout.baseArtifactSlots + getStat(Stats.ARTIFACT_SLOTS)), Loadout.maxArtifactSlots);
	}
	
	public int getArtifactSlotsRemaining() {
		int slotsUsed = 0;
		
		for (int i = 0; i < getNumArtifactSlots(); i++) {
			slotsUsed += artifacts[i].getArtifact().getSlotCost();
		}
		
		return getNumArtifactSlots() - slotsUsed;
	}
	
	/**
	 * This is called when a loadout changes on the server side. Send message to all clients announcing change
	 */
	public void syncServerLoadoutChange() {
		if (player.getState().isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncServerLoadout(player.getEntityID().toString(), loadout));
		}
	}
	
	/**
	 * These are called when a loadout changes on the client side.(Through hub event) Send message to server announcing change
	 */
	public void syncClientLoadoutChangeWeapon(UnlockEquip equip) {
		if (!player.getState().isServer()) {
			HadalGame.client.client.sendTCP(new Packets.SyncClientLoadout(equip, null, null, null, null));
		}
	}
	
	public void syncClientLoadoutAddArtifact(UnlockArtifact artifact) {
		if (!player.getState().isServer()) {
			HadalGame.client.client.sendTCP(new Packets.SyncClientLoadout(null, artifact, null, null, null));
		}
	}
	
	public void syncClientLoadoutRemoveArtifact(UnlockArtifact artifact) {
		if (!player.getState().isServer()) {
			HadalGame.client.client.sendTCP(new Packets.SyncClientLoadout(null, null, artifact, null, null));
		}
	}
	
	public void syncClientLoadoutChangeActive(UnlockActives active) {
		if (!player.getState().isServer()) {
			HadalGame.client.client.sendTCP(new Packets.SyncClientLoadout(null, null, null, active, null));
		}
	}
	
	public void syncClientLoadoutChangeCharacter(UnlockCharacter character) {
		if (!player.getState().isServer()) {
			HadalGame.client.client.sendTCP(new Packets.SyncClientLoadout(null, null, null, null, character));
		}
	}
	
	public void clearStatuses() {
		statuses.clear();
		statusesChecked.clear();
	}
	
	public void fuelSpend(float cost) {
		currentFuel -= cost;
		if (currentFuel < 0) {
			currentFuel = 0;
		}
	}
	
	public void fuelGain(float fuelRegen) {
		currentFuel += fuelRegen;
		if (currentFuel > getStat(Stats.MAX_FUEL)) {
			currentFuel = getStat(Stats.MAX_FUEL);
		}
	}
	
	@Override
	public void die(BodyData perp, Equipable tool) {
		if (player.isAlive()) {
			
			player.createGibs();
			
			schmuck.getState().onPlayerDeath(player, perp.getSchmuck());
			
			if (player.getMouse() != player.getState().getMouse()) {
				player.getMouse().queueDeletion();
			}
			super.die(perp, tool);
			
			//Send death notification to all players
			if (perp instanceof PlayerBodyData) {
				Player p = (Player)perp.getSchmuck();
				HadalGame.server.addNotificationToAll(player.getState(), player.getName(),  "was killed by " + p.getName());
			}
		}
	}
	
	public Player getPlayer() {	return player;}
	
	public int getExtraJumps() { return numExtraJumps + (int)getStat(Stats.JUMP_NUM); }
	
	public float getJumpPower() { return jumpPow * (1 + getStat(Stats.JUMP_POW)); }
	
	public float getFastFallPower() { return fastFallPow * (1 + getStat(Stats.FASTFALL_POW)); }
	
	public float getHoverPower() { return hoverPow * (1 + getStat(Stats.HOVER_POW)); }
	
	public float getHoverCost() { return hoverCost * (1 + getStat(Stats.HOVER_COST)); }

	public float getAirblastCost() { return airblastCost * (1 + getStat(Stats.BOOST_COST)); }
	
	public int getExtraJumpsUsed() { return extraJumpsUsed;	}

	public void setExtraJumpsUsed(int extraJumpsUsed) {	this.extraJumpsUsed = extraJumpsUsed; }

	public Equipable[] getMultitools() { return multitools; }
	
	public UnlockArtifact[] getArtifacts() {	return artifacts; }
	
	public ActiveItem getActiveItem() {	return activeItem; }

	public int getCurrentSlot() { return currentSlot; }	
		
	public void setCurrentSlot(int currentSlot) { this.currentSlot = currentSlot; }

	public Loadout getLoadout() { return loadout; }

	public float getOverrideHpPercent() { return overrideHpPercent; }

	public void setOverrideHpPercent(float overrideHpPercent) {	this.overrideHpPercent = overrideHpPercent;	}
}
