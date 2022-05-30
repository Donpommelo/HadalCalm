package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class MovementFloat extends EnemyStrategy {

    //the speed that the boss spins when spinning
    private int spinSpeed;

    //this is the speed that a tracking enemy will rotate to face its target
    private float trackSpeed = 0.04f;

    //The boss's current state in terms of passive behavior (is it tracking the player, still, spinning etc)
    private FloatingState currentState;

    //this is the boss's sprite
    private Animation<TextureRegion> floatingSprite;

    public MovementFloat(PlayState state, Enemy enemy, Sprite sprite) {
        super(state, enemy);

        enemy.setAttackAngle(0);
        enemy.setDesiredAngle(0);

        this.currentState = FloatingState.TRACKING_PLAYER;

        if (!sprite.equals(Sprite.NOTHING)) {
            this.floatingSprite = new Animation<>(PlayState.spriteAnimationSpeedFast, sprite.getFrames());
            this.floatingSprite.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        }
    }

    private float floatCount;
    private static final float pushInterval = 1 / 60f;
    private final Vector2 entityWorldLocation = new Vector2();
    private final Vector2 targetWorldLocation = new Vector2();
    @Override
    public void controller(float delta) {

        floatCount += delta;
        while (floatCount >= pushInterval) {
            floatCount -= pushInterval;

            //lerp towards desired angle
            float dist = (enemy.getDesiredAngle() - enemy.getAttackAngle()) % 360;
            enemy.setAttackAngle(enemy.getAttackAngle() + (2 * dist % 360 - dist) * trackSpeed);

            //when spinning, spin at a constant speed. When tracking, set desired angle to face player
            switch (currentState) {
                case ROTATING:
                    enemy.setDesiredAngle(enemy.getDesiredAngle() + spinSpeed);
                    break;
                case SPINNING:
                    enemy.setAttackAngle(enemy.getAttackAngle() + spinSpeed);
                    break;
                case TRACKING_PLAYER:
                    //rotate towards attack target
                    if (enemy.getAttackTarget() != null) {
                        if (enemy.getAttackTarget().isAlive()) {
                            entityWorldLocation.set(enemy.getPosition());
                            targetWorldLocation.set(enemy.getAttackTarget().getPosition());
                            enemy.setDesiredAngle(MathUtils.atan2(
                                    targetWorldLocation.y - entityWorldLocation.y ,
                                    targetWorldLocation.x - entityWorldLocation.x) * 180 / MathUtils.PI);
                        }
                    } else {
                        //if there is no attack target, attempt to rotate towards movement target
                        if (enemy.getMoveTarget() != null) {
                            if (enemy.getMoveTarget().isAlive()) {
                                entityWorldLocation.set(enemy.getPosition());
                                targetWorldLocation.set(enemy.getMoveTarget().getPosition());

                                enemy.setDesiredAngle(MathUtils.atan2(
                                        targetWorldLocation.y - entityWorldLocation.y ,
                                        targetWorldLocation.x - entityWorldLocation.x) * 180 / MathUtils.PI);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            enemy.setAngle(enemy.getAttackAngle() * MathUtils.degRad);
        }
    }

    @Override
    public void render(SpriteBatch batch, float animationTime) {

        boolean flip = true;
        float realAngle = enemy.getAngle() % (MathUtils.PI * 2);
        if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
            flip = false;
        }

        entityWorldLocation.set(enemy.getPixelPosition());
        batch.draw(floatingSprite.getKeyFrame(animationTime, true),
                (flip ? enemy.getSize().x : 0) + entityWorldLocation.x - enemy.getSize().x / 2,
                entityWorldLocation.y - enemy.getSize().y / 2,
                (flip ? -1 : 1) * enemy.getSize().x / 2,
                enemy.getSize().y / 2,
                (flip ? -1 : 1) * enemy.getSize().x, enemy.getSize().y, 1, 1,
                (flip ? 0 : 180) + MathUtils.radDeg * enemy.getAngle());
    }

    @Override
    public Object onServerSync(float entityAge) {
        if (enemy.getBody() != null) {
            return new PacketsSync.SyncSchmuckAngled(enemy.getEntityID(), enemy.getPosition(), enemy.getLinearVelocity(),
                    entityAge, state.getTimer(), enemy.getMoveState(), enemy.getBodyData().getCurrentHp(), enemy.getAngle());
        }
        return null;
    }

    private final Vector2 originPt = new Vector2();
    private final Vector2 addVelo = new Vector2();
    @Override
    public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
        originPt.set(enemy.getPixelPosition()).add(addVelo.set(startVelo).nor().scl(enemy.getHboxSize().x / 2));
        return originPt;
    }

    public void setCurrentState(FloatingState currentState) { this.currentState = currentState; }

    public void setSpinSpeed(int spinSpeed) { this.spinSpeed = spinSpeed; }

    public void setTrackSpeed(float trackSpeed) { this.trackSpeed = trackSpeed; }

    public enum FloatingState {
        TRACKING_PLAYER,
        FREE,
        ROTATING,
        SPINNING
    }
}
