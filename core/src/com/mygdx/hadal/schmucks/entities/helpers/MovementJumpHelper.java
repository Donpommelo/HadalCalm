package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;

public class MovementJumpHelper {

    private static final float HOVER_CD = 0.08f;
    private static final int HOVER_COST = 4;
    private static final float HOVER_POW = 5.0f;
    private static final float HOVER_FUEL_REGEN_CD = 1.5f;

    private static final float JUMP_CD = 0.25f;
    private static final float JUMP_POW = 25.0f;
    private static final int NUM_EXTRA_JUMPS = 1;

    private final PlayState state;
    private final Player player;

    private float jumpCdCount, jumpEffectCount;
    private boolean hoveringAttempt, jumpBuffered, jumping;
    private int extraJumpsUsed = 0;

    public MovementJumpHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;
    }

    public void controllerInterval() {

        //if the player is successfully hovering, run hover(). We check if hover is successful so that effects that run when hovering do not activate when not actually hovering (white smoker)
        if (hoveringAttempt && extraJumpsUsed >= getExtraJumps() && player.getPlayerData().getCurrentFuel() >= getHoverCost()) {
            if (jumpCdCount < 0) {
                hover();
            }
        } else {
            player.getEffectHelper().toggleHoverEffects(false);
        }
    }

    public void controller(float delta) {
        jumpCdCount -= delta;

        //if inputting certain actions during cooldown, an action is buffered
        if (jumpBuffered && jumpCdCount < 0) {
            jumpBuffered = false;
            jumpAttempt();
        }
    }

    public void controllerUniversal(float delta, Vector2 playerPosition) {
        if (jumping) {
            if (jumpEffectCount <= 0.0f) {
                jumpEffectCount = JUMP_CD;
                jumpEffect(playerPosition);
            }
        }

        jumpEffectCount -= delta;
        if (jumpEffectCount <= 0.0f) {
            jumping = false;
        }
    }

    private final Vector2 hoverDirection = new Vector2();
    public void hover() {
        if (jumpCdCount < 0) {

            //hovering sets fuel regen on cooldown
            if (player.getFuelHelper().getFuelRegenCdCount() < HOVER_FUEL_REGEN_CD) {
                player.getFuelHelper().setFuelRegenCdCount(HOVER_FUEL_REGEN_CD);
            }

            //Player will continuously do small upwards bursts that cost fuel.
            player.getPlayerData().fuelSpend(getHoverCost());
            jumpCdCount = HOVER_CD;

            hoverDirection.set(0, getHoverPower());

            if (player.getPlayerData().getStat(Stats.HOVER_CONTROL) > 0) {
                hoverDirection.setAngleDeg(player.getMouseHelper().getAttackAngle() + 180);
                player.push(hoverDirection);
            } else {
                player.pushMomentumMitigation(0, hoverDirection.y);
            }

            player.getPlayerData().statusProcTime(new ProcTime.whileHover(hoverDirection));

            player.getEffectHelper().toggleHoverEffects(true);
        } else {
            player.getEffectHelper().toggleHoverEffects(false);
        }
    }

    /**
     * Player's jump. Player moves up if they have jumps left.
     */
    public void jumpAttempt() {
        if (player.getGroundedHelper().isGrounded()) {
            if (jumpCdCount < 0) {
                jump();
            } else {
                jumpBuffered = true;
            }
        } else {
            if (extraJumpsUsed < getExtraJumps()) {
                if (jumpCdCount < 0) {
                    extraJumpsUsed++;
                    jump();
                } else {
                    jumpBuffered = true;
                }
            }
        }
    }

    private void jump() {
        jumpCdCount = JUMP_CD;
        player.pushMomentumMitigation(0, getJumpPower());
        jumping = true;
    }

    private void jumpEffect(Vector2 playerPosition) {
        if (!player.getEffectHelper().isInvisible()) {
            ParticleEntity entity;
            if (player.getGroundedHelper().isGrounded()) {

                //activate jump particles and sound
                entity = new ParticleEntity(state, new Vector2(playerPosition).sub(0, player.getSize().y / 2),
                        Particle.WATER_BURST, 1.0f, true, SyncType.NOSYNC);
                SoundEffect.JUMP.playSourced(state, playerPosition, 0.2f);
            } else {
                //activate double-jump particles and sound
                entity = new ParticleEntity(state, player, Particle.SPLASH, 0.0f, 0.75f, true, SyncType.NOSYNC);
                SoundEffect.DOUBLEJUMP.playSourced(state, playerPosition, 0.2f);
            }

            if (!state.isServer()) {
                ((ClientState) state).addEntity(entity.getEntityID(), entity, false, PlayState.ObjectLayer.EFFECT);
            }
        }
    }

    public int getExtraJumps() { return NUM_EXTRA_JUMPS + (int) player.getPlayerData().getStat(Stats.JUMP_NUM); }

    public float getJumpPower() { return JUMP_POW * (1 + player.getPlayerData().getStat(Stats.JUMP_POW)); }

    public float getHoverPower() { return HOVER_POW * (1 + player.getPlayerData().getStat(Stats.HOVER_POW)); }

    public float getHoverCost() { return HOVER_COST * (1 + player.getPlayerData().getStat(Stats.HOVER_COST)); }

    public void setHoveringAttempt(boolean hoveringAttempt) { this.hoveringAttempt = hoveringAttempt; }

    public boolean isJumping() { return jumping; }

    public void setJumping(boolean jumping) { this.jumping = jumping; }

    public int getExtraJumpsUsed() { return extraJumpsUsed; }

    public void setExtraJumpsUsed(int extraJumpsUsed) { this.extraJumpsUsed = extraJumpsUsed; }
}
