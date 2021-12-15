package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Arrays;

/**
 * This is the data for a player and contains player-specific fields like airblast, jump stats, etc.
 * @author Lallbladder Lemaker
 */
public class PlayerBodyData extends BodyData {
		
	private static final int numExtraJumps = 1;
	private int extraJumpsUsed = 0;
	private static final float jumpPow = 25.0f;
	
	private static final float fastFallPow = 17.0f;

	private static final int hoverCost = 4;
	private static final float hoverPow = 5.0f;
	
	private static final int airblastCost = 25;
	
	//This is the player's current loadout
	private Loadout loadout;
	
	//This is a list of the player's weapons
	private Equippable[] multitools;
	
	//This is the player's active item
	private ActiveItem activeItem;
	
	//This is the slot number of the player's currently selected weapon
	private int currentSlot;
	
	//This is the player's last used slot. (Used for switch-to-last-slot button)
	private int lastSlot = 1;
	
	private Player player;

	//This is used by clients to display each player's hp percent in the ui and artifact slots in hub
	private boolean overrideOutOfAmmo;
	
	public PlayerBodyData(Player player, Loadout loadout) {
		super(player, player.getBaseHp());
		this.player = player;
		this.loadout = new Loadout(loadout);
	}
	
	/**
	 * This is called when creating a brand new player with a reset loadout
	 */
	public void initLoadout() {
		clearStatuses();

		//Acquire weapons from loadout
		this.multitools = new Equippable[Loadout.maxWeaponSlots];
		Arrays.fill(multitools, new NothingWeapon(player));
		syncEquip(loadout.multitools);
		syncArtifact(loadout.artifacts);
		syncActive(loadout.activeItem);
		setCharacter(loadout.character);
		setTeam(loadout.team);
	}
	
	/**
	 * This is called by the client for players that receive a new loadout from the server.
	 * We give the player the new loadout information.
	 * 
	 * @param loadout: The new loadout for the player
	 */
	public void syncLoadout(Loadout loadout, boolean save) {
		Loadout newLoadout = new Loadout(loadout);

		syncEquip(newLoadout.multitools);
		syncArtifact(newLoadout.artifacts);
		syncActive(newLoadout.activeItem);
		setCharacter(newLoadout.character);
		setTeam(newLoadout.team);

		if (save) {
			saveArtifacts();
		}

		this.loadout = newLoadout;

		if (player.getUser() != null) {
			player.getUser().setTeamFilter(loadout.team);
		}
	}

