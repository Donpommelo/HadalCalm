package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.SteeringUtil;

/**
 * UIMomentum appears in the bottom right screen and displays information about the player's momentum freezing cd and stored momentums
 * @author Zachary Tu
 *
 */
public class UIObjective extends AHadalActor {

	private Player player;
	private PlayState state;
	
	private TextureRegion base, ready, overlay;
	
	private float scale = 0.25f;
	
	private float corner;
	
	public UIObjective(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		
		this.base = Sprite.UI_MO_BASE.getFrame();
		this.ready = Sprite.UI_MO_READY.getFrame();
		this.overlay = Sprite.UI_MO_OVERLAY.getFrame();
		
		this.corner = SteeringUtil.vectorToAngle(new Vector2(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT));
	}
	
	private float x, y, angle;
	private Vector2 toObjective = new Vector2();
	@Override
    public void draw(Batch batch, float alpha) {

		x = 500;
		y = 500;
		
		if (state.getObjectiveTarget() != null && player.getBody() != null) {
			
			float xDist = (player.getPixelPosition().x) - (state.getObjectiveTarget().getPixelPosition().x);
			float yDist = (player.getPixelPosition().y) - (state.getObjectiveTarget().getPixelPosition().y);		
			
			if (Math.abs(xDist) > HadalGame.CONFIG_WIDTH / 2 || Math.abs(yDist) > HadalGame.CONFIG_HEIGHT / 2) {
				toObjective.set(xDist, yDist);
				
				angle = SteeringUtil.vectorToAngle(toObjective);
				
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
				batch.setProjectionMatrix(state.sprite.combined);
				x = state.getObjectiveTarget().getPixelPosition().x;
				y = state.getObjectiveTarget().getPixelPosition().y;
			}
			
			batch.draw(base, x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
			batch.draw(ready, x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
			batch.draw(overlay, x - base.getRegionWidth() * scale / 2, y - base.getRegionHeight() * scale / 2, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
			
			batch.setProjectionMatrix(state.hud.combined);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
