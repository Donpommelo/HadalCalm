package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.Static;

public class CallofWalrusUse extends SyncedAttacker {

    public static final float BUFF_DURATION = 4.0f;
    public static final float ATK_SPD_BUFF = 0.15f;
    public static final float DAMAGE_BUFF = 0.3f;

    private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);
    private static final float DURATION = 0.4f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC18_BUFF)
                .setVolume(0.5f)
                .setPosition(user.getPixelPosition()));

        Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, DURATION, new Vector2(),
                (short) 0, false, false, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.RING));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Array<HadalData> buffed = new Array<>();
            @Override
            public void onHit(HadalData fixB, Body body) {
                if (fixB != null) {
                    if (UserDataType.BODY.equals(fixB.getType())) {
                        BodyData ally = (BodyData) fixB;
                        if (ally.getSchmuck().getHitboxFilter() == user.getHitboxFilter()) {
                            if (!buffed.contains(fixB, false)) {
                                buffed.add(fixB);
                                ally.addStatus(new StatusComposite(state, BUFF_DURATION, false, user.getBodyData(), ally,
                                        new StatChangeStatus(state, Stats.TOOL_SPD, ATK_SPD_BUFF, ally),
                                        new StatChangeStatus(state, Stats.DAMAGE_AMP, DAMAGE_BUFF, ally)));

                                EffectEntityManager.getParticle(state, new ParticleCreate(Particle.LIGHTNING_CHARGE, ally.getSchmuck())
                                        .setLifespan(BUFF_DURATION)
                                        .setColor(HadalColor.RED));
                            }
                        }
                    }
                }
            }
        });

        return hbox;
    }
}