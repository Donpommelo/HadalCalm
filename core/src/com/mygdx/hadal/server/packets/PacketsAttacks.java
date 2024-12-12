package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;

public class PacketsAttacks {

    public static class SingleClientIndependent {
        public Vector2 pos, velo;
        public SyncedAttack attack;

        public SingleClientIndependent() {}

        /**
         * A SyncedAttackSingle is sent from client to server to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param pos: The starting position of the hbox this attack will create
         * @param velo: The starting velocity/trajectory of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SingleClientIndependent(Vector2 pos, Vector2 velo, SyncedAttack attack) {
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class SingleClientDependent extends SingleClientIndependent {
        public int entityID;

        public SingleClientDependent() {}

        /**
         * A SyncedAttackSingle is sent from client to server to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param entityID: Dependent attacks must link UUID between client and server for syncing purposes
         * @param pos: The starting position of the hbox this attack will create
         * @param velo: The starting velocity/trajectory of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SingleClientDependent(int entityID, Vector2 pos, Vector2 velo, SyncedAttack attack) {
            super(pos, velo, attack);
            this.entityID = entityID;
        }
    }

    public static class SingleServerIndependent {
        public int creatorID;
        public Vector2 pos, velo;
        public SyncedAttack attack;

        public SingleServerIndependent() {}

        /**
         * A SyncedAttackSingle is sent from server to client to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting position of the hbox this attack will create
         * @param velo: The starting velocity/trajectory of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SingleServerIndependent(int creatorID, Vector2 pos, Vector2 velo, SyncedAttack attack) {
            this.creatorID = creatorID;
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class SingleServerDependent extends SingleServerIndependent {
        public int entityID;

        public SingleServerDependent() {}

        /**
         * A SyncedAttackSingle is sent from server to client to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param entityID: The entityID of the hitbox this attack will create
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting position of the hbox this attack will create
         * @param velo: The starting velocity/trajectory of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SingleServerDependent(int entityID, int creatorID, Vector2 pos, Vector2 velo, SyncedAttack attack) {
            super(creatorID, pos, velo, attack);
            this.entityID = entityID;
        }
    }

    public static class SingleClientIndependentExtra extends SingleClientIndependent {
        public float[] extraFields;

        public SingleClientIndependentExtra() {}

        /**
         * A SyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SingleClientIndependentExtra(Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
            super(pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SingleClientDependentExtra extends SingleClientDependent {
        public float[] extraFields;

        public SingleClientDependentExtra() {}

        /**
         * A SyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SingleClientDependentExtra(int entityID, Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
            super(entityID, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SingleServerIndependentExtra extends SingleServerIndependent {
        public float[] extraFields;

        public SingleServerIndependentExtra() {}

        /**
         * A SyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SingleServerIndependentExtra(int creatorID, Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
            super(creatorID, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SingleServerDependentExtra extends SingleServerDependent {
        public float[] extraFields;

        public SingleServerDependentExtra() {}

        /**
         * A SyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SingleServerDependentExtra(int entityID, int creatorID, Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
            super(entityID, creatorID, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class MultiClientIndependent {
        public Vector2 weaponVelo;
        public Vector2[] pos, velo;
        public SyncedAttack attack;

        public MultiClientIndependent() {}

        /**
         * A SyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
         * hitboxes like the flounderbuss or party popper.
         * @param pos: The starting positions of the hboxes this attack will create
         * @param velo: The starting velocities/trajectories of the hboxes this attack will create
         * @param attack: the type of attack that is being executed
         */
        public MultiClientIndependent(Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
            this.weaponVelo = weaponVelo;
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class MultiClientDependent extends MultiClientIndependent {
        public int[] entityID;

        public MultiClientDependent() {}

        /**
         * A SyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
         * hitboxes like the flounderbuss or party popper.
         * @param pos: The starting positions of the hboxes this attack will create
         * @param velo: The starting velocities/trajectories of the hboxes this attack will create
         * @param attack: the type of attack that is being executed
         */
        public MultiClientDependent(int[] entityID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
            super(weaponVelo, pos, velo, attack);
            this.entityID = new int[entityID.length];
            System.arraycopy(entityID, 0, this.entityID, 0, entityID.length);
        }
    }

    public static class MultiServerIndependent {
        public int creatorID;
        public Vector2 weaponVelo;
        public Vector2[] pos, velo;
        public SyncedAttack attack;

        public MultiServerIndependent() {}

        /**
         * A SyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
         * hitboxes like the flounderbuss or party popper.
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting positions of the hboxes this attack will create
         * @param velo: The starting velocities/trajectories of the hboxes this attack will create
         * @param attack: the type of attack that is being executed
         */
        public MultiServerIndependent(int creatorID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
            this.creatorID = creatorID;
            this.weaponVelo = weaponVelo;
            this.pos = pos;
            this.velo = velo;
            this.attack = attack;
        }
    }

    public static class MultiServerDependent extends MultiServerIndependent {
        public int[] entityID;

        public MultiServerDependent() {}

        /**
         * A SyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
         * hitboxes like the flounderbuss or party popper.
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting positions of the hboxes this attack will create
         * @param velo: The starting velocities/trajectories of the hboxes this attack will create
         * @param attack: the type of attack that is being executed
         */
        public MultiServerDependent(int[] entityID, int creatorID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
            super(creatorID, weaponVelo, pos, velo, attack);
            this.entityID = new int[entityID.length];
            System.arraycopy(entityID, 0, this.entityID, 0, entityID.length);
        }
    }

    public static class MultiClientIndependentExtra extends MultiClientIndependent {
        public float[] extraFields;

        public MultiClientIndependentExtra() {}

        /**
         * A SyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
         * so thee client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public MultiClientIndependentExtra(Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
            super(weaponVelo, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class MultiClientDependentExtra extends MultiClientDependent {
        public float[] extraFields;

        public MultiClientDependentExtra() {}

        /**
         * A SyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
         * so thee client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public MultiClientDependentExtra(int[] entityID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
            super(entityID, weaponVelo, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class MultiServerIndependentExtra extends MultiServerIndependent {
        public float[] extraFields;

        public MultiServerIndependentExtra() {}

        /**
         * A SyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
         * so thee client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public MultiServerIndependentExtra(int creatorID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
            super(creatorID, weaponVelo, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class MultiServerDependentExtra extends MultiServerDependent {
        public float[] extraFields;

        public MultiServerDependentExtra() {}

        /**
         * A SyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
         * so thee client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public MultiServerDependentExtra(int[] entityID, int creatorID, Vector2 weaponVelo, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
            super(entityID, creatorID, weaponVelo, pos, velo, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SyncedAttackNoHbox {
        public Vector2 pos;
        public boolean independent;
        public SyncedAttack attack;

        public SyncedAttackNoHbox() {}

        /**
         * A SyncedAttackNoHbox is sent from server to client to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param attack: the type of attack that is being executed
         * @param independent: Is this attack echoed between server/client (Something that creates a synced event would not need this)
         * @param pos: The starting position associated with this attack (although no hboxes are generated
         */
        public SyncedAttackNoHbox(Vector2 pos, boolean independent, SyncedAttack attack) {
            this.pos = pos;
            this.independent = independent;
            this.attack = attack;
        }
    }

    public static class SyncedAttackNoHboxServer {
        public int creatorID;
        public Vector2 pos;
        public SyncedAttack attack;

        public SyncedAttackNoHboxServer() {}

        /**
         * A SyncedAttackNoHboxServer is sent from server to client to inform them that an attack was executed
         * For most weapons, this packages multiple fields of a single attack to send fewer packets
         * @param creatorID: The entityID of the player that is executing the attack
         * @param pos: The starting position of the hbox this attack will create
         * @param attack: the type of attack that is being executed
         */
        public SyncedAttackNoHboxServer(int creatorID, Vector2 pos, SyncedAttack attack) {
            this.creatorID = creatorID;
            this.pos = pos;
            this.attack = attack;
        }
    }

    public static class SyncedAttackNoHboxExtra extends SyncedAttackNoHbox {
        public float[] extraFields;

        public SyncedAttackNoHboxExtra() {}

        /**
         * A SyncedAttackNoHboxExtra is like a CreateSyncedAttackSingle except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SyncedAttackNoHboxExtra(Vector2 pos, boolean independent, float[] extraFields, SyncedAttack attack) {
            super(pos, independent, attack);
            this.extraFields = extraFields;
        }
    }

    public static class SyncedAttackNoHboxExtraServer extends SyncedAttackNoHboxServer {
        public float[] extraFields;

        public SyncedAttackNoHboxExtraServer() {}

        /**
         * A SyncedAttackNoHboxExtraServer is like a SyncedAttackNoHbox]Server except it carries extra information
         * so the client can process things like charge levels
         * @param extraFields: extra information needed to execute this specific attack
         */
        public SyncedAttackNoHboxExtraServer(int creatorID, Vector2 pos, float[] extraFields, SyncedAttack attack) {
            super(creatorID, pos, attack);
            this.extraFields = extraFields;
        }
    }
}
