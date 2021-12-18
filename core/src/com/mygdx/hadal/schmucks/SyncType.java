package com.mygdx.hadal.schmucks;

/**
 * These are the different ways sound and particle entities can be synced between client at server
 * NOSYNC: The entity is not synced, but crreated independently between client and server
 * CREATESYNC: Server informs the server when the entity is created and deleted
 * TICKSYNC: Server repeatedly sends client packets about the entity's status while it is alive
 *
 */
public enum SyncType {
    NOSYNC,
    CREATESYNC,
    TICKSYNC
}
