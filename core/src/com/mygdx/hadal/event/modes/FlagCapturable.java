package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.EventUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.CarryingFlag;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.PacketUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 * A FlagCapturable is an event that serves as an objective for ctf mode that can be captured
 * @author Hufferty Hibbooey
 */
public class FlagCapturable extends Event {

	private static final Vector2 FLAG_SIZE = new Vector2(80, 80);
	private static final float FLAG_LIFESPAN = 240.0f;
	private final static float PARTICLE_DURATION = 5.0f;
	private static final float CHECK_RADIUS = 4.0f;

	//the timer until a dropped flag returns to spawn
	private static final float RETURN_TIME = 40.0f;

	//the team who this flag belongs to
	private final int teamIndex;

	//this is the player that this event is fixed to and the last player that held it
	private Player target;
	private Player lastHolder;
	private float returnTimer;

	//this is a status inflicted upon the flag carrier
	private Status flagDebuff;

	//The spawner that created the flag. Used by bots to locate spawners
	private final FlagSpawner spawner;

	//the last "flag blocker" this touched. Used to prevent bringing flags through spawn walls
	private FlagBlocker lastBlocker;
	//is the flag held by a player? Has the flag been removed from its spawn location?
	private boolean captured, awayFromSpawn;

	//textures that indicate how long until the flag is returned to base
	private final TextureRegion returnMeter, returnBar;
	private float returnPercent, returnDelayed;

	//amount of players currently nearby their dropped flag to speed up its return
	private int numReturning;

	public FlagCapturable(PlayState state, Vector2 startPos, FlagSpawner spawner, int teamIndex) {
		super(state, startPos, FLAG_SIZE, FLAG_LIFESPAN);
		this.spawner = spawner;
		this.teamIndex = teamIndex;

		setEventSprite(Sprite.DIATOM_D);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setGravity(1.0f);
		setSynced(true);
		setReliableCreate(true);

		//set flag's color according to team alignment
		HadalColor color = HadalColor.NOTHING;
		if (teamIndex < AlignmentFilter.currentTeams.length) {
			HadalColor teamColor = AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon();
			color = teamColor;
			new ParticleEntity(state, this, Particle.BRIGHT_TRAIL, 0, 0, true, SyncType.CREATESYNC)
					.setColor(teamColor);
		}

		//make objective marker track this event
		state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_ALERT, color, true, false, false);


		this.returnMeter = Sprite.UI_RELOAD_METER.getFrame();
		this.returnBar = Sprite.UI_RELOAD_BAR.getFrame();

