package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.utils.PacketUtil;

/**
 * This contains various entity sync packets.
 * These are sent by each entity from server to client regularly to give information like position
 */
public class PacketsSync {

    public static class SyncEntity {
        public int entityID;
        public short posX, posY;
        public short veloX, veloY;
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
         * @param timestamp: time of sync. Used for client prediction.
         */
        public SyncEntity(int entityID, Vector2 pos, Vector2 velocity, float timestamp) {
            this(entityID, PacketUtil.floatToShort(pos.x), PacketUtil.floatToShort(pos.y),
                    PacketUtil.floatToShort(velocity.x), PacketUtil.floatToShort(velocity.y), timestamp);
        }

        public SyncEntity(int entityID, short posX, short posY, short veloX, short veloY, float timestamp) {
            this.entityID = entityID;
            this.posX = posX;
            this.posY = posY;
            this.veloX = veloX;
            this.veloY = veloY;
            this.timestamp = timestamp;
        }
    }

    public static class SyncEntityAngled extends SyncEntity {
        public byte angle;
        public SyncEntityAngled() {}

        /**
         * This is a SyncEntity packet that also specifies the angle of the entity's body.
         * This is a separate packet to avoid sending that unnecessary data for most entities
         *
         * @param angle: angle of the entity's body.
         */
        public SyncEntityAngled(int entityID, Vector2 pos, Vector2 velocity, float timestamp, float angle) {
            super(entityID, pos, velocity, timestamp);
            this.angle = PacketUtil.radianAngleToByte(angle);
        }
    }

    public static class SyncSchmuck extends SyncEntity {
        public MoveState moveState;
        public byte hpPercent;

        public SyncSchmuck() {}

        /**
         * A SyncSchmuck is sent from the Server to the Client for every synchronized schmuck every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of the schmuck.
         * This adjusts the Schmuck's stats and visual information
         *
         * @param moveState: The State of the Schmuck. Used for animations on the Client's end
         * @param hpPercent: The amount of remaining hp this schmuck has.
         */
        public SyncSchmuck(int entityID, Vector2 pos, Vector2 velocity, float timestamp, MoveState moveState, byte hpPercent) {
            super(entityID, pos, velocity, timestamp);
            this.moveState = moveState;
            this.hpPercent = hpPercent;
        }

        public SyncSchmuck(int entityID, short posX, short posY, short veloX, short veloY, float timestamp,
                           MoveState moveState, byte hpPercent) {
            super(entityID, posX, posY, veloX, veloY, timestamp);
            this.moveState = moveState;
            this.hpPercent = hpPercent;
        }
    }

    public static class SyncSchmuckAngled extends SyncSchmuck {
        public byte angle;

        public SyncSchmuckAngled() {}

        /**
         * This is a SyncSchmuck packet that also specifies the angle of the entity's body.
         * This is used for rotating schmucks like swimming enemies
         *
         * @param angle: angle of the entity's body.
         */
        public SyncSchmuckAngled(int entityID, Vector2 pos, Vector2 velocity, float timestamp,
                                 MoveState moveState, byte hpPercent, float angle) {
            super(entityID, pos, velocity, timestamp, moveState, hpPercent);
            this.angle = PacketUtil.radianAngleToByte(angle);
        }
    }

    public static class SyncSchmuckColor extends SyncSchmuck {
        public float desiredScale;
        public HadalColor color;

        public SyncSchmuckColor() {}

        /**
         */
        public SyncSchmuckColor(int entityID, Vector2 pos, Vector2 velocity, float timestamp,
                                MoveState moveState, byte hpPercent, float desiredScale, HadalColor color) {
            super(entityID, pos, velocity, timestamp, moveState, hpPercent);
            this.desiredScale = desiredScale;
            this.color = color;
        }
    }

    public static class SyncPlayerSnapshot extends SyncClientSnapshot {
        public byte connID;

        public SyncPlayerSnapshot() {}

