package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.server.*;
import com.mygdx.hadal.server.Packets.SyncPlayerStats;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Arrays;
import java.util.Objects;

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
		for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
			multitools[i] = UnlocktoItem.getUnlock(loadout.multitools[i], player);
		}
		setEquip();
		
		//acquire artifacts from loadout
		UnlockArtifact[] artifactsTemp = new UnlockArtifact[Loadout.maxArtifactSlots];
		System.arraycopy(loadout.artifacts, 0, artifactsTemp, 0, Loadout.maxArtifactSlots);
		Arrays.fill(loadout.artifacts, UnlockArtifact.NOTHING);
		for (int i = 0; i < Loadout.maxArtifactSlots; i++) {
			addArtifact(artifactsTemp[i], false);
		}
		
		//Acquire active item
		this.activeItem = UnlocktoItem.getUnlock(loadout.activeItem, player);
		this.player.setBodySprite(loadout.character, loadout.team);
		
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
		saveArtifacts();
		
		this.activeItem = UnlocktoItem.getUnlock(newLoadout.activeItem, player);
		player.setBodySprite(newLoadout.character, newLoadout.team);
		
		this.loadout = newLoadout;

		User user = HadalGame.client.getUsers().get(player.getConnID());
		if (user != null) {
			user.setTeamFilter(loadout.team);
		}

		//If this is the player being controlled by the user, update artifact ui
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
	}
	
	/**
	 * This is run when the server receives a request from a client to make a change to their loadout.
	 */
	public void syncLoadoutFromClient(UnlockEquip equip, UnlockArtifact artifactAdd, UnlockArtifact artifactRemove,
									  UnlockActives active, UnlockCharacter character, AlignmentFilter team) {
		if (equip != null) {
			pickup(Objects.requireNonNull(UnlocktoItem.getUnlock(equip, getPlayer())));
		}
		if (artifactAdd != null) {
			addArtifact(artifactAdd, false);
		}
		if (artifactRemove != null) {
			removeArtifact(artifactRemove);
		}
		if (active != null) {
			pickup(Objects.requireNonNull(UnlocktoItem.getUnlock(active, getPlayer())));
		}
		if (character != null) {
        	setCharacter(character);
		}
		if (team != null) {
			setTeam(team);
		}
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
			for (Status s : a.getArtifact().loadEnchantments(player.getState(), this)) {
				addStatus(s);
				s.setArtifact(a);
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
		syncServerLoadoutChange();
		return old;
	}
	
	/**
	 * Player picks up a new Active Item. 
	 * @param item: Old item if nonempty and a Nothing Item otherwise
	 * @return the previous active item (if existent)
	 */
	public ActiveItem pickup(ActiveItem item) {
		
		UnlockActives unlock = UnlockActives.getUnlockFromActive(item.getClass());
		
		ActiveItem old = activeItem;
		activeItem = item;
		
		activeItem.setUser(player);
		
		loadout.activeItem = unlock;
		
		//active items start off charged in the hub
		if (player.getState().isHub()) {
			activeItem.setCurrentChargePercent(1.0f);
		} else {
			activeItem.setCurrentChargePercent(getStat(Stats.STARTING_CHARGE));
		}
		
		syncServerLoadoutChange();

		return old;
	}
	
	/**
	 * Add a new artifact. override is used for effects like Administrator's card that overrides normal restrictions
	 * returns whether the artifact adding was successful
	 */
	public boolean addArtifact(UnlockArtifact artifactUnlock, boolean override) {

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
				for (Status s : newArtifact.loadEnchantments(player.getState(), this)) {
					addStatus(s);
					s.setArtifact(artifactUnlock);
				}
				loadout.artifacts[i] = artifactUnlock;

				syncArtifacts(override);
				
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
		
		syncArtifacts(false);
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
	 */
	public void syncArtifacts(boolean override) {
		
		if (!override) {
			checkArtifactSlotCosts();
			saveArtifacts();
		}
		
		if (player.equals((player.getState().getPlayer()))) {
			player.getState().getUiArtifact().syncArtifact();
		}
		
		syncServerLoadoutChange();
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

		User user = HadalGame.server.getUsers().get(player.getConnID());
		if (user != null) {
			user.setTeamFilter(team);
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
		
		//if this is a client, we send them a packet telling them to update their ui to match the new stats.
		if (player.getState().isServer()) {
			if (player.getConnID() != 0) {
				HadalGame.server.sendPacketToPlayer(player, new SyncPlayerStats(getCurrentTool().getClipSize(), getStat(Stats.MAX_HP), getStat(Stats.MAX_FUEL), 
						getAirblastCost(), getNumWeaponSlots(), getNumArtifactSlots(), getStat(Stats.HEALTH_VISIBILITY)));
			}
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
		if (player.getState().isServer()) {
			
			if (GameStateManager.currentMode == Mode.SINGLE) {
				return Math.min((int) (player.getState().getGsm().getRecord().getSlotsUnlocked() + getStat(Stats.ARTIFACT_SLOTS)), Loadout.maxArtifactSlots);
			} else {
				return Math.min((int) (player.getState().getGsm().getSetting().getArtifactSlots() + getStat(Stats.ARTIFACT_SLOTS)), Loadout.maxArtifactSlots);
			}
		} else {
			return Math.min(((ClientState) player.getState()).getUiPlay().getOverrideArtifactSlots(), Loadout.maxArtifactSlots);
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
			HadalGame.client.sendTCP(new Packets.SyncClientLoadout(equip, null, null, null, null, null));
		}
	}
	
	public void syncClientLoadoutAddArtifact(UnlockArtifact artifact) {
		if (!player.getState().isServer()) {
			HadalGame.client.sendTCP(new Packets.SyncClientLoadout(null, artifact, null, null, null, null));
		}
	}
	
	public void syncClientLoadoutRemoveArtifact(UnlockArtifact artifact) {
		if (!player.getState().isServer()) {
			HadalGame.client.sendTCP(new Packets.SyncClientLoadout(null, null, artifact, null, null, null));
		}
	}
	
	public void syncClientLoadoutChangeActive(UnlockActives active) {
		if (!player.getState().isServer()) {
			HadalGame.client.sendTCP(new Packets.SyncClientLoadout(null, null, null, active, null, null));
		}
	}
	
	public void syncClientLoadoutChangeCharacter(UnlockCharacter character) {
		if (!player.getState().isServer()) {
			HadalGame.client.sendTCP(new Packets.SyncClientLoadout(null, null, null, null, character, null));
		}
	}

	public void syncClientLoadoutChangeTeam(AlignmentFilter team) {
		if (!player.getState().isServer()) {
			HadalGame.client.sendTCP(new Packets.SyncClientLoadout(null, null, null, null, null, team));
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
	public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
		float damage = super.receiveDamage(basedamage, knockback, perp, procEffects, tags);

		//this keeps track of total damage received during rounds
		if (player.getState().isServer()) {
			User user = HadalGame.server.getUsers().get(getPlayer().getConnID());
			if (user != null) {
				SavedPlayerFieldsExtra field = user.getScoresExtra();
				if (damage > 0.0f) {
					field.incrementDamageReceived(damage);
				}
			}
		}
		
		return damage;
	}
	
	private static final float scrapMultiplier = 0.25f;
	private static final int baseScrapDrop = 1;
	private static final float equipDropLifepan = 15.0f;
	@Override
	public void die(BodyData perp, DamageTypes... tags) {
		if (player.isAlive()) {
			
			DespawnType type = DespawnType.GIB;

			//in the case of a disconnect, this is a special death with teleport particles instead of frags
			for (final DamageTypes tag : tags) {
				if (tag == DamageTypes.DISCONNECT) {
					type = DespawnType.TELEPORT;
					break;
				}
			}

			//in weapon drop mode, players will drop their currently held weapon on death (unless it is default weapon or nothing)
			if (player.getState().getGsm().getSetting().getLoadoutType() == 3 && !player.getState().isHub()) {
				if (!loadout.multitools[currentSlot].equals(UnlockEquip.NOTHING) && !loadout.multitools[currentSlot].equals(UnlockEquip.SPEARGUN_NERFED)) {
					new PickupEquip(player.getState(), player.getPixelPosition(), loadout.multitools[currentSlot], equipDropLifepan);
				}
			}

			//despawn sprite helper. This triggers death animations
			player.getSpriteHelper().despawn(type, player.getPixelPosition(), player.getLinearVelocity());
			player.setDespawnType(type);
			if (type == DespawnType.TELEPORT) {
				warpAnimation();
			} else {

				//process score change if pvp modes (and drop eggplants if suitable mode)
				if (player.getState().isPvp() && !player.getState().isHub() && player.getState().getGsm().getSetting().getPVPMode() == 1) {
					User user = HadalGame.server.getUsers().get(player.getConnID());
					if (user != null) {
						SavedPlayerFields field = user.getScores();
						int score = (int) (field.getScore() * scrapMultiplier);
						if (score < 0) {
							score = 0;
						}
						player.getState().getUiExtra().changeFields(player, -score, 0, 0.0f, 0.0f, false);
						WeaponUtils.spawnScrap(player.getState(), score + baseScrapDrop, player.getPixelPosition(), true);
					}
				}

				//Send death notification to all players.
				if (perp instanceof PlayerBodyData) {
					player.getState().getKillFeed().addMessage(((Player) perp.getSchmuck()), player, null, tags);
					HadalGame.server.sendToAllTCP(new Packets.SyncKillMessage(((Player) perp.getSchmuck()).getConnID(), player.getConnID(), null, tags));
				} else if (perp.getSchmuck() instanceof Enemy) {
					player.getState().getKillFeed().addMessage(null, player, ((Enemy) perp.getSchmuck()).getEnemyType(), tags);
					HadalGame.server.sendToAllTCP(new Packets.SyncKillMessage(-1, player.getConnID(), ((Enemy) perp.getSchmuck()).getEnemyType(), tags));
				} else {
					player.getState().getKillFeed().addMessage(null, player, null, tags);
					HadalGame.server.sendToAllTCP(new Packets.SyncKillMessage(-1, player.getConnID(), null, tags));
				}
			}
			
			schmuck.getState().onPlayerDeath(player, perp.getSchmuck());
			
			//delete the player's mouse pointer
			if (player.getMouse() != player.getState().getMouse()) {
				player.getMouse().queueDeletion();
			}
			
			//run the unequip method for current weapon (certain weapons need this to stop playing a sound)
			if (currentTool != null) {
				currentTool.unequip(player.getState());
			}
			
			super.die(perp, tags);
		}
	}
	
	/**
	 * This animation is played when entering levels and respawning
	 */
	public void warpAnimation() {
		new ParticleEntity(player.getState(), new Vector2(player.getPixelPosition()).sub(0, player.getSize().y / 2), Particle.TELEPORT, 0.5f, true, particleSyncType.CREATESYNC);
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

	public Equippable[] getMultitools() { return multitools; }
	
	public ActiveItem getActiveItem() {	return activeItem; }

	public int getCurrentSlot() { return currentSlot; }	
		
	public void setCurrentSlot(int currentSlot) { this.currentSlot = currentSlot; }

	public Loadout getLoadout() { return loadout; }

	public boolean isOverrideOutOfAmmo() { return overrideOutOfAmmo; }

	public void setOverrideOutOfAmmo(boolean overrideOutOfAmmo) { this.overrideOutOfAmmo = overrideOutOfAmmo; }
}
