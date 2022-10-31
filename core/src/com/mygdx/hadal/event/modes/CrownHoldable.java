package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
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
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 *
 *  @author Himbino Hectmaker
 */
public class CrownHoldable extends Event {

	private static final Vector2 FLAG_SIZE = new Vector2(80, 80);
	private static final float FLAG_LIFESPAN = 240.0f;

	//the timer until a dropped crown returns to spawn
	private static final float RETURN_TIME = 10.0f;

	//this is the player that this event is fixed to
	private Player target;
	private float returnTimer;

	//is the flag held by a player? Has the flag been removed from its spawn location?
	private boolean captured, awayFromSpawn;

	//counter keeps track of player score incrementing
	private float timeCount;

	public CrownHoldable(PlayState state, Vector2 startPos) {
		super(state, startPos, FLAG_SIZE, FLAG_LIFESPAN);

		setEventSprite(Sprite.DIATOM_D);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setGravity(1.0f);
		setSynced(true);

		new ParticleEntity(state, this, Particle.BRIGHT_TRAIL, 0, 0, true, SyncType.CREATESYNC)
				.setColor(HadalColor.GOLDEN_YELLOW);

		//make objective marker track this event
		state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_ALERT,true, false, false);

		//we must set this event's layer to make it render underneath players
		setLayer(PlayState.ObjectLayer.HBOX);
	}

	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (!state.isServer()) { return; }
				if (!captured) {
					if (fixB != null) {
						if (fixB instanceof PlayerBodyData playerData) {
							captured = true;
							awayFromSpawn = true;
							target = playerData.getPlayer();

							//a player captures the crown. Alert players.
							body.setGravityScale(0.0f);
							String playerName = WeaponUtils.getPlayerColorName(target, MAX_NAME_LENGTH);
							state.getKillFeed().addNotification(UIText.KM_PICKUP.text(playerName), true);
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

	private final Vector2 hbLocation = new Vector2();
	@Override
	public void controller(float delta) {
		//if the flag holder dies, the flag drops and will return after some time
		if (captured) {
			if (!target.isAlive()) {
				captured = false;
				body.setGravityScale(1.0f);
				returnTimer = RETURN_TIME;

				state.getKillFeed().addNotification(UIText.KM_DROPPED.text(), true);
			} else {
				hbLocation.set(target.getPosition());
				setTransform(hbLocation, getAngle());

				//periodically score when holding the flag
				timeCount += delta;
				if (timeCount >= 1.0f) {
					timeCount = 0;
					state.getMode().processPlayerScoreChange(state, target, 1);
					SoundEffect.COIN3.playUniversal(state, getPixelPosition(), 1.0f, false);
				}
			}
		} else if (awayFromSpawn) {
			returnTimer -= delta;
			if (returnTimer <= 0.0f) {
				queueDeletion();
				state.getKillFeed().addNotification(UIText.KM_RETURN.text(), true);
			}
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		//this makes flag following player less janky for clients when held
		if (captured) {
			if (target != null) {
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


	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreateCrown(entityID, getPixelPosition());
	}

	@Override
	public void onServerSync() {
		if (captured) {
			state.getSyncPackets().add(new PacketsSync.SyncFlagAttached(entityID, target.getEntityID(), getPosition(), getLinearVelocity(),
					entityAge, state.getTimer(), 0.0f));
		} else {
			state.getSyncPackets().add(new PacketsSync.SyncFlag(entityID, getPosition(), getLinearVelocity(),
					entityAge, state.getTimer(), 0.0f));
		}
	}

	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);

		if (o instanceof PacketsSync.SyncFlag) {
			if (o instanceof PacketsSync.SyncFlagAttached p1) {
				HadalEntity entity = state.findEntity(p1.uuidMSBAttached, p1.uuidLSBAttached);
				if (entity != null) {
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

	public boolean isCaptured() { return captured; }

	public Player getTarget() { return target; }
}
