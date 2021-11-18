package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.bots.BotController;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

/**
 * A PlayerBot is a player that is controlled by a bot
 * @author Morcester Mirmalade
 */
public class PlayerBot extends Player {

    //this manages all of the player's actions
    private final BotController botController;

    //this is just a vector used for making the player's aim a set distance away from the target
    //at the moment, this just rotates in a circle around the target when the bot is using thte cola cannon
    private final Vector2 aimWobble = new Vector2(1, 0);

    public PlayerBot(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
                     int connID, User user, boolean reset, StartPoint start) {
        super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
        this.botController = new BotController(this);
    }

    @Override
    public void controller(float delta) {
        botController.processBotAI(delta);
        super.controller(delta);
    }

//    private static final ShapeRenderer debugRenderer = new ShapeRenderer();
//    @Override
//    public void render(SpriteBatch batch) {
//        super.render(batch);
//
//        RallyPoint lastPoint = null;
//        for (RallyPoint point: botController.getPointPath()) {
//            if (lastPoint != null) {
//                Gdx.gl.glLineWidth(2);
//                batch.end();
//                debugRenderer.setProjectionMatrix(state.getCamera().combined);
//                debugRenderer.begin(ShapeRenderer.ShapeType.Line);
//                debugRenderer.setColor(Color.CYAN);
//                debugRenderer.line(new Vector2(lastPoint.getPosition()).scl(32), new Vector2(point.getPosition()).scl(32));
//                debugRenderer.end();
//                batch.begin();
//                Gdx.gl.glLineWidth(1);
//            }
//            lastPoint = point;
//        }
//        batch.end();
//        debugRenderer.setProjectionMatrix(state.getCamera().combined);
//        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
//        debugRenderer.setColor(Color.CYAN);
//        debugRenderer.circle(getMouse().getPixelPosition().x, getMouse().getPixelPosition().y, 50);
//        debugRenderer.end();
//        batch.begin();
//    }

    public BotController getBotController() { return botController; }

    public Vector2 getAimWobble() { return aimWobble; }
}
