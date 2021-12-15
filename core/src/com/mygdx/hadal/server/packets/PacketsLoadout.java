package com.mygdx.hadal.server.packets;

import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.server.AlignmentFilter;

public class PacketsLoadout {

    public static class SyncLoadoutClient {
        public SyncLoadoutClient() {}
    }

    public static class SyncLoadoutServer {
        public int connID;

        public SyncLoadoutServer() {}

        public SyncLoadoutServer(int connID) {
            this.connID = connID;
        }
    }

    public static class SyncEquipClient extends SyncLoadoutClient {
        public UnlockEquip equip;

        public SyncEquipClient() {}

        /**
         *
         * @param equip: An equip to be switched to this client's loadout
         */
        public SyncEquipClient(UnlockEquip equip) {
            this.equip = equip;
        }
    }

    public static class SyncArtifactAddClient extends SyncLoadoutClient {
        public UnlockArtifact artifactAdd;
        public boolean save;

        public SyncArtifactAddClient() {}

        /**
         * @param artifactAdd: An artifact to be added to this client's loadout
         */
        public SyncArtifactAddClient(UnlockArtifact artifactAdd, boolean save) {
            this.artifactAdd = artifactAdd;
            this.save = save;
        }
    }

    public static class SyncArtifactRemoveClient extends SyncLoadoutClient {
        public UnlockArtifact artifactRemove;
        public boolean save;

        public SyncArtifactRemoveClient() {}

        /**
         * @param artifactRemove: An artifact to be removed from this client's loadout
         */
        public SyncArtifactRemoveClient(UnlockArtifact artifactRemove, boolean save) {
            this.artifactRemove = artifactRemove;
            this.save = save;
        }
    }

    public static class SyncActiveClient extends SyncLoadoutClient {
        public UnlockActives active;

        public SyncActiveClient() {}

        /**
         * @param active: An active item to be added to this client's loadout
         */
        public SyncActiveClient(UnlockActives active) {
            this.active = active;
        }
    }

    public static class SyncCharacterClient extends SyncLoadoutClient {
        public UnlockCharacter character;

        public SyncCharacterClient() {}

        /**
         * @param character: A character to be added to this client's loadout
         */
        public SyncCharacterClient(UnlockCharacter character) {
            this.character = character;
        }
    }

    public static class SyncTeamClient extends SyncLoadoutClient {
        public AlignmentFilter team;

        public SyncTeamClient() {}

        /**
         * @param team: A team to be added to this client's loadout
         */
        public SyncTeamClient(AlignmentFilter team) {
            this.team = team;
        }
    }

    public static class SyncEquipServer extends SyncLoadoutServer {
        public UnlockEquip[] equip;

        public SyncEquipServer() {}

        /**
         */
        public SyncEquipServer(int connID, UnlockEquip[] equip) {
            super(connID);
            this.equip = equip;
        }
    }

    public static class SyncArtifactServer extends SyncLoadoutServer {
        public long uuidMSB, uuidLSB;
        public UnlockArtifact[] artifact;
        public boolean save;

        public SyncArtifactServer() {}

        /**
         */
        public SyncArtifactServer(int connID, UnlockArtifact[] artifact, boolean save) {
            super(connID);
            this.artifact = artifact;
            this.save = save;
        }
    }

    public static class SyncArtifactRemoveServer extends SyncLoadoutServer {
        public UnlockArtifact artifactAdd;
        public boolean save;

        public SyncArtifactRemoveServer() {}

        /**
         */
        public SyncArtifactRemoveServer(int connID, UnlockArtifact artifactAdd, boolean save) {
            super(connID);
            this.artifactAdd = artifactAdd;
            this.save = save;
        }
    }

    public static class SyncActiveServer extends SyncLoadoutServer {
        public UnlockActives actives;

        public SyncActiveServer() {}

        /**
         */
        public SyncActiveServer(int connID, UnlockActives actives) {
            super(connID);
            this.actives = actives;
        }
    }

    public static class SyncCharacterServer extends SyncLoadoutServer {
        public UnlockCharacter character;

        public SyncCharacterServer() {}

        /**
         */
        public SyncCharacterServer(int connID, UnlockCharacter character) {
            super(connID);
            this.character = character;
        }
    }

    public static class SyncTeamServer extends SyncLoadoutServer {
        public AlignmentFilter team;

        public SyncTeamServer() {}

        /**
         */
        public SyncTeamServer(int connID, AlignmentFilter team) {
            super(connID);
            this.team = team;
        }
    }
}
