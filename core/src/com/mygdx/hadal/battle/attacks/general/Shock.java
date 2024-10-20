package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.TravelDistanceDie;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Shock extends SyncedAttacker {

    //these manage the trail that shows the lightning particles
    private static final Vector2 TRAIL_SIZE = new Vector2(10, 10);
    private static final float TRAIL_SPEED = 120.0f;
    private static final float TRAIL_LIFESPAN = 3.0f;

    private final DamageSource damageSource;

    public Shock(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.ZAP.playSourced(state, user.getPixelPosition(), 0.4f);

        User inflicter = null;
        int chainAmount = 0;
        float damage = 0.0f;
        int radius = 0;
        if (extraFields.length > 3) {
            inflicter = HadalGame.usm.getUsers().get((int) extraFields[0]);

            chainAmount = (int) extraFields[1];
            damage = extraFields[2];
            radius = (int) extraFields[3];
        }

        if (null != inflicter) {
            Player player = inflicter.getPlayer();
            if (null != player) {

                //spread status to new victim with -1 jump and damage them.
                user.getBodyData().addStatus(new Shocked(state, player.getBodyData(), user.getBodyData(), damage, radius,
                chainAmount - 1, player.getHitboxFilter(), getSyncedAttack()));
                user.getBodyData().receiveDamage(damage, new Vector2(), player.getBodyData(), true, null,
                        damageSource, DamageTag.LIGHTNING);

                //draw the trail that makes the lightning particles visible
                Vector2 trailPath = new Vector2(user.getPosition()).sub(startPosition);
                Hitbox trail = new RangedHitbox(state, new Vector2(startPosition).scl(PPM), TRAIL_SIZE, TRAIL_LIFESPAN,
                        new Vector2(trailPath).nor().scl(TRAIL_SPEED), (short) 0, true, false,
                        player, Sprite.NOTHING);

                trail.addStrategy(new ControllerDefault(state, trail, player.getBodyData()));
                trail.addStrategy(new AdjustAngle(state, trail, player.getBodyData()));
                trail.addStrategy(new TravelDistanceDie(state, trail, player.getBodyData(), trailPath.len()));
                trail.addStrategy(new CreateParticles(state, trail, player.getBodyData(), Particle.LIGHTNING_BOLT,
                        0.0f, 3.0f).setRotate(true).setSyncType(SyncType.NOSYNC));

                if (!state.isServer()) {
                    ((ClientState) state).addEntity(trail.getEntityID(), trail, false, ObjectLayer.HBOX);
                }
            }
        }
    }
}