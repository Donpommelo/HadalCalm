package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.SteeringUtil;

/**
 * UIObjective displays an icon along the periphery of the screen to indicate the location of an objective.
 * These objectives can be set by the objective event.
 * @author Zachary Tu
 */
public class UIObjective extends AHadalActor {

	private PlayState state;
	
	private TextureRegion icon;
	
	private final static float scale = 0.4f;
	
	private float width, height;
	
	private float corner;
	
	//If there is an objective target that has a display if offscreen, this is that entity.
	private HadalEntity objectiveTarget;
	private boolean displayObjectiveOffScreen, displayObjectiveOnScreen;
	
	public UIObjective(PlayState state) {
		this.state = state;
		
		this.icon = Sprite.UI_MO_READY.getFrame();
		this.corner = SteeringUtil.vectorToAngle(new Vector2(HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT));
		
		this.width = icon.getRegionWidth() * scale;
		this.height = icon.getRegionHeight() * scale;
	}
	
	private float x, y, angle;
	private Vector2 toObjective = new Vector2();
	private Vector2 objectiveLocation = new Vector2();
	@Override
    public void draw(Batch batch, float alpha) {

		//This math calculates the location of the objective icon
		if (objectiveTarget != null && state.getPlayer().getBody() != null) {
			
			objectiveLocation.set(objectiveTarget.getPixelPosition());
			
			if (!objectiveTarget.isVisible() && displayObjectiveOffScreen) {
				toObjective.set(state.getPlayer().getPixelPosition()).sub(objectiveLocation);
				
				angle = SteeringUtil.vectorToAngle(toObjective);
				
				if (angle < corner && angle > -(Math.PI + corner)) {
					x = width;
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(Math.abs(angle) - Math.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - height));
				}
				else if (angle > -corner && angle < (Math.PI + corner)) {
					x = HadalGame.CONFIG_WIDTH - width;
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(angle - Math.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - height));
				}
				else if (angle <= -corner && angle >= corner) {
					x = (float) (HadalGame.CONFIG_WIDTH / 2 + Math.tan(angle) * (HadalGame.CONFIG_HEIGHT / 2 - width));
					y = height;
				}
				else if (angle >= (Math.PI + corner) || angle <= -(Math.PI + corner)) {				
					x = (float) (HadalGame.CONFIG_WIDTH / 2 + (angle > 0 ? -1 : 1) * Math.tan(Math.abs(angle) - Math.PI) * (HadalGame.CONFIG_HEIGHT / 2 - width));
					y = HadalGame.CONFIG_HEIGHT - height;
				}
				
				batch.draw(icon, x - width / 2, y - height / 2, width, height);
			} else if (displayObjectiveOnScreen) {
				batch.setProjectionMatrix(state.getCamera().combined);
				x = objectiveLocation.x;
				y = objectiveLocation.y;
				batch.draw(icon, x - width / 2, y - height / 2, width, height);
				batch.setProjectionMatrix(state.getHud().combined);
			}
		}
	}

	public void setObjectiveTarget(HadalEntity objectiveTarget) { this.objectiveTarget = objectiveTarget; }

	public HadalEntity getObjectiveTarget() { return objectiveTarget; }

	public void setDisplayObjectiveOffScreen(boolean displayObjectiveOffScreen) { this.displayObjectiveOffScreen = displayObjectiveOffScreen; }

	public void setDisplayObjectiveOnScreen(boolean displayObjectiveOnScreen) {	this.displayObjectiveOnScreen = displayObjectiveOnScreen; }
	
	public void setIconType(Sprite sprite) { this.icon = sprite.getFrame(); }
}
