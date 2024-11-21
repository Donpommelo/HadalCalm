package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class ReloaderUse extends SyncedAttacker {

    public static final float DURATION = 1.5f;
    private static final float BONUS_ATK_SPD_1 = 0.45f;
    private static final float BONUS_ATK_SPD_2 = 0.3f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.RELOAD)
                .setVolume(0.4f)
                .setPosition(startPosition));

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.PICKUP_AMMO, user)
                .setLifespan(DURATION));

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BRIGHT, user)
                .setLifespan(DURATION)
                .setColor(HadalColor.RED));

        if (user instanceof Player player) {
            user.getBodyData().addStatus(new StatusComposite(state, DURATION, false, user.getBodyData(), user.getBodyData(),
                    new Status(state, user.getBodyData())) {

                @Override
                public void onShoot(Equippable tool) {

                    float modifiedAttackSpeed = BONUS_ATK_SPD_1;

                    if (tool instanceof RangedWeapon weapon) {
                        if (weapon.getClipSize() <= 1) {
                            modifiedAttackSpeed = 0;
                        } else if (weapon.getClipSize() <= 4) {
                            modifiedAttackSpeed = BONUS_ATK_SPD_2;
                        }
                    }

                    float cooldown = player.getShootHelper().getShootCdCount();
                    player.getShootHelper().setShootCdCount(cooldown * (1 - modifiedAttackSpeed));
                    tool.gainClip(1);
                }
            });

            for (Equippable e : player.getEquipHelper().getMultitools()) {
                e.gainClip(100);
                e.gainAmmo(0.5f);
            }
        }
    }
}