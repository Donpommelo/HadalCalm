package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;

public class SkippersBoxOfFunActivate extends SyncedAttacker {

    private static final float DURATION = 1.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.MAGIC27_EVIL.playSourced(state, user.getPixelPosition(), 0.5f);

        ((Player) user).getArtifactIconHelper().addArtifactFlash(UnlockArtifact.SKIPPERS_BOX_OF_FUN);

        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE_TOTLC, user)
                .setLifespan(DURATION));

        Equippable equip = UnlocktoItem.getUnlock(UnlockEquip.getRandWeapFromPool(state, ""), null);
        ((Player) user).getEquipHelper().pickup(equip);
    }
}