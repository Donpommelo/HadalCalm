package com.mygdx.hadal.server.packets;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.server.AlignmentFilter;

/**
 * This contains various packets used for syncing loadout between client and server
 *
 */
public class PacketsLoadout {

    public static class SyncWholeLoadout {
        public int connID;
        public Loadout loadout;
        public boolean save;
        public SyncWholeLoadout() {}

        /**
         * A SyncWholeLoadout is sent from the Server to the Client when a Player in the world changes their loadout.
         * Upon receiving this packet, clients adjust their versions of that Player to have the new loadout.
         * Atm, this is sent when a player changes their whole loadout (outfitter) or is spawned)
         * When a player only changes an item at another hub event, they will send a smaller packet
         * This is also sent from client to server when choosing outfit in outfitter
         * @param connID: connection ID of the player to change
         * @param loadout: Player's new loadout
         * @param save: do we save this loadout change in records?
         */
        public SyncWholeLoadout(int connID, Loadout loadout, boolean save) {
            this.connID = connID;
            this.loadout = loadout;
            this.save = save;
        }
    }

    /**
     * This base class exists so that client loadout packets can be processed in a single instanceof statement when received
     */
    public static class SyncLoadoutClient {
        public SyncLoadoutClient() {}
    }

    /**
     * This base class exists so that server loadout packets can be processed in a single instanceof statement when received
     * Also contains connection id information so the client knows which player's loadout changed
     */
    public static class SyncLoadoutServer {
        public int connID;

        public SyncLoadoutServer() {}

        public SyncLoadoutServer(int connID) {
            this.connID = connID;
        }
    }

    public static class SyncEquipClient extends SyncLoadoutClient {
        public UnlockEquip[] equip;

        public SyncEquipClient() {}

        /**
         * A SyncEquipClient is sent from client to server to inform them that they changed weapon in the hub armory
         * @param equip: An equip to be switched to this client's loadout
         */
        public SyncEquipClient(UnlockEquip[] equip) {
            this.equip = equip;
        }
    }

    public static class SyncArtifactAddClient extends SyncLoadoutClient {
        public UnlockArtifact artifactAdd;
        public boolean save;

        public SyncArtifactAddClient() {}

        /**
         * A SyncArtifactAddClient is sent from client to server to inform them that they selected an artifact in the hub reliquary
         * @param artifactAdd: An artifact to be added to this client's loadout
         * @param save: Should this change be saved to the user's saved loadout
         */
        public SyncArtifactAddClient(UnlockArtifact artifactAdd, boolean save) {
            this.artifactAdd = artifactAdd;
            this.save = save;
        }
    }

    public static class SyncArtifactRemoveClient extends SyncLoadoutClient {
        public UnlockArtifact artifactRemove;

        public SyncArtifactRemoveClient() {}

        /**
         * A SyncArtifactRemoveClient is sent from client to server to inform them that they unequipped an artifact in the hub reliquary
         * @param artifactRemove: An artifact to be removed from this client's loadout
         */
        public SyncArtifactRemoveClient(UnlockArtifact artifactRemove) {
            this.artifactRemove = artifactRemove;
        }
    }

    public static class SyncActiveClient extends SyncLoadoutClient {
        public UnlockActives active;

        public SyncActiveClient() {}

        /**
         * A SyncActiveClient is sent from client to server to inform them that chose an active item in the hub dispensary
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
         * A SyncCharacterClient is sent from client to server to inform them that chose character in the hub dormitory
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
         * A SyncTeamClient is sent from client to server to inform them that chose team in the hub painter
         * @param team: A team to be added to this client's loadout
         */
        public SyncTeamClient(AlignmentFilter team) {
            this.team = team;
        }
    }

    public static class SyncCosmeticClient extends SyncLoadoutClient {
        public UnlockCosmetic cosmetic;

        public SyncCosmeticClient() {}

        /**
         * A SyncCosmeticClient is sent from client to server to inform them that chose a cosmetic item in the hub haberdasher
         * @param cosmetic: An cosmetic to be added to this client's loadout
         */
        public SyncCosmeticClient(UnlockCosmetic cosmetic) {
            this.cosmetic = cosmetic;
        }
    }

    public static class SyncEquipServer extends SyncLoadoutServer {
        public UnlockEquip[] equip;

        public SyncEquipServer() {}

        /**
         * A SyncEquipServer is sent from server to client to inform them of a weapon change for a player
         * @param connID: connection id of the player whose weapons changed
         * @param equip: list of weapons to set in the player's loadout
         */
        public SyncEquipServer(int connID, UnlockEquip[] equip) {
            super(connID);
            this.equip = equip;
        }
    }

    public static class SyncArtifactServer extends SyncLoadoutServer {
        public UnlockArtifact[] artifact;
        public boolean save;

        public SyncArtifactServer() {}

        /**
         * A SyncArtifactServer is sent from server to client to inform them of an artifact change for a player
         * @param connID: connection id of the player whose weapons changed
         * @param artifact: list of artifacts to set in the player's loadout
         * @param save: should this artifact change be saved into the user's saved loadout
         */
        public SyncArtifactServer(int connID, UnlockArtifact[] artifact, boolean save) {
            super(connID);
            this.artifact = artifact;
            this.save = save;
        }
    }

    public static class SyncActiveServer extends SyncLoadoutServer {
        public UnlockActives active;

        public SyncActiveServer() {}

        /**
         * A SyncActiveServer is sent from server to client to inform them of an active item change for a player
         * @param connID: connection id of the player whose weapons changed
         * @param active: active item to be set in the player's loadout
         */
        public   SyncActiveServer(int connID, UnlockActives active) {
            super(connID);
            this.active = active;
        }
    }

    public static class SyncCharacterServer extends SyncLoadoutServer {
        public UnlockCharacter character;

        public SyncCharacterServer() {}

        /**
         * A SyncCharacterServer is sent from server to client to inform them of character change for a player
         * @param connID: connection id of the player whose weapons changed
         * @param character: character to be set in the player's loadout
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
         * A SyncTeamServer is sent from server to client to inform them of team change for a player
         * @param connID: connection id of the player whose weapons changed
         * @param team: team to be set in the player's loadout
         */
        public SyncTeamServer(int connID, AlignmentFilter team) {
            super(connID);
            this.team = team;
        }
    }

    public static class SyncCosmeticServer extends SyncLoadoutServer {
        public UnlockCosmetic cosmetic;

        public SyncCosmeticServer() {}

        /**
         * A SyncCosmeticServer is sent from server to client to inform them of a cosmetic change for a player
         * @param connID: connection id of the player whose weapons changed
         * @param cosmetic: cosmetic to be set in the player's loadout
         */
        public SyncCosmeticServer(int connID, UnlockCosmetic cosmetic) {
            super(connID);
            this.cosmetic = cosmetic;
        }
    }
}
