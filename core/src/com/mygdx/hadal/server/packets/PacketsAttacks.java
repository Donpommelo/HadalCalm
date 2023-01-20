package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;

import java.util.UUID;

public class PacketsAttacks {

    public static class SyncedAttackSingle {
        public Vector2 pos, velo;
        public SyncedAttack attack;

        public SyncedAttackSingle() {}

        /**
         * A SyncedAttackSingle is sent from server to client to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param pos: The starting position of the hbox this attack will create
         * @param velo: The starting velocity/trajectory of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SyncedAttackSingle(Vector2 pos, Vector2 velo, SyncedAttack attack) {
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class SyncedAttackSingleServer {
        public long uuidMSB, uuidLSB;
        public long uuidMSBCreator, uuidLSBCreator;
        public Vector2 pos, velo;
        public SyncedAttack attack;

        public SyncedAttackSingleServer() {}

        /**
         * A SyncedAttackSingle is sent from server to client to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param entityID: The entityID of the hitbox this attack will create
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting position of the hbox this attack will create
         * @param velo: The starting velocity/trajectory of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SyncedAttackSingleServer(UUID entityID, UUID creatorID, Vector2 pos, Vector2 velo, SyncedAttack attack) {
            this.uuidLSB = entityID.getLeastSignificantBits();
            this.uuidMSB = entityID.getMostSignificantBits();
            this.uuidLSBCreator = creatorID.getLeastSignificantBits();
            this.uuidMSBCreator = creatorID.getMostSignificantBits();
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class SyncedAttackSingleExtra extends SyncedAttackSingle {
        public float[] extraFields;

        public SyncedAttackSingleExtra() {}

        /**
         * A SyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SyncedAttackSingleExtra(Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
            super(pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SyncedAttackSingleExtraServer extends SyncedAttackSingleServer {
        public float[] extraFields;

        public SyncedAttackSingleExtraServer() {}

        /**
         * A SyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SyncedAttackSingleExtraServer(UUID entityID, UUID creatorID, Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
            super(entityID, creatorID, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SyncedAttackMulti {
        public Vector2 weaponVelo;
        public Vector2[] pos, velo;
        public SyncedAttack attack;

        public SyncedAttackMulti() {}

        /**
         * A SyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
         * hitboxes like the flounderbuss or party popper.
         * @param pos: The starting positions of the hboxes this attack will create
         * @param velo: The starting velocities/trajectories of the hboxes this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SyncedAttackMulti(Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
            this.weaponVelo = weaponVelo;
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class SyncedAttackMultiServer {
        public long[] uuidMSB, uuidLSB;
        public long uuidMSBCreator, uuidLSBCreator;
        public Vector2 weaponVelo;
        public Vector2[] pos, velo;
        public SyncedAttack attack;

        public SyncedAttackMultiServer() {}

        /**
         * A SyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
         * hitboxes like the flounderbuss or party popper.
         * @param entityID: The entityID of the hitbox this attack will create
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting positions of the hboxes this attack will create
         * @param velo: The starting velocities/trajectories of the hboxes this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SyncedAttackMultiServer(UUID[] entityID, UUID creatorID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
            this.uuidLSB = new long[entityID.length];
            this.uuidMSB = new long[entityID.length];
            for (int i = 0; i < entityID.length; i++) {
                uuidLSB[i] = entityID[i].getLeastSignificantBits();
                uuidMSB[i] = entityID[i].getMostSignificantBits();
            }
            this.uuidLSBCreator = creatorID.getLeastSignificantBits();
            this.uuidMSBCreator = creatorID.getMostSignificantBits();
            this.weaponVelo = weaponVelo;
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class SyncedAttackMultiExtra extends SyncedAttackMulti {
        public float[] extraFields;

        public SyncedAttackMultiExtra() {}

        /**
         * A SyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
         * so thee client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SyncedAttackMultiExtra(Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
            super(weaponVelo, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SyncedAttackMultiExtraServer extends SyncedAttackMultiServer {
        public float[] extraFields;

        public SyncedAttackMultiExtraServer() {}

        /**
         * A SyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
         * so thee client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SyncedAttackMultiExtraServer(UUID[] entityID, UUID creatorID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
            super(entityID, creatorID, weaponVelo, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }
}
