package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.PlayerClientOnHost;
import com.mygdx.hadal.schmucks.entities.PlayerSelfOnClient;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;
import com.mygdx.hadal.utils.b2d.HadalFixture;

/**
 * A Sensor is an activating event that will activate a connected event when touching a specified type of body.
 * <p>
 * Triggered Behavior: N/A.
 * Triggering Behavior: When touching a specified type of body, this event will trigger its connected event.
 * <p>
 * Fields:
 * player: Boolean that describes whether this sensor touches player. Optional. Default: true
 * hbox: Boolean that describes whether this sensor touches hit-boxes. Optional. Default: false
 * event: Boolean that describes whether this sensor touches events. Optional. Default: false
 * enemy: Boolean that describes whether this sensor touches enemies. Optional. Default: false
 * gravity: float that determines the gravity of the object. Optional. Default: 0.0f. Currently only used for falling targets in NASU
 * collision: Do we add a collision hbox to this event? This is used on dynamically spawned pickups so they can have gravity while not passing through walls.
 * pickup: For pickup sensors (fuel/hp etc), clients should activate them for themselves
 * @author Melfeneydew Merpucacia
 */
public class Sensor extends Event {

    private final short filter;
    private final boolean collision;
    private final boolean pickup;
    private final float cooldown;
    private float cooldownCount;

    public Sensor(PlayState state, Vector2 startPos, Vector2 size, boolean player, boolean hbox, boolean event, boolean enemy,
                  float gravity, float cooldown, boolean collision, boolean pickup) {
        super(state, startPos, size);
        this.filter = (short) ((player ? BodyConstants.BIT_PLAYER : 0) | (hbox ? BodyConstants.BIT_PROJECTILE: 0) |
                (event ? BodyConstants.BIT_SENSOR : 0) | (enemy ? BodyConstants.BIT_ENEMY : 0));
        this.gravity = gravity;
        this.cooldown = cooldown;
        this.collision = collision;
        this.pickup = pickup;
    }

    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
                                       DamageSource source, DamageTag... tags) {

                //this event should receive no kb from attacks.
                return basedamage;
            }

            @Override
            public void onTouch(HadalData fixB) {
                super.onTouch(fixB);

                //cannot be activated multiple times during cooldown if set
                if (cooldownCount < cooldown) { return; }

                if (isAlive()) {
                    if (event.getConnectedEvent() != null) {
                        if (fixB instanceof PlayerBodyData playerData) {
                            if (pickup) {
                                if (state.isServer()) {
                                    if (!(playerData.getPlayer() instanceof PlayerClientOnHost)) {
                                        event.getConnectedEvent().getEventData().preActivate(this, playerData.getPlayer());
                                    }
                                } else {
                                    if (playerData.getPlayer() instanceof PlayerSelfOnClient) {
                                        event.getConnectedEvent().getEventData().preActivate(this, playerData.getPlayer());
                                    }
                                }
                            } else {
                                event.getConnectedEvent().getEventData().preActivate(this, playerData.getPlayer());
                            }
                        } else if (fixB instanceof HitboxData hboxData) {
                            if (hboxData.getHbox().getCreator().getBodyData() instanceof PlayerBodyData shooterData) {
                                event.getConnectedEvent().getEventData().preActivate(this, shooterData.getPlayer());
                            }
                        } else {
                            event.getConnectedEvent().getEventData().preActivate(this, null);
                        }
                        cooldownCount = 0.0f;
                        if (standardParticle != null) {
                            standardParticle.onForBurst(1.0f);
                        }
                    }
                }
            }
        };

        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, filter, (short) 0)
                .setBodyType(BodyDef.BodyType.KinematicBody)
                .setGravity(gravity)
                .addToWorld(world);

        if (collision) {
            new HadalFixture(new Vector2(), new Vector2(size).scl(2),
                    BodyConstants.BIT_SENSOR, BodyConstants.BIT_WALL, (short) 0)
                    .setSensor(false)
                    .setFriction(1.0f)
                    .addToBody(body);
        }
    }

    @Override
    public void controller(float delta) {
        super.controller(delta);
        cooldownCount += delta;
    }

    @Override
    public void clientController(float delta) {
        super.clientController(delta);
        cooldownCount += delta;
    }
}