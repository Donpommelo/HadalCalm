package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

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
	private float linger, interval;
	
	//Has the attached entity despawned yet?
	private boolean despawn;
	
	//This constructor creates a particle effect at an area.
	public ParticleEntity(PlayState state, World world, OrthographicCamera camera, RayHandler rays,
			float startX, float startY, String effect, float linger) {
		super(state, world, camera, rays, 0, 0, startX, startY);
		
		particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
		
		this.effect = new ParticleEffect();
		this.effect.load(Gdx.files.internal(effect), particleAtlas);
		this.despawn = true;
		this.linger = linger;
		
		this.effect.start();
	}
	
	//This constructor creates a particle effect that will follow another entity.
	public ParticleEntity(PlayState state, World world, OrthographicCamera camera, RayHandler rays,
			HadalEntity entity, String effect, float linger) {
		super(state, world, camera, rays, 0, 0, 0, 0);
		this.attachedEntity = entity;
		
		particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
		
		this.effect = new ParticleEffect();
		this.effect.load(Gdx.files.internal(effect), particleAtlas);
		this.despawn = false;
		this.linger = linger;
		this.effect.start();
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(world, UserDataTypes.FEET, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 0, 0, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX, true, hadalData);
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
