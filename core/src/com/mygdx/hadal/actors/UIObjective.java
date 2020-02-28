package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.SteeringUtil;

/**
 * UIObjective displays an icon along the periphery of the screen to indicate the location of an objective.
 * These objectives can be set by the objective event.
 * @author Zachary Tu
 *
 */
public class UIObjective extends AHadalActor {

	private Player player;
	private PlayState state;
	
	private TextureRegion icon;
	
	private float scale = 0.25f;
	
	private float corner;
	
	public UIObjective(PlayState state, Player player) {
		this.player = player;
		this.state = state;
		
		this.icon = Sprite.UI_MO_READY.getFrame();
		this.corner = SteeringUtil.vectorToAngle(new Vector2(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT));
	}
	
	private float x, y, angle;
	private Vector2 toObjective = new Vector2();
	@Override
    public void draw(Batch batch, float alpha) {

		x = 500;
		y = 500;
		
		//This math calculates the location of the objective icon
		if (state.getObjectiveTarget() != null && player.getBody() != null) {
			
			float xDist = (player.getPixelPosition().x) - (state.getObjectiveTarget().getPixelPosition().x);
			float yDist = (player.getPixelPosition().y) - (state.getObjectiveTarget().getPixelPosition().y);		
			
			if (Math.abs(xDist) > HadalGame.CONFIG_WIDTH / 2 || Math.abs(yDist) > HadalGame.CONFIG_HEIGHT / 2) {
				toObjective.set(xDist, yDist);
				
				angle = SteeringUtil.vectorToAngle(toObjective);
				
				if (angle < corner && angle > -(Math.PI + corner)) {
					x = (float) (icon.getRegionWidth() * scale);
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(Math.abs(angle) - Math.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - icon.getRegionWidth() * scale));
				}
				if (angle > -corner && angle < (Math.PI + corner)) {
					x = (float) (HadalGame.CONFIG_WIDTH - icon.getRegionWidth() * scale);
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(angle - Math.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - icon.getRegionWidth() * scale));
				}
				if (angle <= -corner && angle >= corner) {
					x = (float) (HadalGame.CONFIG_WIDTH / 2 + Math.tan(angle) * (HadalGame.CONFIG_HEIGHT / 2 - icon.getRegionHeight() * scale));
					y = (float) (icon.getRegionHeight() * scale);
				}
				if (angle >= (Math.PI + corner) || angle <= -(Math.PI + corner)) {				
					x = (float) (HadalGame.CONFIG_WIDTH / 2 + (angle > 0 ? -1 : 1) * Math.tan(Math.abs(angle) - Math.PI) * (HadalGame.CONFIG_HEIGHT / 2 - icon.getRegionHeight() * scale));
					y = (float) (HadalGame.CONFIG_HEIGHT - icon.getRegionHeight() * scale);
				}	
			} else {
				batch.setProjectionMatrix(state.camera.combined);
				x = state.getObjectiveTarget().getPixelPosition().x;
				y = state.getObjectiveTarget().getPixelPosition().y;
			}
			
			batch.draw(icon, x - icon.getRegionWidth() * scale / 2, y - icon.getRegionHeight() * scale / 2, icon.getRegionWidth() * scale, icon.getRegionHeight() * scale);
			batch.setProjectionMatrix(state.hud.combined);
		}
	}

	public void setPlayer(Player player) { this.player = player; }
}
