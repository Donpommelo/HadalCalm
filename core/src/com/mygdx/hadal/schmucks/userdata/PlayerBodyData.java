package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerSelfOnClient;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.helpers.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.users.StatsManager;
import com.mygdx.hadal.utils.CameraUtil;

/**
 * This is the data for a player and contains player-specific fields like airblast, jump stats, loadout etc.
 * @author Lallbladder Lemaker
 */
public class PlayerBodyData extends BodyData {
		
	private Player player;

	public PlayerBodyData(Player player) {
		super(player, player.getBaseHp());
		this.player = player;
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
	}

	/**
	 * We override this method so that player-specific fields can adjust properly when stats are modified.
	 * atm, this is only used for weapon slot number changes, as well as camera modifiers
	 */
	@Override
	public void calcStats() {
		super.calcStats();
		
		if (player == null) { return; }

		//vision modifiers should be applies whenever stats are modified
		if (player.getUser().equals(HadalGame.usm.getOwnUser())) {
			player.getState().getCameraManager().setZoomModifier(getStat(Stats.VISION_RADIUS));
		}

		player.getEquipHelper().postCalcStats();
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
	public float receiveDamage(float baseDamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
							   DamageSource source, DamageTag... tags) {
		float damage = baseDamage * getGroupDamageReduction(recentDamagedBy.size);
		damage = super.receiveDamage(damage, knockback, perp, procEffects, hbox, source, tags);

		if (perp.schmuck.getHitboxFilter() != player.getHitboxFilter()) {
			if (perp instanceof PlayerBodyData playerData) {
				recentDamagedBy.put(playerData, recentDamagedBy.get(playerData, 0.0f) + damage);
			}
		}

		//this keeps track of total damage received during rounds
		if (player.getState().isServer()) {
			if (player.getUser() != null) {
				StatsManager statsManager = getPlayer().getUser().getStatsManager();
				if (damage > 0.0f) {
					statsManager.incrementDamageReceived(damage);
				}
			}
		}

		//when the player is damaged (or spectator target is damaged) we shake the screen a little
		if (player.getUser().equals(HadalGame.usm.getOwnUser()) && damage > 0.0f) {
			CameraUtil.inflictTrauma(damage);
		}
		if (player.getState().getKillFeed() != null) {
			if (player.getState().isSpectatorMode() || player.getState().getKillFeed().isRespawnSpectator()) {
				if (player.equals(player.getState().getUiSpectator().getSpectatorTarget()) && damage > 0.0f) {
					CameraUtil.inflictTrauma(damage);
				}
			}
		}
		return damage;
	}

	@Override
	public void die(BodyData perp, DamageSource source, DamageTag... tags) {

		//set death info to be sent to clients once death is processed
		player.setDamageSource(source);
		player.setDamageTags(tags);
		player.setPerpID(perp.getSchmuck().getEntityID());

		if (player.isAlive()) {

			DespawnType type = DespawnType.GIB;

			//in the case of a disconnect, this is a special death with teleport particles instead of frags
			if (source == DamageSource.DISCONNECT) {
				type = DespawnType.TELEPORT;
			} else {
				for (DamageTag tag : tags) {
					if (tag == DamageTag.FIRE || tag == DamageTag.ENERGY) {
						type = DespawnType.VAPORIZE;
						break;
					}
				}
			}

			//despawn sprite helper. This triggers death animations
			player.getSpriteHelper().despawn(type, player.getPixelPosition(), player.getLinearVelocity());

			//process kill feed messages (unless "dead" player is just disconnected)
			if (type != DespawnType.TELEPORT) {
				if (perp instanceof PlayerBodyData playerData) {
					player.getState().getKillFeed().addMessage(playerData.getPlayer(), player, null, source, tags);
				} else if (perp.getSchmuck() instanceof Enemy enemyData) {
					player.getState().getKillFeed().addMessage(null, player, enemyData.getEnemyType(), source, tags);
				} else {
					player.getState().getKillFeed().addMessage(null, player, null, source, tags);
				}
			}

			//for the client's own player, death is processed and signalled to server
			if (!player.getState().isServer() && this.getPlayer() instanceof PlayerSelfOnClient) {
				player.setAlive(false);
				((ClientState) player.getState()).removeEntity(player.getEntityID());
				HadalGame.client.sendTCP(new Packets.DeleteClientSelf(perp.getSchmuck().getEntityID(), source, tags));
			}
			
			//run the unequip method for current weapon (certain weapons need this to stop playing a sound)
			if (player.getEquipHelper().getCurrentTool() != null) {
				player.getEquipHelper().getCurrentTool().unequip(player.getState());
			}

			super.die(perp, source, tags);

			schmuck.getState().getMode().processPlayerDeath(schmuck.getState(), perp.getSchmuck(), player, source, tags);
		}
	}

	private final Array<PlayerBodyData> damagedByToRemove = new Array<>();
	private static final float DECREMENT_OVER_TIME = 6.0f;
	/**
	 * This processes the map of players that have damaged this player recently
	 * @param delta; time since last processing
	 */
	public void processRecentDamagedBy(float delta) {
		damagedByToRemove.clear();

		//decrement the timer for all damaged-by players according to time. Remove players that damaged too long ago
		for (ObjectMap.Entry<PlayerBodyData, Float> entry : recentDamagedBy) {
			recentDamagedBy.put(entry.key, entry.value - delta * DECREMENT_OVER_TIME);

			if (entry.value <= 0.0f) {
				damagedByToRemove.add(entry.key);
			}
		}
		for (PlayerBodyData playerData : damagedByToRemove) {
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
			case 2 -> 0.75f;
			case 3 -> 0.5f;
			case 4 -> 0.25f;
			default -> 0.1f;
		};
	}

	public ObjectMap<PlayerBodyData, Float> getRecentDamagedBy() { return recentDamagedBy; }

	public Player getPlayer() {	return player;}
}
