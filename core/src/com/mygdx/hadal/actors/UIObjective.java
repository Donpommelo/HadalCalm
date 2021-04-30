package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

/**
 * UIObjective displays an icon along the periphery of the screen to indicate the location of an objective.
 * These objectives can be set by the objective event.
 * @author Gnetalini Ghoginald
 */
public class UIObjective extends AHadalActor {

	private final PlayState state;

	private final ArrayList<ObjectiveMarker> objectives = new ArrayList<>();
	private final ArrayList<ObjectiveMarker> objectivesToRemove = new ArrayList<>();

	public UIObjective(PlayState state) {
		this.state = state;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {

		if (state.getPlayer().getPlayerData() == null) { return; }
		if (state.getGsm().getSetting().isHideHUD()) { return; }

		if (state.getPlayer().getBody() == null) { return; }

		for (ObjectiveMarker marker: objectives) {
			marker.draw(batch);

			if (marker.getObjectiveTarget() != null) {
				if (!marker.getObjectiveTarget().isAlive()) {
					objectivesToRemove.add(marker);
				}
			}
		}

		for (ObjectiveMarker marker: objectivesToRemove) {
			objectives.remove(marker);
		}
		objectivesToRemove.clear();
	}

	public void addObjective(HadalEntity objective, Sprite sprite,
							 boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
		addObjective(objective, sprite, new Vector3(), displayObjectiveOffScreen, displayObjectiveOnScreen);
	}

	public void addObjective(HadalEntity objective, Sprite sprite, Vector3 color,
							 boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
		objectives.add(new ObjectiveMarker(state, objective, sprite, color,
			displayObjectiveOffScreen, displayObjectiveOnScreen));
	}

	public void addObjectiveClient(String objectiveID, Sprite sprite, Vector3 color,
								   boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen) {
		ObjectiveMarker newObjective = new ObjectiveMarker(state, null, sprite, color,
			displayObjectiveOffScreen, displayObjectiveOnScreen);
		newObjective.setObjectiveTargetID(objectiveID);
		objectives.add(newObjective);
	}

	public ArrayList<ObjectiveMarker> getObjectives() { return objectives; }
}
