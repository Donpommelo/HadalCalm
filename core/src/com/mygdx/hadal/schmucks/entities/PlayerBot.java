package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.bots.BotPersonality;
import com.mygdx.hadal.bots.BotPlayerController;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Botting;

/**
 * A PlayerBot is a player that is controlled by a bot
 * @author Morcester Mirmalade
 */
public class PlayerBot extends Player {

    //this manages all of the player's actions
    private final BotPlayerController botController;

    //bot's personality affects its difficulty and other quirks
    private final BotPersonality personality;

    //this is just a vector used for making the player's aim a set distance away from the target
    //at the moment, this just rotates in a circle around the target when the bot is using the cola cannon
    private final Vector2 aimWobble = new Vector2(1, 0);

    public PlayerBot(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
                     int connID, User user, boolean reset, StartPoint start) {
        super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
        this.botController = new BotPlayerController(this);
        this.personality = new BotPersonality(state.getMode().getBotDifficulty());
    }

    @Override
    public void create() {
        super.create();
        getBodyData().addStatus(new Botting(state, this));
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

    public BotPlayerController getBotController() { return botController; }

    public Vector2 getAimWobble() { return aimWobble; }

    public float getMouseAimSpeed() { return personality.getMouseAimSpeed(); }

    public float getVisionX() { return personality.getVisionX(); }

    public float getVisionY() { return personality.getVisionY(); }

    public float getBoostDesireMultiplier() { return personality.getBoostDesireMultiplier(); }

    public float getWeaponDesireMultiplier() { return personality.getWeaponDesireMultiplier(); }

    public float getHealthDesireMultiplier() { return personality.getHealthDesireMultiplier(); }

    public float getViolenceDesireMultiplier() { return personality.getViolenceDesireMultiplier(); }

    public float getChatWheelDesire() { return personality.getChatWheelDesire(); }

}
