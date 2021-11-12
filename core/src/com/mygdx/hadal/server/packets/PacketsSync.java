package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.schmucks.MoveState;

public class PacketsSync {

    public static class SyncEntity {
        public String entityID;
        public Vector2 pos;
        public Vector2 velocity;
        public float age;
        public float timestamp;
        public SyncEntity() {}

        /**
         * A SyncEntity is sent from the Server to the Client for every synchronized entity every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of the entity.
         * Most basic version just transforms the entity's body.
         *
         * @param entityID: ID of the entity to synchronize
         * @param pos: position of the entity
         * @param velocity: linear velocity of the entity
         * @param age: age of the entity. (used by client to determine if they missed a packet)
         * @param timestamp: time of sync. Used for client prediction.
         */
        public SyncEntity(String entityID, Vector2 pos, Vector2 velocity, float age, float timestamp) {
            this.entityID = entityID;
            this.pos = pos;
            this.velocity = velocity;
            this.age = age;
            this.timestamp = timestamp;
        }
    }

    public static class SyncEntityAngled extends SyncEntity {
        public float angle;
        public SyncEntityAngled() {}

        /**
         * A SyncEntity is sent from the Server to the Client for every synchronized entity every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of the entity.
         * Most basic version just transforms the entity's body.
         *
         * @param entityID: ID of the entity to synchronize
         * @param pos: position of the entity
         * @param velocity: linear velocity of the entity
         * @param age: age of the entity. (used by client to determine if they missed a packet)
         * @param timestamp: time of sync. Used for client prediction.
         */
        public SyncEntityAngled(String entityID, Vector2 pos, Vector2 velocity, float age, float timestamp, float angle) {
            super(entityID, pos, velocity, age, timestamp);
            this.angle = angle;
        }
    }

    public static class SyncSchmuck extends SyncEntity {
        public MoveState moveState;
        public float hpPercent;

        public SyncSchmuck() {}

        /**
         * A SyncSchmuck is sent from the Server to the Client for every synchronized schmuck every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of the schmuck.
         * This adjusts the Schmuck's stats and visual information
         *
         * @param moveState: The State of the Schmuck. Used for animations on the Client's end
         * @param hpPercent: The percent of remaining hp this schmuck has.
         */
        public SyncSchmuck(String entityID, Vector2 pos, Vector2 velocity, float age, float timestamp,
                           MoveState moveState, float hpPercent) {
            super(entityID, pos, velocity, age, timestamp);
            this.moveState = moveState;
            this.hpPercent = hpPercent;
        }
    }

    public static class SyncSchmuckAngled extends SyncSchmuck {
        public float angle;

        public SyncSchmuckAngled() {}

        public SyncSchmuckAngled(String entityID, Vector2 pos, Vector2 velocity, float age, float timestamp,
                                 MoveState moveState, float hpPercent, float angle) {
            super(entityID, pos, velocity, age, timestamp, moveState, hpPercent);
            this.angle = angle;
        }
    }

    public static class SyncPlayer extends SyncSchmuck {
        public Vector2 attackAngle;
        public boolean grounded;
        public int currentSlot;
        public boolean reloading;
        public float reloadPercent;
        public boolean charging;
        public float chargePercent;
        public boolean outOfAmmo;
        public int invisible;

        public SyncPlayer() {}

        /**
         * A SyncPlayerAll is sent from the Server to the Client for every synchronized Player every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of each Player.
         * This long list of fields is just the Player-specific information needed for Clients to properly render other players.
         */
        public SyncPlayer(String entityID, Vector2 pos, Vector2 velocity, float age, float timestamp, MoveState moveState,
                          float hpPercent, Vector2 attackAngle, Boolean grounded, int currentSlot, boolean reloading,
                          float reloadPercent, boolean charging, float chargePercent, boolean outOfAmmo, int invisible) {
            super(entityID, pos, velocity, age, timestamp, moveState, hpPercent);
            this.attackAngle = attackAngle;
            this.grounded = grounded;
            this.currentSlot = currentSlot;
            this.reloading = reloading;
            this.reloadPercent = reloadPercent;
            this.charging = charging;
            this.chargePercent = chargePercent;
            this.outOfAmmo = outOfAmmo;
            this.invisible = invisible;
        }
    }

    public static class SyncPlayerSelf extends SyncPlayer {
        public float fuelPercent;
        public int currentClip;
        public int currentAmmo;
        public float activeCharge;
        public float blinded;

        public SyncPlayerSelf() {}

        /**
         * A SyncPlayerSelf is sent from the Server to the Client every engine tick.
         * This packet (and similar packets) just tells the client how to change their own Player for their purpose of their own ui.
         * @param fuelPercent: The client player's current fuel amount.
         * @param currentClip: The client player's current clip amount.
         * @param currentAmmo: The client player's current ammo amount.
         * @param activeCharge: The client player's current active item charge amount.
         */
        public SyncPlayerSelf(String entityID, Vector2 pos, Vector2 velocity, float age, float timestamp, MoveState moveState,
                          float hpPercent, Vector2 attackAngle, Boolean grounded, int currentSlot, boolean reloading,
                          float reloadPercent, boolean charging, float chargePercent, boolean outOfAmmo, int invisible,
                          float fuelPercent, int currentClip, int currentAmmo, float activeCharge, float blinded) {
            super(entityID, pos, velocity, age, timestamp, moveState, hpPercent, attackAngle, grounded, currentSlot, reloading,
                    reloadPercent, charging, chargePercent, outOfAmmo, invisible);
            this.fuelPercent = fuelPercent;
            this.currentClip = currentClip;
            this.currentAmmo = currentAmmo;
            this.activeCharge = activeCharge;
            this.blinded = blinded;
        }
    }

    public static class SyncParticles extends SyncEntity {
        public boolean on;
        public SyncParticles() {}

        /**
         * A SyncParticles is sent from the Server to the Client every engine tick for every ParticleEntity of the TICKSYNC type.
         * Particles of this nature are dynamically turned on and off in the Server, thus needing this packet.
         *
         * @param entityID: ID of the Particle Effect to turn on/off
         * @param pos: position of the synced particle effect
         * @param offset: if connected to another entity, this is the offset from that entity's position
         * @param on: Is the Server's version of this effect on or off?
         * @param age: age of the entity. (used by client to determine if they missed a packet)
         * @param timestamp: time of sync. Used for client prediction.
         */
        public SyncParticles(String entityID, Vector2 pos, Vector2 offset, float age, float timestamp, boolean on) {
            super(entityID, pos, offset, age, timestamp);
            this.on = on;
        }
    }

    public static class SyncParticlesExtra extends SyncParticles {
        public float scale;
        public Vector3 color;

        public SyncParticlesExtra() {}

        /**
         * This sync packet is used for particles that sync the extra fields; color and scale.
         */
        public SyncParticlesExtra(String entityID, Vector2 pos, Vector2 offset, float age, float timestamp, boolean on, float scale, Vector3 color) {
            super(entityID, pos, offset, age, timestamp, on);
            this.scale = scale;
            this.color = color;
        }
    }
}