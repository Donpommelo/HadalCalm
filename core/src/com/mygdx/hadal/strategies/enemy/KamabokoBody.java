package com.mygdx.hadal.strategies.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyCrawling;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathParticles;
import com.mygdx.hadal.strategies.EnemyStrategy;

public class KamabokoBody extends EnemyStrategy {

    private static final int smileOffset = 200;
    private static final float scale = 0.25f;

    private final TextureRegion faceSprite;
    private final boolean rotate;

    public KamabokoBody(PlayState state, Enemy enemy, boolean rotate) {
        super(state, enemy);
        this.rotate = rotate;
        faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(MathUtils.random(4));
    }

    @Override
    public void create() {
        enemy.getBodyData().addStatus(new DeathParticles(state, enemy.getBodyData(), Particle.KAMABOKO_IMPACT, 1.0f));
    }

    private final Vector2 entityLocation = new Vector2();
    @Override
    public void render(SpriteBatch batch, float animationTime) {
        entityLocation.set(enemy.getPixelPosition());

        if (rotate) {
            boolean flip = true;
            float realAngle = enemy.getAngle() % (MathUtils.PI * 2);
            if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
                flip = false;
            }

            batch.draw(faceSprite,
                    (flip ? enemy.getSize().x : 0) + entityLocation.x - enemy.getSize().x / 2,
                    entityLocation.y - enemy.getSize().y / 2,
                    (flip ? -1 : 1) * enemy.getSize().x / 2,
                    enemy.getSize().y / 2,
                    (flip ? -1 : 1) * enemy.getSize().x, enemy.getSize().y, 1, 1,
                    (flip ? 0 : 180) + MathUtils.radDeg * enemy.getAngle());
        } else {
            if (enemy instanceof EnemyCrawling crawler) {
                boolean flip = crawler.getMoveDirection() < 0;
                batch.draw(faceSprite,
                        (flip ? 0 : enemy.getSize().x) + entityLocation.x - enemy.getSize().x / 2,
                        entityLocation.y - enemy.getHboxSize().y / 2 - smileOffset * scale,
                        enemy.getSize().x / 2,
                        (flip ? 1 : -1) * enemy.getSize().y / 2,
                        (flip ? 1 : -1) * enemy.getSize().x, enemy.getSize().y, 1, 1, 0);
            }
        }
    }
}
