package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxHomingStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AbyssalInsignia extends Artifact {

	private final static String name = "Abyssal Insignia";
	private final static String descr = "Release Vengeful Spirit Upon Killing or Dying.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float spiritSize = 25;
	private final static float spiritLifespan = 6.0f;
	private final static float spiritDamage = 25.0f;
	private final static float spiritKnockback = 8.0f;
	
	public AbyssalInsignia() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onKill(BodyData vic) {
				releaseVengefulSpirits(state, new Vector2(
						(vic.getSchmuck().getPosition().x * PPM), 
						(vic.getSchmuck().getPosition().y * PPM)), 
						inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
			
			@Override
			public void onDeath(BodyData perp) {
				releaseVengefulSpirits(state, new Vector2(
						(inflicted.getSchmuck().getPosition().x * PPM), 
						(inflicted.getSchmuck().getPosition().y * PPM)), 
						inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
		};
		
		return enchantment;
	}
	
	private void releaseVengefulSpirits(PlayState state, Vector2 pos, BodyData creator, short filter) {		
		Hitbox hbox = new Hitbox(state, (int)pos.x, (int)pos.y, (int)spiritSize, (int)spiritSize, 0, spiritLifespan, 1, 0, 
				new Vector2(), filter, true, true, creator.getSchmuck());
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, creator));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, creator));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, creator, null, spiritDamage, spiritKnockback));
		hbox.addStrategy(new HitboxHomingStrategy(state, hbox, creator, filter));
		new ParticleEntity(state, hbox, Particle.SHADOW_PATH, spiritLifespan, 0.0f, true, particleSyncType.CREATESYNC);
	}
}
