package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class Speargun extends RangedWeapon {

	private final static String name = "Harpoon Gun";
	private final static int clipSize = 6;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 6;
	private final static float baseDamage = 50.0f;
	private final static float recoil = 1.5f;
	private final static float knockback = 3.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 10;
	private final static float lifespan = 0.75f;
	private final static float gravity = 1;
	
	private final static int projDura = 2;
	
	private TextureAtlas atlas;
	private static TextureRegion projectileSprite;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			Hitbox proj = new Hitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, world, camera, rays, user) {
				
				@Override
				public void render(SpriteBatch batch) {

					batch.setProjectionMatrix(state.hud.combined);
					Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
					camera.project(bodyScreenPosition);
										
					batch.draw(projectileSprite, 
							bodyScreenPosition.x - projectileWidth / 2, 
							bodyScreenPosition.y - projectileHeight / 2, 
							projectileWidth / 2, projectileHeight / 2,
							projectileWidth, projectileHeight, 1, 1, 
							(float) Math.toDegrees(body.getAngle()) + 180);
				}
				
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback));
						}
					}
					super.onHit(fixB);
				}
			});		
			
			return null;
		}
		
	};
	
	public Speargun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString());
		projectileSprite = atlas.findRegion("harpoon");
	}

}
