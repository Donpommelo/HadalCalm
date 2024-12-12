package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.RagdollManager;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.schmucks.entities.helpers.PlayerSpriteHelper;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.CombatUtil;

/**
 * This status makes units spawn a ragdoll upon death. This is used by certain enemies
 * @author Rameyer Relforpo
 */
public class DeathRagdoll extends Status {

	private static final float GIB_DURATION = 1.0f;
	private static final float VAPORIZATION_DURATION = 1.5f;
	private static final float GRAVITY = 1.0f;
	
	//this is the sprite of the ragdoll to be spawned and the size of the ragdoll
	private final Sprite sprite;
	private final Vector2 size = new Vector2();
	
	public DeathRagdoll(PlayState state, BodyData p, Sprite sprite, Vector2 size) {
		super(state, p);
		this.sprite = sprite;
		this.size.set(size);
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
		PlayerSpriteHelper.DespawnType type = CombatUtil.getDespawnType(source, tags);

		RagdollCreate ragdollCreate = new RagdollCreate()
				.setSprite(sprite)
				.setPosition(inflicted.getSchmuck().getPixelPosition())
				.setSize(size)
				.setVelocity(inflicted.getSchmuck().getLinearVelocity())
				.setStartVelocity(true);

		switch (type) {
			case GIB -> ragdollCreate.setLifespan(GIB_DURATION).setGravity(GRAVITY).setFade();
			case VAPORIZE -> ragdollCreate.setLifespan(VAPORIZATION_DURATION).setFade(1.25f, Shader.PERLIN_COLOR_FADE);
		}

		RagdollManager.getRagdoll(state, ragdollCreate);
	}
}
