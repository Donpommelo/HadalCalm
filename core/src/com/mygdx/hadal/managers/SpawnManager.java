package com.mygdx.hadal.managers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.*;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.User;

public class SpawnManager {

    private final PlayState state;

    //This is the id of the start event that we will be spawning on (for 1-p maps that can be entered from >1 point)
    private final String startID;

    //If a player respawns, they will respawn at the coordinates of a safe point from this list.
    private final Array<StartPoint> savePoints = new Array<>();

    public SpawnManager(PlayState state, String startID) {
        this.state = state;

        //start id is set when loadlevel is called, but might be null otherwise; use empty string as default
        this.startID = null == startID ? "" : startID;
    }

    /**This creates a player to occupy the playstate
     * @param start: start event to spawn the player at.
     * @param name: player name
     * @param loadout: the player's loadout
     * @param old: player's old playerdata if retaining old values.
     * @param reset: should we reset the new player's hp/fuel/ammo?
     * @param client: is this the client's own player?
     * @param hitboxFilter: the new player's collision filter
     * @return the newly created player
     */
    public Player createPlayer(Event start, String name, Loadout loadout, PlayerBodyData old, User user, boolean reset,
                               boolean client, short hitboxFilter) {

        Loadout newLoadout = new Loadout(loadout);

        //process mode-specific loadout changes
        state.getMode().processNewPlayerLoadout(state, newLoadout, user.getConnID());
        user.getLoadoutManager().setActiveLoadout(newLoadout);

        //set start pont, generate one if a designated one isn't passed in
        Event spawn = start;
        if (spawn == null) {
            spawn = getSavePoint(user);
        }

        Vector2 overiddenSpawn = new Vector2();
        if (spawn != null) {
            //servers spawn at the starting point if existent. We prefer using the body's position,
            // but can also use the starting position if it hasn't been created yet.
            if (spawn.getBody() != null) {
                overiddenSpawn.set(spawn.getPixelPosition());
            } else {
                overiddenSpawn.set(spawn.getStartPos());
            }
        }

        //process spawn overrides if the user specifies being spawned at a set location instead of at a start point
        if (state.isServer()) {
            if (user.getTransitionManager().isSpawnOverridden()) {
                overiddenSpawn.set(user.getTransitionManager().getOverrideSpawnLocation());
            }
        }

        Player p;
        if (user.getConnID() < 0) {
            p = new PlayerBot(state, overiddenSpawn, name, old, user, reset, spawn);
        } else if (state.isServer()) {
            if (0 == user.getConnID()) {
                p = new Player(state, overiddenSpawn, name, old, user, reset, spawn);
            } else {
                p = new PlayerClientOnHost(state, overiddenSpawn, name, old, user, reset, spawn);
            }
        } else {
            if (!client) {
                p = new Player(state, overiddenSpawn, name, old, user, reset, spawn);
            } else {
                p = new PlayerSelfOnClient(state, overiddenSpawn, name, null, user, reset, spawn);
            }
        }

        //teleportation particles for reset players (indicates returning to hub)
        if (reset && state.isServer() && user.getEffectManager().isShowSpawnParticles()) {
            EffectEntityManager.getParticle(state, new ParticleCreate(Particle.TELEPORT,
                    new Vector2(p.getStartPos()).sub(0, p.getSize().y / 2))
                    .setLifespan(1.0f)
                    .setSyncType(SyncType.CREATESYNC));
        }

        //for own player, the server must update their user information
        if (state.isServer() && user.getConnID() == 0) {
            HadalGame.usm.getOwnUser().setPlayer(p);
        }
        user.setPlayer(p);

        //mode-specific player modifications
        state.getMode().modifyNewPlayer(state, newLoadout, p, hitboxFilter);
        return p;
    }

    /**
     * This acquires the level's save points. If none, respawn at starting location. If many, choose one randomly
     * @return a save point to spawn a respawned player at
     */
    public StartPoint getSavePoint(String startID, User user) {
        if (!state.isServer()) { return null; }

        Array<StartPoint> validStarts = new Array<>();
        Array<StartPoint> readyStarts = new Array<>();

        //get a list of all start points that match the startID
        for (StartPoint s : savePoints) {
            if (state.getMode().isTeamDesignated() && AlignmentFilter.currentTeams.length > s.getTeamIndex()) {
                if (user.getTeamFilter().equals(AlignmentFilter.currentTeams[s.getTeamIndex()])) {
                    validStarts.add(s);
                }
            } else if (s.getStartId().equals(startID)) {
                validStarts.add(s);
            }
        }

        //if no start points are found, we return the first save point (if existent)
        if (validStarts.isEmpty()) {
            if (state.getMode().isTeamDesignated()) {
                validStarts.addAll(savePoints);
            } else {
                if (savePoints.isEmpty()) {
                    return null;
                } else {
                    return savePoints.get(0);
                }
            }
        }

        //add all valid starts that haven't had a respawn recently.
        for (StartPoint s : validStarts) {
            if (s.isReady()) {
                readyStarts.add(s);
            }
        }

        //if any start points haven't been used recently, pick one of them randomly. Otherwise pick a random valid start point
        if (readyStarts.isEmpty()) {
            int randomIndex = MathUtils.random(validStarts.size - 1);
            validStarts.get(randomIndex).startPointSelected();
            return validStarts.get(randomIndex);
        } else {
            int randomIndex = MathUtils.random(readyStarts.size - 1);
            readyStarts.get(randomIndex).startPointSelected();
            return readyStarts.get(randomIndex);
        }
    }

    /**
     * This returns a single starting point for a newly spawned player to spawn at.
     */
    public StartPoint getSavePoint(User user) {
        return getSavePoint(startID, user);
    }

    /**
     * This adds a save point to the list of available spawns
     */
    public void addSavePoint(StartPoint start) {
        savePoints.add(start);
    }
}
