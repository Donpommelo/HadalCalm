package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.EnemyStrategy;
import com.mygdx.hadal.utils.PacketUtil;

/**
 * This strategy applies to "floating" moving behavior.
 * This just controls the direction the enemy faces and makes them rotate smoothly towards their desired targets.
 * This also controls things like bosses spinning.
 */
public class MovementFloat extends EnemyStrategy {

    //the speed that the boss spins when spinning
    private int spinSpeed;

    //this is the speed that a tracking enemy will rotate to face its target
    private float trackSpeed = 0.06f;

    //The boss's current state in terms of passive behavior (is it tracking the player, still, spinning etc)
    private FloatingState currentState;

    private final ObjectMap<MoveState, Sprite> sprites = new ObjectMap<>();

    private MoveState lastMoveState;

    //this is the boss's sprite
    private Animation<TextureRegion> floatingSprite;
    private boolean looping;

    public MovementFloat(PlayState state, Enemy enemy, Sprite sprite) {
        super(state, enemy);

        enemy.setAttackAngle(0);
        enemy.setDesiredAngle(0);

        this.currentState = FloatingState.TRACKING_PLAYER;

        addSprite(MoveState.DEFAULT, sprite);
    }

    private float floatCount;
    private final Vector2 entityWorldLocation = new Vector2();
    private final Vector2 targetWorldLocation = new Vector2();
    @Override
    public void controller(float delta) {

        floatCount += delta;
        while (floatCount >= Constants.INTERVAL) {
            floatCount -= Constants.INTERVAL;

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

        setFloatingSprite(enemy.getMoveState());

        entityWorldLocation.set(enemy.getPixelPosition());
        batch.draw(floatingSprite.getKeyFrame(enemy.getAnimationTime(), looping),
                (flip ? enemy.getSize().x : 0) + entityWorldLocation.x - enemy.getSize().x / 2,
                entityWorldLocation.y - enemy.getSize().y / 2,
                (flip ? -1 : 1) * enemy.getSize().x / 2,
                enemy.getSize().y / 2,
                (flip ? -1 : 1) * enemy.getSize().x, enemy.getSize().y, 1, 1,
                (flip ? 0 : 180) + MathUtils.radDeg * enemy.getAngle());
    }

    @Override
    public Object onServerSync() {
        if (enemy.getBody() != null) {
            return new PacketsSync.SyncSchmuckAngled(enemy.getEntityID(), enemy.getPosition(), enemy.getLinearVelocity(),
                    state.getTimer(), enemy.getMoveState(),
                    PacketUtil.percentToByte(enemy.getBodyData().getCurrentHp() / enemy.getBodyData().getStat(Stats.MAX_HP)),
                    enemy.getAngle());
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

    public void setFloatingSprite(MoveState moveState) {
        if (lastMoveState == null || lastMoveState != moveState) {
            lastMoveState = moveState;
            Sprite newSprite = sprites.get(lastMoveState);

            if (null == newSprite) {
                newSprite = sprites.get(MoveState.DEFAULT);
            }

            if (null != newSprite && !Sprite.NOTHING.equals(newSprite)) {
                this.floatingSprite = SpriteManager.getAnimation(newSprite);
                this.floatingSprite.setPlayMode(newSprite.getPlayMode());

                if (!Animation.PlayMode.NORMAL.equals(newSprite.getPlayMode())) {
                    looping = true;
                }

                enemy.resetAnimationTime();
            }
        }
    }

    public void addSprite(MoveState moveState, Sprite sprite) {
        sprites.put(moveState, sprite);
    }

    public enum FloatingState {
        TRACKING_PLAYER,
        FREE,
        ROTATING,
        SPINNING
    }
}
