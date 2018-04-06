package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.SteeringUtil;

/**
 * UIMomentum appears in the bottom right screen and displays information about the player's momentum freezing cd and stored momentums
 * @author Zachary Tu
 *
 */
public class UIObjective extends AHadalActor{

	private Player player;
	private PlayState state;
	
	private TextureAtlas atlas;
	
	private TextureRegion base, ready, overlay;
	private Array<AtlasRegion> arrow;
	
	private float scale = 0.25f;
	
	public UIObjective(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.base = atlas.findRegion("UI_momentum_base");
		this.ready = atlas.findRegion("UI_momentum_ready");
		this.overlay = atlas.findRegion("UI_momentum_overlay");
		
		this.arrow = atlas.findRegions("UI_momentum_arrow");
		
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		float x = 500;
		float y = 500;
		
		if (state.getObjectiveTarget() != null) {
			
			Vector3 playerScreenPosition = new Vector3(player.getBody().getPosition().x, player.getBody().getPosition().y, 0);
			state.camera.project(playerScreenPosition);
			
			Vector3 objectiveScreenPosition = new Vector3(state.getObjectiveTarget().getBody().getPosition().x, state.getObjectiveTarget().getBody().getPosition().y, 0);
			state.camera.project(objectiveScreenPosition);
			
			float xDist = playerScreenPosition.x - objectiveScreenPosition.x;
			float yDist = playerScreenPosition.y - objectiveScreenPosition.y;
			
			
			
			if (Math.abs(xDist) > HadalGame.CONFIG_WIDTH / 2 || Math.abs(yDist) > HadalGame.CONFIG_HEIGHT / 2) {
				Vector2 toObjective = new Vector2(xDist, yDist);
				
				float angle = SteeringUtil.vectorToAngle(toObjective);
				float corner = SteeringUtil.vectorToAngle(new Vector2(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT));

				if (angle < corner && angle > -(Math.PI + corner)) {
					x = (float) (base.getRegionWidth() * scale);
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(Math.abs(angle) - Math.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - base.getRegionWidth() * scale));
				}
				if (angle > -corner && angle < (Math.PI + corner)) {
					x = (float) (HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale);
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(angle - Math.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - base.getRegionWidth() * scale));
				}
				if (angle <= -corner && angle >= corner) {
					x = (float) (HadalGame.CONFIG_WIDTH / 2 + Math.tan(angle) * (HadalGame.CONFIG_HEIGHT / 2 - base.getRegionHeight() * scale));
					y = (float) (base.getRegionHeight() * scale);
				}
				if (angle >= (Math.PI + corner) || angle <= -(Math.PI + corner)) {				
					x = (float) (HadalGame.CONFIG_WIDTH / 2 + (angle > 0 ? -1 : 1) * Math.tan(Math.abs(angle) - Math.PI) * (HadalGame.CONFIG_HEIGHT / 2 - base.getRegionHeight() * scale));
					y = (float) (HadalGame.CONFIG_HEIGHT - base.getRegionHeight() * scale);
				}	
			} else {
				x = objectiveScreenPosition.x;
				y = objectiveScreenPosition.y;
				
			}
			batch.draw(base, x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
			batch.draw(ready, x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
			batch.draw(overlay, x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
			batch.draw(arrow.get(0), x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale / 2, base.getRegionHeight() * scale / 2,
					base.getRegionWidth() * scale, base.getRegionWidth() * scale, 1, 1, 0);
		}
		
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