	public void syncEquip(UnlockEquip[] equip) {
		for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
			multitools[i] = UnlocktoItem.getUnlock(equip[i], player);
			loadout.multitools[i] = equip[i];
		}
		setEquip();
	}

	public void syncArtifact(UnlockArtifact[] artifact) {

		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			removeArtifactStatus(this.loadout.artifacts[i]);
		}
		loadout.artifacts = new UnlockArtifact[Loadout.maxArtifactSlots];

		UnlockArtifact[] artifactsTemp = new UnlockArtifact[Loadout.maxArtifactSlots];
		System.arraycopy(artifact, 0, artifactsTemp, 0, Loadout.maxArtifactSlots);
		Arrays.fill(loadout.artifacts, UnlockArtifact.NOTHING);
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			addArtifact(artifactsTemp[i], false, false);
		}

		//add map modifiers as 0-cost, overriding, invisible artifacts
		for (UnlockArtifact modifier: player.getState().getMapModifiers()) {
			addArtifact(modifier, false, false);
		}

		//If this is the player being controlled by the user, update artifact ui
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
	}

	public void syncActive(UnlockActives active) {
		this.activeItem = UnlocktoItem.getUnlock(active, player);
		this.loadout.activeItem = active;
	}

	/**
	 * This is run when transitioning the player into a new map/world or respawning
	 * @param newPlayer: the new player that this data belongs to.
	 */
	public void updateOldData(Player newPlayer) {
		this.setEntity(newPlayer);
		this.schmuck = newPlayer;
		this.player = newPlayer;
		
		clearStatuses();
		
		for (Equippable e : multitools) {
			e.setUser(player);
		}
		
		for (UnlockArtifact a : loadout.artifacts) {
			a.getArtifact().loadEnchantments(player.getState(), this);
			if (a.getArtifact().getEnchantment() != null) {
				addStatus(a.getArtifact().getEnchantment());
				a.getArtifact().getEnchantment().setArtifact(a);
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
	 */
	public Equippable pickup(Equippable equip) {
		
		UnlockEquip unlock = UnlockEquip.getUnlockFromEquip(equip.getClass());
		
		int slotToReplace = currentSlot;
		
		for (int i = 0; i < getNumWeaponSlots(); i++) {
			if (multitools[i] instanceof NothingWeapon || multitools[i].isOutofAmmo()) {
				slotToReplace = i;
				break;
			}
		}

		Equippable old = multitools[slotToReplace];
		
		multitools[slotToReplace] = equip;
		multitools[slotToReplace].setUser(player);
		currentSlot = slotToReplace;
		setEquip();
		
		loadout.multitools[slotToReplace] = unlock;
		syncServerEquipChange(loadout.multitools);
		return old;
	}
	
	/**
	 * Player picks up a new Active Item. 
	 * @param item: Old item if nonempty and a Nothing Item otherwise
	 */
	public void pickup(ActiveItem item) {
		
		UnlockActives unlock = UnlockActives.getUnlockFromActive(item.getClass());
		
		activeItem = item;
		
		activeItem.setUser(player);
		
		loadout.activeItem = unlock;
		
		//active items start off charged in the hub
		if (player.getState().getMode().isHub()) {
			activeItem.setCurrentChargePercent(1.0f);
		} else {
			activeItem.setCurrentChargePercent(getStat(Stats.STARTING_CHARGE));
		}

		syncServerActiveChange(unlock);
	}
	
	/**
	 * Add a new artifact.
	 * @param override whether this change should override artifact limits (like admin's card)
	 * @param save whether this change should be saved into loadout file (like special mode modifiers shouldn't)
	 * returns whether the artifact adding was successful
	 */
	public boolean addArtifact(UnlockArtifact artifactUnlock, boolean override, boolean save) {

		if (artifactUnlock.equals(UnlockArtifact.NOTHING)) { return false; }

		Artifact newArtifact =  artifactUnlock.getArtifact();
		int slotsUsed = 0;
		
		//iterate through all artifacts and count the number of slots used
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			
			//new artifact fails to add if slot cost is too high
			slotsUsed += loadout.artifacts[i].getArtifact().getSlotCost();
			
			if (slotsUsed + newArtifact.getSlotCost() > getNumArtifactSlots() && !override) {
				return false;
			}
			
			if (!(loadout.artifacts[i].equals(UnlockArtifact.NOTHING))) {
				
				//new artifact fails to add if a repeat
				if (loadout.artifacts[i].equals(artifactUnlock)) {
					return false;
				} 
				
			} else {

				//when we reach a NOTHING (empty slot), we add the artifact
				newArtifact.loadEnchantments(player.getState(), this);
				if (newArtifact.getEnchantment() != null) {
					addStatus(newArtifact.getEnchantment());
					newArtifact.getEnchantment().setArtifact(artifactUnlock);
				}
				loadout.artifacts[i] = artifactUnlock;

				syncArtifacts(override, save);
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Remove a designated artifact. 
	 */
	public void removeArtifact(UnlockArtifact artifact) {
		
		if (artifact.equals(UnlockArtifact.NOTHING)) { return; }
		
		int indexRemoved = -1;
		
		//iterate through artifacts until we find the one we're trying to remove
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {			
			if (loadout.artifacts[i].equals(artifact)) {
				indexRemoved = i;
				break;
			}
		}

		//if found, remove all of the artifact's statuses and move other artifacts up in the list
		if (indexRemoved != -1) {
			if (loadout.artifacts[indexRemoved] != null) {
				removeArtifactStatus(artifact);
			}

			System.arraycopy(loadout.artifacts, indexRemoved + 1, loadout.artifacts, indexRemoved,
				Loadout.maxArtifactSlots - 1 - indexRemoved);
			loadout.artifacts[Loadout.maxArtifactSlots - 1] = UnlockArtifact.NOTHING;
		}
		
		syncArtifacts(false, true);
	}
	
	/**
	 * This checks if the player has too many artifacts and removes all of the ones over carrying capacity
	 */
	public void checkArtifactSlotCosts() {
		int slotsUsed = 0;
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			slotsUsed += loadout.artifacts[i].getArtifact().getSlotCost();
			if (slotsUsed > getNumArtifactSlots()) {
				removeArtifact(loadout.artifacts[i]);
			}
		}
	}

	/**
	 * This is called when a player's artifacts may change to sync ui and clients
	 * @param override whether this change should override artifact limits
	 * @param save whether this change should be saved into loadout file
	 */
	public void syncArtifacts(boolean override, boolean save) {
		
		if (!override) {
			checkArtifactSlotCosts();

			if (save) {
				saveArtifacts();
			}
		}
		
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
		syncServerArtifactChange(loadout.artifacts, save);
		calcStats();
	}
	
	/**
	 * This helper function is called when weapon switching to ensure the correct weapon sprite is drawn and that the 
	 * current weapon is kept track of.
	 */
	public void setEquip() {
		if (currentTool != null) {
			currentTool.unequip(player.getState());
		}
		currentTool = multitools[currentSlot];
		player.setToolSprite(currentTool.getWeaponSprite().getFrame());
		
		currentTool.equip(player.getState());
		
		//This recalcs stats that are tied to weapons. ex: "player receives 50% more damage when x is equipped".
		calcStats();
		
		//play sounds for weapon switching
		SoundEffect.LOCKANDLOAD.playExclusive(player.getState(), null, player, 0.5f, true);
	}

	/**
	 * This is called when switching teams.
	 */
	public void setTeam(AlignmentFilter team) {
		loadout.team = team;
		player.setBodySprite(null, team);
		if (player.getUser() != null) {
			player.getUser().setTeamFilter(team);
		}
	}

	public void setCharacter(UnlockCharacter character) {
		loadout.character = character;
		player.setBodySprite(character, null);
	}

	/**
	 * This method saves the player's current artifacts into records
	 */
	public void saveArtifacts() {
		if (player.equals(player.getState().getPlayer())) {
			for(int i = 0; i < Loadout.maxArtifactSlots; i++) {
				player.getState().getGsm().getLoadout().setArtifact(i, loadout.artifacts[i].toString());
			}
		}
	}
	
	/**
	 * We override this method so that player-specific fields can adjust properly when stats are modified.
	 * atm, this is only used for weapon slot number changes
	 */
	@Override
	public void calcStats() {
		super.calcStats();
		
		if (player == null) { return; }
		
		if (currentSlot >= getNumWeaponSlots()) {
			currentSlot = getNumWeaponSlots() - 1;
			setEquip();
		}
	}
	
	/**
	 * This returns the number of weapon slots after modifications
	 */
	public int getNumWeaponSlots() {
		return Math.min((int) (Loadout.baseWeaponSlots + getStat(Stats.WEAPON_SLOTS)), Loadout.maxWeaponSlots);
	}
	
	/**
	 * This returns the number of artifact slots after modifications
	 * The extra if/else is there b/c artifact slots are checked by the client when they use the reliquary hub event.
	 */
	public int getNumArtifactSlots() {
		if (GameStateManager.currentMode == Mode.SINGLE) {
			return Math.min((int) (player.getState().getGsm().getRecord().getSlotsUnlocked() + getStat(Stats.ARTIFACT_SLOTS)), Loadout.maxArtifactSlots);
		} else {
			return Math.min((int) (player.getState().getGsm().getSetting().getArtifactSlots() + getStat(Stats.ARTIFACT_SLOTS)), Loadout.maxArtifactSlots);
		}
	}
	
	/**
	 * This returns the number of unused artifact slots
	 */
	public int getArtifactSlotsRemaining() {
		int slotsUsed = 0;
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			slotsUsed += loadout.artifacts[i].getArtifact().getSlotCost();
		}
		return getNumArtifactSlots() - slotsUsed;
	}
	
	/**
	 * These are called when a loadout changes on the server side. Send message to all clients announcing change
	 * the "save" input for this and adding/removing artifacts indicates whether the client should save this change
	 * It is false for artifact changes that result from mode modifications
	 */
	public void syncServerEquipChange(UnlockEquip[] equip) {
		HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncEquipServer(player.getConnId(), equip));
	}

	public void syncServerArtifactChange(UnlockArtifact[] artifact, boolean save) {
		HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncArtifactServer(player.getConnId(), artifact, save));
	}

	public void syncServerActiveChange(UnlockActives active) {
		HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncActiveServer(player.getConnId(), active));
	}

	public void syncServerCharacterChange(UnlockCharacter character) {
		HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncCharacterServer(player.getConnId(), character));
	}

	public void syncServerTeamChange(AlignmentFilter team) {
		HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncTeamServer(player.getConnId(), team));
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
		if (currentFuel < 0) {
			currentFuel = 0;
		}
	}

	//mapping of players that damaged this player recently. Value = amount of damage dealt and decreases over time
	//used to calculate damage reduction from groups and also to process assists
	private final ObjectMap<PlayerBodyData, Float> recentDamagedBy = new ObjectMap<>();
	@Override
	public float receiveDamage(float baseDamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox, DamageTypes... tags) {
		float damage = baseDamage * getGroupDamageReduction(recentDamagedBy.size);
		damage = super.receiveDamage(damage, knockback, perp, procEffects, hbox, tags);

		if (perp.schmuck.getHitboxfilter() != player.getHitboxfilter()) {
			if (perp instanceof PlayerBodyData playerData) {
				recentDamagedBy.put(playerData, recentDamagedBy.get(playerData, 0.0f) + damage);
			}
		}

		//this keeps track of total damage received during rounds
		if (player.getState().isServer()) {
			if (player.getUser() != null) {
				SavedPlayerFieldsExtra field = getPlayer().getUser().getScoresExtra();
				if (damage > 0.0f) {
					field.incrementDamageReceived(damage);
				}
			}
		}

		//when the player is damaged (or spectator target is damaged) we shake the screen a little
		if (player.equals(player.getState().getPlayer()) && damage > 0.0f) {
			CameraUtil.inflictTrauma(player.getState().getGsm(), damage);
		}
		if (player.getState().getKillFeed() != null) {
			if (player.getState().isSpectatorMode() || player.getState().getKillFeed().isRespawnSpectator()) {
				if (player.equals(player.getState().getUiSpectator().getSpectatorTarget()) && damage > 0.0f) {
					CameraUtil.inflictTrauma(player.getState().getGsm(), damage);
				}
			}
		}

		return damage;
	}
	
	@Override
	public void die(BodyData perp, DamageTypes... tags) {
		if (player.isAlive()) {
			
			DespawnType type = DespawnType.GIB;

			//in the case of a disconnect, this is a special death with teleport particles instead of frags
			for (DamageTypes tag : tags) {
				if (tag == DamageTypes.DISCONNECT) {
					type = DespawnType.TELEPORT;
					break;
				}
				if (tag == DamageTypes.FIRE || tag == DamageTypes.ENERGY) {
					type = DespawnType.VAPORIZE;
					break;
				}
			}

			//despawn sprite helper. This triggers death animations
			player.getSpriteHelper().despawn(type, player.getPixelPosition(), player.getLinearVelocity());
			player.setDespawnType(type);
			if (type == DespawnType.TELEPORT) {
				warpAnimation();
			} else {

				//Send death notification to all players.
				if (perp instanceof PlayerBodyData playerData) {
					player.getState().getKillFeed().addMessage(playerData.getPlayer(), player, null, tags);
					HadalGame.server.sendToAllTCP(new Packets.SyncKillMessage(playerData.getPlayer().getConnId(), player.getConnId(), null, tags));
				} else if (perp.getSchmuck() instanceof Enemy enemyData) {
					player.getState().getKillFeed().addMessage(null, player, enemyData.getEnemyType(), tags);
					HadalGame.server.sendToAllTCP(new Packets.SyncKillMessage(-1, player.getConnId(), enemyData.getEnemyType(), tags));
				} else {
					player.getState().getKillFeed().addMessage(null, player, null, tags);
					HadalGame.server.sendToAllTCP(new Packets.SyncKillMessage(-1, player.getConnId(), null, tags));
				}
			}
			
			//delete the player's mouse pointer
			if (player.getMouse() != player.getState().getMouse()) {
				player.getMouse().queueDeletion();
			}
			
			//run the unequip method for current weapon (certain weapons need this to stop playing a sound)
			if (currentTool != null) {
				currentTool.unequip(player.getState());
			}
			
			super.die(perp, tags);
			schmuck.getState().getMode().processPlayerDeath(schmuck.getState(), perp.getSchmuck(), player, tags);
		}
	}

	private final Array<PlayerBodyData> damagedByToRemove = new Array<>();
	private static final float decrementOverTime = 8.0f;
	/**
	 * This processes the map of players that have damaged this player recently
	 * @param delta; time since last processing
	 */
	public void processRecentDamagedBy(float delta) {
		damagedByToRemove.clear();

		//decrement the timer for all damaged-by players according to time. Remove players that damaged too long ago
		for (ObjectMap.Entry<PlayerBodyData, Float> entry: recentDamagedBy) {
			recentDamagedBy.put(entry.key, entry.value - delta * decrementOverTime);

			if (entry.value <= 0.0f) {
				damagedByToRemove.add(entry.key);
			}
		}
		for (PlayerBodyData playerData: damagedByToRemove) {
			recentDamagedBy.remove(playerData);
		}
	}

	/**
	 * @param numDamagedBy: the number of unique players that have recently damaged this player
	 * @return damage multipler
	 */
	private static float getGroupDamageReduction(int numDamagedBy) {
		return switch (numDamagedBy) {
			case 0, 1 -> 1.0f;
			case 2 -> 0.8f;
			case 3 -> 0.6f;
			case 4 -> 0.4f;
			case 5 -> 0.2f;
			default -> 0.1f;
		};
	}

	/**
	 * This animation is played when disconnecting
	 */
	public void warpAnimation() {
		new ParticleEntity(player.getState(), new Vector2(player.getPixelPosition()).sub(0, player.getSize().y / 2), Particle.TELEPORT,
				1.0f, true, SyncType.CREATESYNC);
	}

	public ObjectMap<PlayerBodyData, Float> getRecentDamagedBy() { return recentDamagedBy; }

	public Player getPlayer() {	return player;}
	
	public int getExtraJumps() { return numExtraJumps + (int)getStat(Stats.JUMP_NUM); }
	
	public float getJumpPower() { return jumpPow * (1 + getStat(Stats.JUMP_POW)); }
	
	public float getFastFallPower() { return fastFallPow * (1 + getStat(Stats.FASTFALL_POW)); }
	
	public float getHoverPower() { return hoverPow * (1 + getStat(Stats.HOVER_POW)); }
	
	public float getHoverCost() { return hoverCost * (1 + getStat(Stats.HOVER_COST)); }

	public float getAirblastCost() { return airblastCost * (1 + getStat(Stats.BOOST_COST)); }
	
	public int getExtraJumpsUsed() { return extraJumpsUsed;	}

	public void setExtraJumpsUsed(int extraJumpsUsed) {	this.extraJumpsUsed = extraJumpsUsed; }

	public Equippable[] getMultitools() { return multitools; }
	
	public ActiveItem getActiveItem() {	return activeItem; }

	public int getCurrentSlot() { return currentSlot; }	
		
	public void setCurrentSlot(int currentSlot) { this.currentSlot = currentSlot; }

	public Loadout getLoadout() { return loadout; }

	public boolean isOverrideOutOfAmmo() { return overrideOutOfAmmo; }

	public void setOverrideOutOfAmmo(boolean overrideOutOfAmmo) { this.overrideOutOfAmmo = overrideOutOfAmmo; }
}
