package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.states.PlayState;

import java.util.UUID;

/**
 * UIObjective displays an icon along the periphery of the screen to indicate the location of an objective.
 * These objectives can be set by the objective event.
 * @author Gnetalini Ghoginald
 */
public class UIObjective extends AHadalActor {

	private final PlayState state;

	private final Array<ObjectiveMarker> objectives = new Array<>();
	private final Array<ObjectiveMarker> objectivesToRemove = new Array<>();

	public UIObjective(PlayState state) {
		this.state = state;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {

		if (state.getGsm().getSetting().isHideHUD()) { return; }

		//draw all objective markers. Delete the ones attached to dead objects
		for (ObjectiveMarker marker: objectives) {
			marker.draw(batch);

			if (marker.getObjectiveTarget() != null) {
				if (!marker.getObjectiveTarget().isAlive()) {
					objectivesToRemove.add(marker);
				}
			}
		}

		for (ObjectiveMarker marker: objectivesToRemove) {
			objectives.removeValue(marker, false);
		}
		objectivesToRemove.clear();
	}

	public void addObjective(HadalEntity objective, Sprite sprite,
							 boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
		addObjective(objective, sprite, HadalColor.NOTHING, displayObjectiveOffScreen, displayObjectiveOnScreen);
	}

	public void addObjective(HadalEntity objective, Sprite sprite, HadalColor color,
							 boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
		objectives.add(new ObjectiveMarker(state, objective, sprite, color,
			displayObjectiveOffScreen, displayObjectiveOnScreen));
	}

	public void addObjectiveClient(UUID objectiveID, Sprite sprite, HadalColor color,
								   boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
		ObjectiveMarker newObjective = new ObjectiveMarker(state, null, sprite, color,
			displayObjectiveOffScreen, displayObjectiveOnScreen);
		newObjective.setObjectiveTargetID(objectiveID);
		objectives.add(newObjective);
	}

	public Array<ObjectiveMarker> getObjectives() { return objectives; }
}
