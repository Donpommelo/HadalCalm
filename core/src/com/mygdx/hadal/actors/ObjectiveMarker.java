package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;

import java.util.UUID;

/**
 * An Objective Marker represents a single object that should have a ui element displayed when it is offscreen
 *
 * @author Pufka Padatter
 */
public class ObjectiveMarker {

    private final PlayState state;
    private final TextureRegion icon;
    private final TextureRegion arrow;

    //is this icon colored? if so, what color (rgb)
    private final boolean colored;
    private final Color color;

    private static final float scale = 0.4f;

    private final float width, height, arrowWidth, arrowHeight;
    private final float corner;

    //If there is an objective target that has a display if offscreen, this is that entity.
    private HadalEntity objectiveTarget;

    //for the client, this is the id of the entity we want to track (if it hasn't spawned yet)
    private UUID objectiveTargetID;
    private final boolean displayObjectiveOffScreen, displayObjectiveOnScreen;

    public ObjectiveMarker(PlayState state, HadalEntity objectiveTarget, Sprite sprite, HadalColor color,
                           boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
        this.state = state;
        this.objectiveTarget = objectiveTarget;
        this.displayObjectiveOffScreen = displayObjectiveOffScreen;
        this.displayObjectiveOnScreen = displayObjectiveOnScreen;
        this.icon = sprite.getFrame();
        this.color = color.getColor();
        this.colored = !color.equals(HadalColor.NOTHING);

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
    public void draw(Batch batch) {
        if (objectiveTarget != null) {
            objectiveLocation.set(objectiveTarget.getPixelPosition());

            if (colored) {
                batch.setColor(color);
            }

            if (!objectiveTarget.isVisible() && displayObjectiveOffScreen) {

                //identify the angle of the line between the objective and the center of the screen
                centerPosition.set(HadalGame.CONFIG_WIDTH / 2, HadalGame.CONFIG_HEIGHT / 2, 0);
                HadalGame.viewportCamera.unproject(centerPosition);
                toObjective.set(centerPosition.x, centerPosition.y).sub(objectiveLocation);

                //calculate the point of intersection between aforementioned line and the perimeter of the screen
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

            //if we colored this icon, we must change the batch back to normal
            if (colored) {
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }

        //if client is trying to track a nonexistent entity, we search for it here
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

    public HadalEntity getObjectiveTarget() { return objectiveTarget; }

    /**
     * This finds the location of the current game objective. Used for bot ai as well as some shaders
     */
    public Vector2 getObjectiveLocation() {
        if (objectiveTarget != null) {
            return objectiveTarget.getPixelPosition();
        } else {
            return new Vector2();
        }
    }

    public void setObjectiveTargetID(UUID objectiveTargetID) { this.objectiveTargetID = objectiveTargetID; }
}
