package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.utility.EventDeleter;
import com.mygdx.hadal.event.utility.ParticleCreator;
import com.mygdx.hadal.event.utility.PlayerChanger;
import com.mygdx.hadal.event.utility.Sensor;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class GluttonousGreyGlove extends Artifact {

	private final static String name = "Gluttonous Grey Glove";
	private final static String descr = "Occasionally drop a Medpak on Kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private static final float heal = 20.0f;
	private static final int size = 64;
	private static final float chance = 1.0f;
	
	public GluttonousGreyGlove() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			Event changer;
			Event deleter;
			Event particles;
						
			@Override
			public void onKill(BodyData vic) {
				if (GameStateManager.generator.nextFloat() <= chance) {
					Event medpak = new Sensor(state, size, size, 
							(int)(vic.getSchmuck().getBody().getPosition().x * PPM), 
							(int)(vic.getSchmuck().getBody().getPosition().y * PPM), 
							true, false, false, false, 1.0f, true);
					
					if (changer == null) {
						changer = new PlayerChanger(state, heal, 0, 0);
						deleter = new EventDeleter(state);
						particles = new ParticleCreator(state, Particle.PICKUP_HEALTH, 2.0f, false);
					}
					
					medpak.setConnectedEvent(changer);
					changer.setConnectedEvent(deleter);
					deleter.setConnectedEvent(particles);
					medpak.setEventSprite(Sprite.MEDPAK);
					medpak.addAmbientParticle(Particle.EVENT_HOLO);
				}
			}
		};
		return enchantment;
	}
}
