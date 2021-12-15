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
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;
import com.mygdx.hadal.strategies.hitbox.FlagCapturable;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 *
 * Triggered Behavior: This event is triggered when a team scores. Increment score and display notification
 * Triggering Behavior: N/A
 *
 * Fields: teamIndex: int index of the team that is trying to score by bringing enemy flag to this event
 *
 * @author Noporon Nashmere
 */
public class SpawnerFlag extends Event {

    private final int teamIndex;
    private boolean flagPresent;

    public SpawnerFlag(PlayState state, Vector2 startPos, Vector2 size, int teamIndex) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
    }

    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public void onActivate(EventData activator, Player p) {

                if (standardParticle != null) {
                    standardParticle.onForBurst(1.0f);
                }

                //give score credit to the player and give notification
                if (p != null) {
                    String playerName = WeaponUtils.getPlayerColorName(p, MAX_NAME_LENGTH);
                    state.getKillFeed().addNotification(HText.CTF_CAPTURE.text(playerName), false);
                    state.getMode().processPlayerScoreChange(state, p, 1);
                }
                state.getMode().processTeamScoreChange(state, teamIndex, 1);
            }
        };

        this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
            Constants.BIT_SENSOR, Constants.BIT_PROJECTILE, (short) 0, true, eventData);
        this.body.setType(BodyDef.BodyType.KinematicBody);
    }

    private Hitbox flag;
    private float spawnCountdown;
    private static final float spawnDelay = 3.0f;
    private float controllerCount;
    private static final float checkInterval = 0.2f;
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

        controllerCount += delta;
        if (controllerCount >= checkInterval) {
            controllerCount = 0.0f;
            for (HadalEntity entity : eventData.getSchmucks()) {
                if (entity instanceof Hitbox hbox) {
                    if (hbox.getStrategies().size >= 2) {
                        if (hbox.getStrategies().get(1) instanceof FlagCapturable capture) {
                            capture.checkCapture(this);
                        }
                    }
                }
            }
        }

        messageCount -= delta;
    }

    private static final Vector2 flagSize = new Vector2(80, 80);
    private static final float flagLifespan = 240.0f;
    private void spawnFlag() {
        flag = new RangedHitbox(state, new Vector2(getPixelPosition()), flagSize, flagLifespan, new Vector2(),
            (short) 0, false, false, state.getWorldDummy(), Sprite.DIATOM_D);

        //the order of strats is important; FlagCapturable must be second because somethings check for it by index
        flag.addStrategy(new ControllerDefault(state, flag, state.getWorldDummy().getBodyData()));
        flag.addStrategy(new FlagCapturable(state, flag, state.getWorldDummy().getBodyData(), this, teamIndex));
        flag.addStrategy(new DropThroughPassability(state, flag, state.getWorldDummy().getBodyData()));

        Vector3 color = new Vector3();

        //the flag's particle should match the team color
        if (teamIndex < AlignmentFilter.currentTeams.length) {
            HadalColor teamColor = AlignmentFilter.currentTeams[teamIndex].getColor1();
            color.set(teamColor.getR(), teamColor.getG(), teamColor.getB());

            flag.addStrategy(new CreateParticles(state, flag, state.getWorldDummy().getBodyData(), Particle.BRIGHT_TRAIL, 0.0f, 1.0f)
                .setParticleColor(teamColor));
        }

        state.getUiObjective().addObjective(flag, Sprite.CLEAR_CIRCLE_ALERT, color, true, false);

        if (state.isServer()) {
            HadalGame.server.sendToAllTCP(new Packets.SyncObjectiveMarker(flag.getEntityID(),
                color, true, false, Sprite.CLEAR_CIRCLE_ALERT));
        }
        flagPresent = true;
    }

    private static final float messageCooldown = 5.0f;
    private float messageCount = 0.0f;
    public void triggerFailMessage() {

        //message is activated when attempting to capture flag while enemy holds your flag
        if (messageCount <= 0.0f) {
            if (teamIndex < AlignmentFilter.currentTeams.length) {
                messageCount = messageCooldown;
                String teamColor = AlignmentFilter.currentTeams[teamIndex].getColoredAdjective();
                state.getKillFeed().addNotification(HText.CTF_CAPTURE_FAIL.text(teamColor), true);
            }
        }
    }

    public int getTeamIndex() { return teamIndex; }

    @Override
    public void loadDefaultProperties() {
        setScaleAlign("CENTER_STRETCH");
        setSyncType(eventSyncTypes.ALL);
        addAmbientParticle(Particle.RING);

        //this block of code is used b/c the default particle behavior doesn't like effects with custom colors
        if (state.isServer()) {
            standardParticle = new ParticleEntity(state, this,
                Particle.DIATOM_IMPACT_LARGE, 0, 0, true, SyncType.CREATESYNC);
            if (teamIndex < AlignmentFilter.currentTeams.length) {
                standardParticle.setColor(AlignmentFilter.currentTeams[teamIndex].getColor1());
            }
        }
    }

    public boolean isFlagPresent() { return flagPresent; }

    public void setFlagPresent(boolean flagPresent) { this.flagPresent = flagPresent; }
}
