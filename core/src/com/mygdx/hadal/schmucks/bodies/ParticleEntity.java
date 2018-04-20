package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.states.PlayState;

/**
 * The particle entity is an invisible, ephemeral entity that emits particle effects.
 * Atm, this is needed so that other entities can have particle effects that persist beyond their own disposal.
 * @author Zachary Tu
 *
 */
public class ParticleEntity extends HadalEntity {

	private static TextureAtlas particleAtlas;
	
	//What particles come out of this entity?
	private ParticleEffect effect;
	
	//Is this entity following another entity?
	private HadalEntity attachedEntity;
	
	//How long this entity will last after deletion.
	private float linger, interval, lifespan;
	
	//Has the attached entity despawned yet?
	private boolean despawn, temp;
	
	//This constructor creates a particle effect at an area.
	public ParticleEntity(PlayState state, float startX, float startY, String effect, float lifespan, boolean startOn) {
		super(state, 0, 0, startX, startY);
		
		particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
		
		this.effect = new ParticleEffect();
		this.effect.load(Gdx.files.internal(effect), particleAtlas);
		
		this.despawn = false;
		
		temp = lifespan != 0;
		this.lifespan = lifespan;
		
		if (startOn) {
			this.effect.start();
		} else {
			this.effect.allowCompletion();
		}
		
		this.effect.setPosition(startX, startY);
	}
	
	//This constructor creates a particle effect that will follow another entity.
	public ParticleEntity(PlayState state, HadalEntity entity, String effect, float linger, float lifespan, boolean startOn) {
		this(state, 0, 0, effect, lifespan, startOn);
		this.attachedEntity = entity;
		this.linger = linger;
	}

	@Override
	public void create() {
		
	}

	@Override
	public void controller(float delta) {
		if (attachedEntity != null) {
			if (attachedEntity.isAlive()) {
				effect.setPosition(attachedEntity.getBody().getPosition().x * PPM, attachedEntity.getBody().getPosition().y * PPM);
			} else {
				despawn = true;
				effect.allowCompletion();
			}
		}
		
		if (despawn) {
			linger -= delta;
			
			if (linger <= 0) {
				this.queueDeletion();
			}
		}
		
		if (temp) {
			lifespan -= delta;
			
			if (lifespan <= 0) {
				this.queueDeletion();
			}
		}
		
		if (interval > 0) {
			interval -= delta;
			
			if (interval <= 0) {
				effect.allowCompletion();
			}
		}
		
	}
	
	public void turnOn() {
		if (effect.isComplete()) {
			effect.start();
		}
	}
	
	public void turnOff() {
		effect.allowCompletion();
	}

	public void onForBurst(float duration) {
		turnOn();
		interval = duration;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.sprite.combined);
		effect.draw(batch, Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose() {
		effect.dispose();
		super.dispose();
	}

	public ParticleEffect getEffect() {
		return effect;
	}

	public void setEffect(ParticleEffect effect) {
		this.effect = effect;
	}

}
