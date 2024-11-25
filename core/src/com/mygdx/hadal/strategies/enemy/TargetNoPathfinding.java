package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.strategies.EnemyStrategy;
import com.mygdx.hadal.utils.WorldUtil;

/**
 * This strategy is used by enemies who do not use bot pathfinding to find targets (bosses, maps with no pathfinding points)
 * This just raycasts at nearby enemies to choose a target
 */
public class TargetNoPathfinding extends EnemyStrategy {

    //This is the range that the enemy will be able to detect targets
    protected static final float AI_RADIUS = 2000;

    //These are used for raycasting to determining whether the player is in vision of the enemy.
    private float shortestFraction;
    private Schmuck homeAttempt;
    private Fixture closestFixture;

    //can the enemy find targets through walls? True for bosses
    private final boolean trackThroughWalls;

    public TargetNoPathfinding(PlayState state, Enemy enemy, boolean trackThroughWalls) {
        super(state, enemy);
        this.trackThroughWalls = trackThroughWalls;
    }

    private final Vector2 homeLocation = new Vector2();
    private final Vector2 entityWorldLocation = new Vector2();
    @Override
    public void acquireTarget() {
        enemy.setAttackTarget(null);
        enemy.setApproachTarget(false);

        entityWorldLocation.set(enemy.getPosition());
        //query nearby units
        enemy.getWorld().QueryAABB((fixture -> {
                    if (fixture.getUserData() instanceof final BodyData bodyData) {
                        homeAttempt = bodyData.getSchmuck();
                        homeLocation.set(homeAttempt.getPosition());
                        shortestFraction = 1.0f;

                        if (WorldUtil.preRaycastCheck(entityWorldLocation, homeLocation)) {
                            enemy.getWorld().rayCast((fixture1, point, normal, fraction) -> {
                                if (fixture1.getFilterData().categoryBits == BodyConstants.BIT_WALL && !trackThroughWalls) {
                                    if (fraction < shortestFraction) {
                                        shortestFraction = fraction;
                                        closestFixture = fixture1;
                                        return fraction;
                                    }
                                } else if (fixture1.getUserData() instanceof final BodyData bodyData2) {
                                    if (bodyData2.getSchmuck().getHitboxFilter() != enemy.getHitboxFilter()) {
                                        if (fraction < shortestFraction) {

                                            //enemies will not see invisible units
                                            if (bodyData2.getStatus(Invisibility.class) == null) {
                                                shortestFraction = fraction;
                                                closestFixture = fixture1;
                                                return fraction;
                                            }
                                        }
                                    }
                                }
                                return -1.0f;
                            }, entityWorldLocation, homeLocation);
                            if (closestFixture != null) {
                                if (closestFixture.getUserData() instanceof BodyData targetData) {
                                    enemy.setAttackTarget(targetData.getSchmuck());
                                    enemy.setApproachTarget(true);
                                }
                            }
                        }
                    }
                    return true;
                }), entityWorldLocation.x - AI_RADIUS, entityWorldLocation.y - AI_RADIUS,
                entityWorldLocation.x + AI_RADIUS, entityWorldLocation.y + AI_RADIUS);
    }
}
