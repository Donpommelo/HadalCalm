package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;

/**
 * UIObjective displays an icon along the periphery of the screen to indicate the location of an objective.
 * These objectives can be set by the objective event.
 * @author Gnetalini Ghoginald
 */
public class UIObjective extends AHadalActor {

	private final PlayState state;
	
	private TextureRegion icon;
	private final TextureRegion arrow;

	private static final float scale = 0.4f;
	
	private final float width, height, arrowWidth, arrowHeight;
	private final float corner;
	
	//If there is an objective target that has a display if offscreen, this is that entity.
	private HadalEntity objectiveTarget;
	private String objectiveTargetID;
	private boolean displayObjectiveOffScreen, displayObjectiveOnScreen;
	
	public UIObjective(PlayState state) {
		this.state = state;
		
		this.icon = Sprite.CLEAR_CIRCLE_ALERT.getFrame();
		this.arrow = Sprite.NOTIFICATIONS_DIRECTIONAL_ARROW.getFrame();
		this.corner = MathUtils.atan2(-HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
		this.width = icon.getRegionWidth() * scale;
		this.height = icon.getRegionHeight() * scale;
		this.arrowWidth = arrow.getRegionWidth() * scale;
		this.arrowHeight = arrow.getRegionHeight() * scale;
	}
	
	private float x, y;
	private final Vector2 toObjective = new Vector2();
	private final Vector2 objectiveLocation = new Vector2();
	private final Vector3 centerPosition = new Vector3();
	@Override
    public void draw(Batch batch, float alpha) {

		if (state.getPlayer().getPlayerData() == null) { return; }
		if (state.getGsm().getSetting().isHideHUD()) { return; }

		//This math calculates the location of the objective icon
		if (objectiveTarget != null && state.getPlayer().getBody() != null) {

			objectiveLocation.set(objectiveTarget.getPixelPosition());
			
			if (!objectiveTarget.isVisible() && displayObjectiveOffScreen) {
				centerPosition.set(HadalGame.CONFIG_WIDTH / 2, HadalGame.CONFIG_HEIGHT / 2, 0);
				HadalGame.viewportCamera.unproject(centerPosition);
				toObjective.set(centerPosition.x, centerPosition.y).sub(objectiveLocation);
				float angle = MathUtils.atan2(-toObjective.x, toObjective.y);
				float tanAngle = (float) (Math.tan(angle) * (HadalGame.CONFIG_HEIGHT / 2 - height));
				if (angle < corner && angle > -(MathUtils.PI + corner)) {
					x = width;
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 - Math.tan(angle + MathUtils.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - width));
				}
				else if (angle > -corner && angle < (MathUtils.PI + corner)) {
					x = HadalGame.CONFIG_WIDTH - width;
					y = (float) (HadalGame.CONFIG_HEIGHT / 2 + Math.tan(angle - MathUtils.PI / 2) * (HadalGame.CONFIG_WIDTH / 2 - width));
				}
				else if (angle <= -corner && angle >= corner) {
					x = HadalGame.CONFIG_WIDTH / 2 + tanAngle;
					y = height;
				}
				else if (angle >= (MathUtils.PI + corner) || angle <= -(MathUtils.PI + corner)) {
					x = HadalGame.CONFIG_WIDTH / 2 - tanAngle;
					y = HadalGame.CONFIG_HEIGHT - height;
				}
				
				batch.draw(icon, x - width / 2, y - height / 2, width, height);
				batch.draw(arrow, x + width / 2 + 1, y - arrowHeight / 2, - width / 2 - 1, arrowHeight / 2,
					arrowWidth, arrowHeight, 1, 1, 180 * angle / MathUtils.PI - 90);
			} else if (displayObjectiveOnScreen) {

				//if desired, we display an on-screen objective icon directly on top of the objective
				batch.setProjectionMatrix(state.getCamera().combined);
				x = objectiveLocation.x;
				y = objectiveLocation.y;
				batch.draw(icon, x - width / 2, y - height / 2, width, height);
				batch.setProjectionMatrix(state.getHud().combined);
			}
		}

		if (!state.isServer()) {
			if (objectiveTargetID != null) {
				HadalEntity newObjective = state.findEntity(objectiveTargetID);
				if (newObjective != null) {
					objectiveTarget = newObjective;
					objectiveTargetID = null;
				}
			}
		}
	}

	public void setObjectiveTarget(HadalEntity objectiveTarget) { this.objectiveTarget = objectiveTarget; }

	public HadalEntity getObjectiveTarget() { return objectiveTarget; }

	public void setObjectiveTargetID(String objectiveTargetID) { this.objectiveTargetID = objectiveTargetID; }

	public void setDisplayObjectiveOffScreen(boolean displayObjectiveOffScreen) { this.displayObjectiveOffScreen = displayObjectiveOffScreen; }

	public void setDisplayObjectiveOnScreen(boolean displayObjectiveOnScreen) {	this.displayObjectiveOnScreen = displayObjectiveOnScreen; }
	
	public void setIconType(Sprite sprite) { this.icon = sprite.getFrame(); }
}
