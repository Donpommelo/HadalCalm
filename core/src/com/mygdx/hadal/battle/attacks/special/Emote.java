package com.mygdx.hadal.battle.attacks.special;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.actors.ChatWheel;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Emote extends SyncedAttacker {

    public static final float EMOTE_EXPLODE_DAMAGE = 90.0f;
    private static final Vector2 EMOTE_SIZE = new Vector2(64, 64);
    private static final float EMOTE_LIFESPAN = 1.9f;
    private static final float EMOTE_LIFESPAN_LONG = 6.0f;
    private static final int EMOTE_EXPLODE_RADIUS = 150;
    private static final float EMOTE_EXPLODE_KNOCKBACK = 20;
    private static final float PROJ_DAMPEN = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        boolean special = user.getBodyData().getStat(Stats.PING_DAMAGE) > 0.0f;
        int spriteIndex = 0;
        if (1 <= extraFields.length) {
            spriteIndex = (int) extraFields[0];
        }

        Sprite emote = ChatWheel.indexToEmote(spriteIndex);

        Hitbox hbox = new RangedHitbox(state, new Vector2(user.getPixelPosition()).add(0, user.getSize().y / 2 + 50), EMOTE_SIZE,
                special ? EMOTE_LIFESPAN_LONG : EMOTE_LIFESPAN, new Vector2(), (short) 0, !special, special, user, emote);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float controllerCount;
            private final Vector2 entityLocation = new Vector2();
            @Override
            public void controller(float delta) {

                //non-special emotes despawn if the user dies
                if (!user.isAlive() && !special) {
                    hbox.die();
                } else {
                    controllerCount += delta;

                    if (EMOTE_LIFESPAN >= controllerCount) {
                        entityLocation.set(user.getPosition()).add(0, (user.getSize().y / 2 + 50) / PPM);
                        hbox.setTransform(entityLocation, hbox.getAngle());
                        hbox.setLinearVelocity(user.getLinearVelocity());
                    }
                }
            }
        });

        //with the Finger equipped, emotes detach and explode
        if (special) {
            hbox.setRestitution(0.5f);
            hbox.addStrategy(new Pushable(state, hbox, user.getBodyData(), 1.0f));
            hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(EMOTE_LIFESPAN + 1.0f));
            hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EMOTE_EXPLODE_RADIUS, EMOTE_EXPLODE_DAMAGE,
                    EMOTE_EXPLODE_KNOCKBACK, (short) 0, false, DamageSource.THE_FINGER));
            hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.4f).setSynced(false));
            hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
            hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                @Override
                public void create() {
                    super.create();
                    hbox.getBody().setLinearDamping(PROJ_DAMPEN);
                }
            });
        }

        return hbox;
    }
}