		//we must set this event's layer to make it render underneath players
		setLayer(PlayState.ObjectLayer.HBOX);
	}

	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (!state.isServer()) { return; }
				if (null != fixB) {
					if (!captured) {
						if (fixB instanceof PlayerBodyData playerData) {

							//if this is touching a flag blocker, do not register pickups
							boolean blockPickup = false;
							if (null != lastBlocker) {
								if (lastBlocker.getEventData().getSchmucks().contains(event) &&
										lastBlocker.getEventData().getSchmucks().contains(fixB.getEntity())) {
									blockPickup = true;
								}
							}

							if (!blockPickup) {
								//if the flag touches an enemy player, it is picked up, displaying a notification and tracking the player
								if (teamIndex < AlignmentFilter.currentTeams.length) {
									if (playerData.getLoadout().team != AlignmentFilter.currentTeams[teamIndex]) {
										captured = true;
										awayFromSpawn = true;
										target = playerData.getPlayer();
										lastHolder = target;
										flagDebuff = new CarryingFlag(state, target.getBodyData());
										target.getPlayerData().addStatus(flagDebuff);

										event.getBody().setGravityScale(0.0f);
										String playerName = WeaponUtils.getPlayerColorName(target, MAX_NAME_LENGTH);
										state.getKillFeed().addNotification(UIText.CTF_PICKUP.text(playerName), true);

										spawner.setFlagPresent(false);
									}
								}
							}
						}
					} else {
						//if touching a "flag blocker" while held, the flag is automatically dropped
						if (fixB.getEntity() instanceof FlagBlocker blocker) {
							if (blocker.getTeamIndex() != teamIndex) {
								dropFlag();
								lastBlocker = blocker;
							}
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0.0f, 1.0f, 0, false, true,
				Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0, false, eventData);

		//feetdata is set to make the flag selectively pass through dropthrough platforms
		EventUtils.addFeetFixture(this);

		FixtureBuilder.createFixtureDef(body, new Vector2(), new Vector2(size), true, 0, 0, 0.0f, 1.0f,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_SENSOR), (short) 0).setUserData(eventData);
	}

	private static final float CHECK_INTERVAL = 0.2f;
	private final Vector2 hbLocation = new Vector2();
	private float controllerCount;
	@Override
	public void controller(float delta) {
		//if the flag holder dies, the flag drops and will return after some time
		if (captured) {
			if (!target.isAlive()) {
				dropFlag();
			} else {
				hbLocation.set(target.getPosition());
				setTransform(hbLocation, getAngle());
			}
		} else if (awayFromSpawn) {

			//return time decrementing scales to number of players nearby
			returnTimer -= delta * numReturningToSpeed(numReturning);

			if (returnTimer <= 0.0f) {
				ParticleEntity particle = new ParticleEntity(state, getPixelPosition(), Particle.DIATOM_IMPACT_LARGE,
						PARTICLE_DURATION, true, SyncType.CREATESYNC);
				queueDeletion();

				if (teamIndex < AlignmentFilter.currentTeams.length) {
					particle.setColor(AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon());

					String teamColor = AlignmentFilter.currentTeams[teamIndex].getColoredAdjective();
					teamColor = WeaponUtils.getColorName(AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon(), teamColor);
					state.getKillFeed().addNotification(UIText.CTF_RETURNED.text(teamColor), true);
				}
			}

			//check nearby area for allied players and set return percent
			controllerCount += delta;
			if (controllerCount >= CHECK_INTERVAL) {
				controllerCount = 0.0f;
				hbLocation.set(getPosition());
				numReturning = 0;
				state.getWorld().QueryAABB(fixture -> {
					if (fixture.getUserData() instanceof PlayerBodyData playerData) {
						if (playerData.getLoadout().team == AlignmentFilter.currentTeams[teamIndex]) {
							numReturning++;
						}
					}
					return true;
				},
				hbLocation.x - CHECK_RADIUS, hbLocation.y - CHECK_RADIUS,
				hbLocation.x + CHECK_RADIUS, hbLocation.y + CHECK_RADIUS);
			}
		}
		returnPercent = (RETURN_TIME - returnTimer) / RETURN_TIME;
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		//this makes flag following player less janky for clients when held
		if (captured) {
			if (null != target) {
				hbLocation.set(target.getPosition());
				setTransform(hbLocation, getAngle());
			}
		}
	}

	@Override
	public void clientInterpolation() {
		if (!captured) {
			super.clientInterpolation();
		}
	}

	private static final float UI_SCALE = 0.4f;
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		super.render(batch, entityLocation);
		if (awayFromSpawn && !captured) {

			//draw return meter according to timer
			returnDelayed = Math.min(1.0f, returnDelayed + (returnPercent - returnDelayed) * 0.25f);

			float textX = entityLocation.x - returnMeter.getRegionWidth() * UI_SCALE / 2;
			float textY = entityLocation.y + returnMeter.getRegionHeight() * UI_SCALE + size.y / 2;

			batch.draw(returnBar, textX + 10, textY + 4, returnBar.getRegionWidth() * UI_SCALE * returnDelayed, returnBar.getRegionHeight() * UI_SCALE);
			HadalGame.FONT_SPRITE.draw(batch, UIText.CTF_RETURN.text(), textX + 12, textY + returnMeter.getRegionHeight() * UI_SCALE);
			batch.draw(returnMeter, textX, textY, returnMeter.getRegionWidth() * UI_SCALE, returnMeter.getRegionHeight() * UI_SCALE);

			if (returnDelayed > returnPercent) {
				returnDelayed = 0.0f;
			}
		}
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreateFlag(entityID, getPixelPosition(), teamIndex);
	}

	@Override
	public void onServerSync() {
		if (captured) {
			state.getSyncPackets().add(new PacketsSync.SyncFlagAttached(entityID, target.getEntityID(), getPosition(), getLinearVelocity(),
					state.getTimer(), returnPercent));
		} else {
			state.getSyncPackets().add(new PacketsSync.SyncFlag(entityID, getPosition(), getLinearVelocity(),
					state.getTimer(), returnPercent));
		}
	}

	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);

		if (o instanceof PacketsSync.SyncFlag p) {
			returnPercent = PacketUtil.byteToPercent(p.returnPercent);
			if (o instanceof PacketsSync.SyncFlagAttached p1) {
				HadalEntity entity = state.findEntity(p1.uuidMSBAttached, p1.uuidLSBAttached);
				if (null != entity) {
					if (entity instanceof Player player) {
						target = player;
						captured = true;
						awayFromSpawn = true;
					}
				}
			} else {
				captured = false;
			}
		}
	}

	/**
	 * Repeatedly check if the flag is touching its return event.
	 * This makes it so players do not need to leave and return to their spawner to capture flags
	 */
	public void checkCapture(FlagSpawner flag) {
		//if this hbox touches an enemy flag spawn, it is "captured", scoring a point and disappearing
		if (flag.getTeamIndex() != teamIndex) {

			//in order to capture, you must have your own flag present.
			if (flag.isFlagPresent()) {
				flag.getEventData().preActivate(null, lastHolder);
				queueDeletion();

				if (null != target) {
					if (null != target.getPlayerData()) {
						if (null != flagDebuff) {
							target.getPlayerData().removeStatus(flagDebuff);
						}
					}
				}
			} else {
				flag.triggerFailMessage();
			}
		}
	}

	/**
	 * Drop flag. give notification and set flag properties
	 */
	private void dropFlag() {
		captured = false;
		body.setGravityScale(1.0f);
		returnTimer = RETURN_TIME;

		if (teamIndex < AlignmentFilter.currentTeams.length) {
			String teamColor = AlignmentFilter.currentTeams[teamIndex].getColoredAdjective();
			state.getKillFeed().addNotification(UIText.CTF_DROPPED.text(teamColor), true);
		}
	}

	/**
	 * Get speed of flag returning for number of players returning it
	 */
	private float numReturningToSpeed(int numReturning) {
		switch (numReturning) {
			case 0 -> {
				return 1.0f;
			}
			case 1 -> {
				return 4.0f;
			}
			case 2 -> {
				return 8.0f;
			}
			case 3 -> {
				return 14.0f;
			}
			default -> {
				return 20.0f;
			}
		}
	}

	public FlagSpawner getSpawner() { return spawner; }

	public Player getTarget() { return target; }

	public int getTeamIndex() { return teamIndex; }

	public boolean isCaptured() { return captured; }

	public boolean isAwayFromSpawn() { return awayFromSpawn; }
}
