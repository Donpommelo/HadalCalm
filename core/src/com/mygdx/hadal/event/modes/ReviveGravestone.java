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
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.states.PlayState.defaultFadeOutSpeed;
import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 */
public class ReviveGravestone extends Event {

	private static final Vector2 graveSize = new Vector2(80, 80);
	private final static float particleDuration = 5.0f;

	//this is the player that this event is fixed to and the last player that held it
	private final User user;
	private final int connID;
	private final float returnMaxTimer;
	private final Event defaultStartPoint;

	private float returnTimer;
	private boolean positionReset;

	//textures that indicate how long until the flag is returned to base
	private final TextureRegion returnMeter, returnBar;
	private float returnPercent, returnDelayed;

	//amount of players currently nearby their dropped flag to speed up its return
	private int numReturning;
	private static final float checkRadius = 5.0f;

	public ReviveGravestone(PlayState state, Vector2 startPos, User user, int connID, float returnMaxTimer,
							Event defaultStartPoint) {
		super(state, startPos, graveSize);
		this.user = user;
		this.connID = connID;
		this.returnMaxTimer = returnMaxTimer;
		this.returnDelayed = returnMaxTimer;
		this.defaultStartPoint = defaultStartPoint;

		this.returnTimer = returnMaxTimer;

		setEventSprite(Sprite.DIATOM_A);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setGravity(1.0f);
		setSynced(true);

		//set flag's color according to team alignment
		HadalColor color = user.getTeamFilter().getColor1();
		new ParticleEntity(state, this, Particle.BRIGHT_TRAIL, 0, 0, true, SyncType.CREATESYNC)
				.setScale(1.8f).setColor(color);

		//make objective marker track this event
		EventUtils.setObjectiveMarkerTeam(state, this, Sprite.CLEAR_CIRCLE_ALERT, color,
				true, false, user.getTeamFilter());

		if (state.isSpectatorMode()) {
			state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_ALERT, color, true, false);
		} else if (state.getPlayer() != null) {
			if (state.getPlayer().getUser() != null) {
				if (state.getPlayer().getUser().getTeamFilter() == user.getTeamFilter()) {
					state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_ALERT, color, true, false);
				}
			}
		}

		this.returnMeter = Sprite.UI_RELOAD_METER.getFrame();
		this.returnBar = Sprite.UI_RELOAD_BAR.getFrame();

		//we must set this event's layer to make it render underneath players
		setLayer(PlayState.ObjectLayer.HBOX);
	}

	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1.0f, 1.0f, 0, false, true,
				Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0, false, eventData);

		//feetdata is set to make the grave selectively pass through dropthrough platforms
		EventUtils.addFeetFixture(this);

		FixtureBuilder.createFixtureDef(body, new Vector2(), new Vector2(size), true, 0, 0, 0.0f, 1.0f,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_SENSOR), (short) 0).setUserData(eventData);
	}

	private final Vector2 hbLocation = new Vector2();
	private float controllerCount;
	private static final float checkInterval = 0.2f;
	private Player lastReviver;
	@Override
	public void controller(float delta) {

		if (positionReset && defaultStartPoint != null) {
			positionReset = false;
			setTransform(defaultStartPoint.getPosition(), getAngle());
		}

		//return time decrementing scales to number of players nearby
		returnTimer -= delta * numReturningToSpeed(numReturning);

		if (returnTimer > returnMaxTimer) {
			returnTimer = returnMaxTimer;
		}

		if (returnTimer <= 0.0f) {
			new ParticleEntity(state, getPixelPosition(), Particle.DIATOM_IMPACT_LARGE,	particleDuration, true, SyncType.CREATESYNC)
					.setColor(user.getTeamFilter().getColor1());

			queueDeletion();

			if (HadalGame.server.getUsers().containsValue(user, true)) {

				String playerName = WeaponUtils.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH);

				if (lastReviver != null && numReturning > 0) {
					String reviverName = WeaponUtils.getPlayerColorName(lastReviver, MAX_NAME_LENGTH);
					state.getKillFeed().addNotification(HText.GRAVE_REVIVER.text(playerName, reviverName), true);
				} else {
					state.getKillFeed().addNotification(HText.GRAVE_REVIVE.text(playerName), true);
				}

				user.setOverrideSpawn(getPixelPosition());
				user.setOverrideStart(defaultStartPoint);
				user.beginTransition(state, PlayState.TransitionState.RESPAWN, false, defaultFadeOutSpeed, 1.0f);
			}
		}

		//check nearby area for allied players and set return percent
		controllerCount += delta;
		if (controllerCount >= checkInterval) {
			controllerCount = 0.0f;
			hbLocation.set(getPosition());
			numReturning = 0;
			state.getWorld().QueryAABB(fixture -> {
						if (fixture.getUserData() instanceof PlayerBodyData playerData) {
							if (playerData.getPlayer().getHitboxfilter() == user.getPlayer().getHitboxfilter()) {
								lastReviver = playerData.getPlayer();
								numReturning++;
							}
						}
						return true;
					},
					hbLocation.x - checkRadius, hbLocation.y - checkRadius,
					hbLocation.x + checkRadius, hbLocation.y + checkRadius);
		}
		returnPercent = (returnMaxTimer - returnTimer) / returnMaxTimer;
	}

	private static final float uiScale = 0.4f;
	private final Vector2 flagLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);

		//draw revive meter according to timer
		returnDelayed = Math.min(1.0f, returnDelayed + (returnPercent - returnDelayed) * 0.25f);

		flagLocation.set(getPixelPosition());
		float textX = flagLocation.x - returnMeter.getRegionWidth() * uiScale / 2;
		float textY = flagLocation.y + returnMeter.getRegionHeight() * uiScale + size.y / 2;

		batch.draw(returnBar, textX + 10, textY + 4, returnBar.getRegionWidth() * uiScale * returnDelayed, returnBar.getRegionHeight() * uiScale);
		HadalGame.FONT_SPRITE.draw(batch, user.getScores().getNameShort(), textX + 12, textY + returnMeter.getRegionHeight() * uiScale);
		batch.draw(returnMeter, textX, textY, returnMeter.getRegionWidth() * uiScale, returnMeter.getRegionHeight() * uiScale);
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreateGrave(entityID, connID, getPixelPosition(), returnMaxTimer);
	}

	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncFlag(entityID, getPosition(), getLinearVelocity(),
				entityAge, state.getTimer(), returnPercent));
	}

	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);

		if (o instanceof PacketsSync.SyncFlag p) {
			returnPercent = p.returnPercent;
		}
	}

	public void resetPosition() {
		positionReset = true;
	}

	public AlignmentFilter getGraveTeam() { return user.getTeamFilter(); }

	/**
	 * Get speed of flag returning for number of players returning it
	 */
	private float numReturningToSpeed(int numReturning) {
		switch (numReturning) {
			case 0 -> {
				return -0.25f;
			}
			case 1 -> {
				return 1.0f;
			}
			case 2 -> {
				return 2.0f;
			}
			case 3 -> {
				return 4.0f;
			}
			default -> {
				return 8.0f;
			}
		}
	}
}