        /**
         * A SyncPlayerAll is sent from the Server to the Client for every synchronized Player every engine tick.
         * This packet (and similar packets) just tell the client how to change their version of each Player.
         * This long list of fields is just the Player-specific information needed for Clients to properly render other players.
         */
        public SyncPlayerSnapshot(byte connID, Vector2 pos, Vector2 velocity, Vector2 mousePosition, float timestamp, MoveState moveState,
                          byte hpPercent, byte fuelPercent, byte currentSlot, byte reloadPercent,
                          byte chargePercent, short conditionCode) {
            super(pos, velocity, mousePosition, timestamp, moveState, hpPercent, fuelPercent, currentSlot, reloadPercent,
            chargePercent, conditionCode);
            this.connID = connID;
        }

        public SyncPlayerSnapshot(byte connID, short posX, short posY, short veloX, short veloY, short mouseX, short mouseY,
                                  float timestamp, MoveState moveState, byte hpPercent, byte fuelPercent, byte currentSlot,
                                  byte reloadPercent, byte chargePercent, short conditionCode) {
            super(posX, posY, veloX, veloY, mouseX, mouseY, timestamp, moveState, hpPercent, fuelPercent, currentSlot, reloadPercent,
                    chargePercent, conditionCode);
            this.connID = connID;
        }
    }

    public static class SyncClientSnapshot {
        public byte hpPercent;
        public byte fuelPercent;
        public byte currentSlot;
        public byte reloadPercent;
        public byte chargePercent;
        public short conditionCode;
        public short posX, posY;
        public short veloX, veloY;
        public short mouseX, mouseY;
        public float timestamp;
        public MoveState moveState;

        public SyncClientSnapshot() {}

        /**
         * A SyncClientSnapshot is like a SyncPlayerSnapshot except sent from client to the server to sync their own player
         */
        public SyncClientSnapshot(short posX, short posY, short veloX, short veloY, short mouseX, short mouseY,
                                  float timestamp, MoveState moveState, byte hpPercent, byte fuelPercent, byte currentSlot,
                                  byte reloadPercent, byte chargePercent, short conditionCode) {
            this.posX = posX;
            this.posY = posY;
            this.veloX = veloX;
            this.veloY = veloY;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.timestamp = timestamp;
            this.moveState = moveState;
            this.hpPercent = hpPercent;
            this.fuelPercent = fuelPercent;
            this.currentSlot = currentSlot;
            this.reloadPercent = reloadPercent;
            this.chargePercent = chargePercent;
            this.conditionCode = conditionCode;
        }

        public SyncClientSnapshot(Vector2 pos, Vector2 velocity, Vector2 mousePosition, float timestamp, MoveState moveState,
                                  byte hpPercent, byte fuelPercent, byte currentSlot, byte reloadPercent,
                                  byte chargePercent, short conditionCode) {
            this(PacketUtil.floatToShort(pos.x), PacketUtil.floatToShort(pos.y), PacketUtil.floatToShort(velocity.x), PacketUtil.floatToShort(velocity.y),
                    PacketUtil.floatToShort(mousePosition.x), PacketUtil.floatToShort(mousePosition.y), timestamp, moveState,
                    hpPercent, fuelPercent, currentSlot, reloadPercent, chargePercent, conditionCode);
        }
    }

    public static class SyncFlag extends SyncEntity {
        public byte returnPercent;

        public SyncFlag() {}

        /**
         * This sync packet is used for the flag event in ctf mode to sync its return timer
         * @param returnPercent: return timer percent
         */
        public SyncFlag(int entityID, Vector2 pos, Vector2 velocity, float timestamp, byte returnPercent) {
            super(entityID, pos, velocity, timestamp);
            this.returnPercent = returnPercent;
        }
    }

    public static class SyncFlagAttached extends SyncFlag {
        public int attachedID;

        public SyncFlagAttached() {}

        /**
         * This sync packet is used for the flag event in ctf mode when flag is picked up
         * @param attachedID: entityID of the player that is carrying the flag
         */
        public SyncFlagAttached(int entityID, int attachedID, Vector2 pos, Vector2 velocity, float timestamp, byte returnPercent) {
            super(entityID, pos, velocity, timestamp, returnPercent);
            this.attachedID = attachedID;
        }
    }
}
