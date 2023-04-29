package com.mygdx.hadal.strategies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;

public interface IEnemyStrategy {

    void create();

    void controller(float delta);

    void render(SpriteBatch batch, float animationTime);

    void acquireTarget();

    void setRallyEvent(HadalEntity entity, float moveSpeed);

    void die(BodyData perp, DamageSource source);

    Vector2 getProjectileOrigin(Vector2 startVelo, float projSize);

    Object onServerSync();
}
