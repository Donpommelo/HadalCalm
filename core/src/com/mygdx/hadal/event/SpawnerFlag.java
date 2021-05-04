package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.CapturableFlag;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 *
 * Triggered Behavior: This event is triggered when a team score. Increment score and display notification
 * Triggering Behavior: N/A
 *
 * Fields: teamIndex: int index of the team that is trying to score on this goal
 *
 */
public class SpawnerFlag extends Event {

    private final int teamIndex;

    public SpawnerFlag(PlayState state, Vector2 startPos, Vector2 size, int teamIndex) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
    }

    private static final int maxNameLength = 25;
    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public void onActivate(EventData activator, Player p) {
                state.getUiExtra().changeTeamField(teamIndex, 1);

                if (standardParticle != null) {
                    standardParticle.onForBurst(1.0f);
                }

                //give score credit to the player and give notification
                if (p != null) {
                    String playerName = WeaponUtils.getPlayerColorName(p, maxNameLength);
                    state.getKillFeed().addNotification(playerName + " CAPTURED THE FLAG!", false);
                    state.getUiExtra().changeFields(p, 1, 0, 0, 0, false);
                }
            }
        };

        this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
            Constants.BIT_SENSOR, Constants.BIT_PROJECTILE, (short) 0, true, eventData);
        this.body.setType(BodyDef.BodyType.KinematicBody);
    }

    private Hitbox flag;
    private float spawnCountdown;
    private static final float spawnDelay = 2.5f;
    @Override
    public void controller(float delta) {

        //flag is spawned after a set delay
        if (spawnCountdown > 0.0f) {
            spawnCountdown -= delta;
            if (spawnCountdown <= 0.0f) {
                spawnFlag();
            }
        } else {

            //spawn a flag if it is dead or nonexistent
            boolean flagded = false;
            if (flag == null) {
                flagded = true;
            } else if (!flag.isAlive()) {
                flagded = true;
            }

            if (flagded) {
                spawnCountdown = spawnDelay;

                if (getStandardParticle() != null) {
                    getStandardParticle().onForBurst(spawnDelay);
                }
            }
        }
    }

    private static final Vector2 flagSize = new Vector2(80, 80);
    private static final float flagLifespan = 240.0f;
    private void spawnFlag() {
        flag = new RangedHitbox(state, new Vector2(getPixelPosition()), flagSize, flagLifespan, new Vector2(),
            (short) 0, false, false, state.getWorldDummy(), Sprite.DIATOM_D);

        flag.addStrategy(new ControllerDefault(state, flag, state.getWorldDummy().getBodyData()));
        flag.addStrategy(new CapturableFlag(state, flag, state.getWorldDummy().getBodyData(), teamIndex));

        Vector3 color = new Vector3();
        if (teamIndex < AlignmentFilter.currentTeams.length) {
            HadalColor teamColor = AlignmentFilter.currentTeams[teamIndex].getColor1();
            color.set(teamColor.getR(), teamColor.getG(), teamColor.getB());

            flag.addStrategy(new CreateParticles(state, flag, state.getWorldDummy().getBodyData(), Particle.BRIGHT_TRAIL, 0.0f, 1.0f)
                .setParticleColor(teamColor));
        }

        state.getUiObjective().addObjective(flag, Sprite.CLEAR_CIRCLE_ALERT, color, true, false);

        if (state.isServer()) {
            HadalGame.server.sendToAllTCP(new Packets.SyncObjectiveMarker(flag.getEntityID().toString(),
                color, true, false, Sprite.CLEAR_CIRCLE_ALERT));
        }
    }

    public int getTeamIndex() { return teamIndex; }

    @Override
    public void loadDefaultProperties() {
        setScaleAlign("CENTER_STRETCH");
        setSyncType(eventSyncTypes.ALL);
        addAmbientParticle(Particle.RING);

        if (state.isServer()) {
            standardParticle = new ParticleEntity(state, this,
                Particle.DIATOM_IMPACT_LARGE, 0, 0, true, ParticleEntity.particleSyncType.CREATESYNC);
            if (teamIndex < AlignmentFilter.currentTeams.length) {
                standardParticle.setColor(AlignmentFilter.currentTeams[teamIndex].getColor1());
            }
        }
    }
}
