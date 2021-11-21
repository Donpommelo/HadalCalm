package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.bots.BotController;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPath;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.FlagHoldable;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 *  This modifier is used for thte Kingmaker mode and simply makes bots able to locate and pursue the crown
 *  @author Lolias Lilmiwig
 */
public class ModeKingmaker extends ModeSetting {

    private static final float crownDesireMultiplier = 0.05f;
    private final Vector2 objectiveLocation = new Vector2();
    @Override
    public RallyPath processAIPath(PlayState state, GameMode mode, PlayerBot p, Vector2 playerLocation, Vector2 playerVelocity) {
        if (!state.getUiObjective().getObjectives().isEmpty()) {
            objectiveLocation.set(state.getUiObjective().getObjectives().get(0).getObjectiveLocation()).scl(1 / PPM);
            if (state.getUiObjective().getObjectives().get(0).getObjectiveTarget() instanceof Hitbox flag) {
                if (flag.getStrategies().size() >= 2) {

                    //this is kinda sketchy code that relies on the fact that capturable flags are only created in 1 place
                    //and that they are always added as the second strategy
                    if (flag.getStrategies().get(1) instanceof FlagHoldable capture) {
                        if (capture.isCaptured()) {
                            if (p.equals(capture.getTarget())) {
                                p.getBotController().getPointPath().clear();
                                p.getBotController().setCurrentMood(BotController.BotMood.WANDER);
                                return null;
                            }
                        }
                        RallyPath tempPath = BotManager.getShortestPathBetweenLocations(p, playerLocation, objectiveLocation, playerVelocity);
                        if (tempPath != null) {
                            p.getBotController().setEventTarget(flag);
                            return new RallyPath(tempPath.getPath(), tempPath.getDistance() * crownDesireMultiplier);
                        }
                    }
                }
            }
        }
        return null;
    }
}
