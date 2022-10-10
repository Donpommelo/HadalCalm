package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.constants.MoveState;

import java.util.UUID;

/**
 * This contains various entity sync packets.
 * These are sent by each entity from server to client regularly to give information like position
 */
public class PacketsSync {

    public static class SyncEntity {
        public long uuidMSB, uuidLSB;
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
        public SyncEntity(UUID entityID, Vector2 pos, Vector2 velocity, float age, float timestamp) {
            this.uuidLSB = entityID.getLeastSignificantBits();
            this.uuidMSB = entityID.getMostSignificantBits();
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
         * This is a SyncEntity packet that also specifies the angle of the entity's body.
         * This is a separate packet to avoid sending that unnecessary data for most entities
         *
         * @param angle: angle of the entity's body.
         */
        public SyncEntityAngled(UUID entityID, Vector2 pos, Vector2 velocity, float age, float timestamp, float angle) {
            super(entityID, pos, velocity, age, timestamp);
            this.angle = angle;
        }
    }

    public static class SyncSchmuck extends SyncEntity {
        public MoveState moveState;
        public float currentHp;

        public SyncSchmuck() {}

        /**
         * A SyncSchmuck is sent from the Server to the Client for every synchronized schmuck every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of the schmuck.
         * This adjusts the Schmuck's stats and visual information
         *
         * @param moveState: The State of the Schmuck. Used for animations on the Client's end
         * @param currentHp: The amount of remaining hp this schmuck has.
         */
        public SyncSchmuck(UUID entityID, Vector2 pos, Vector2 velocity, float age, float timestamp,
                           MoveState moveState, float currentHp) {
            super(entityID, pos, velocity, age, timestamp);
            this.moveState = moveState;
            this.currentHp = currentHp;
        }
    }

    public static class SyncSchmuckAngled extends SyncSchmuck {
        public float angle;

        public SyncSchmuckAngled() {}

        /**
         * This is a SyncSchmuck packet that also specifies the angle of the entity's body.
         * This is used for rotating schmucks like swimming enemies
         *
         * @param angle: angle of the entity's body.
         */
        public SyncSchmuckAngled(UUID entityID, Vector2 pos, Vector2 velocity, float age, float timestamp,
                                 MoveState moveState, float currentHp, float angle) {
            super(entityID, pos, velocity, age, timestamp, moveState, currentHp);
            this.angle = angle;
        }
    }

    public static class SyncPlayer extends SyncSchmuck {
        public Vector2 attackAngle;
        public int currentSlot;
        public float reloadPercent;
        public float chargePercent;
        public float currentFuel;
        public int currentClip;
        public int currentAmmo;
        public float activeCharge;
        public short maskBits;
        public float blinded;
        public short statusCode;

        public SyncPlayer() {}

        /**
         * A SyncPlayerAll is sent from the Server to the Client for every synchronized Player every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of each Player.
         * This long list of fields is just the Player-specific information needed for Clients to properly render other players.
         */
        public SyncPlayer(UUID entityID, Vector2 pos, Vector2 velocity, float age, float timestamp, MoveState moveState,
                          float currentHp, Vector2 attackAngle, int currentSlot, float reloadPercent,
                          float chargePercent, float currentFuel, int currentClip, int currentAmmo, float activeCharge,
                          short maskBits, float blinded, short statusCode) {
            super(entityID, pos, velocity, age, timestamp, moveState, currentHp);
            this.attackAngle = attackAngle;
            this.currentSlot = currentSlot;
            this.reloadPercent = reloadPercent;
            this.chargePercent = chargePercent;
            this.currentFuel = currentFuel;
            this.currentClip = currentClip;
            this.currentAmmo = currentAmmo;
            this.activeCharge = activeCharge;
            this.maskBits = maskBits;
            this.blinded = blinded;
            this.statusCode = statusCode;
        }
    }

    public static class SyncParticles extends SyncEntity {
        public boolean on;
        public SyncParticles() {}

        /**
         * A SyncParticles is sent from the Server to the Client every engine tick for every ParticleEntity of the TICKSYNC type.
         * Particles of this nature are dynamically turned on and off in the Server, thus needing this packet.
         *
         * @param on: Is the Server's version of this effect on or off?
         */
        public SyncParticles(UUID entityID, Vector2 pos, Vector2 offset, float age, float timestamp, boolean on) {
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
         * @param scale: size modification of the particle
         * @param color: if the particle is not using default colors, this is its rgb
         */
        public SyncParticlesExtra(UUID entityID, Vector2 pos, Vector2 offset, float age, float timestamp, boolean on, float scale, Vector3 color) {
            super(entityID, pos, offset, age, timestamp, on);
            this.scale = scale;
            this.color = color;
        }
    }

    public static class SyncFlag extends SyncEntity {
        public float returnPercent;

        public SyncFlag() {}

        /**
         * This sync packet is used for the flag event in ctf mode to sync its return timer
         * @param returnPercent: return timer percent
         */
        public SyncFlag(UUID entityID, Vector2 pos, Vector2 velocity, float age, float timestamp, float returnPercent) {
            super(entityID, pos, velocity, age, timestamp);
            this.returnPercent = returnPercent;
        }
    }

    public static class SyncFlagAttached extends SyncFlag {
        public long uuidMSBAttached, uuidLSBAttached;

        public SyncFlagAttached() {}

        /**
         * This sync packet is used for the flag event in ctf mode when flag is picked up
         * @param attachedID: entityID of the player that is carrying the flag
         */
        public SyncFlagAttached(UUID entityID, UUID attachedID, Vector2 pos, Vector2 velocity, float age, float timestamp, float returnPercent) {
            super(entityID, pos, velocity, age, timestamp, returnPercent);
            this.uuidLSBAttached = attachedID.getLeastSignificantBits();
            this.uuidMSBAttached = attachedID.getMostSignificantBits();
        }
    }
}
