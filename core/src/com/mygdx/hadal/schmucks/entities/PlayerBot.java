package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.bots.BotControllerPlayer;
import com.mygdx.hadal.bots.BotPersonality;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Botting;

/**
 * A PlayerBot is a player that is controlled by a bot
 * @author Morcester Mirmalade
 */
public class PlayerBot extends Player {

    //this manages all of the player's actions
    private final BotControllerPlayer botController;

    //bot's personality affects its difficulty and other quirks
    private final BotPersonality personality;

    //this is just a vector used for making the player's aim a set distance away from the target
    //at the moment, this just rotates in a circle around the target when the bot is using the cola cannon
    //this is also used to make the bot's aim worse when on low difficulty
    private final Vector2 weaponWobble = new Vector2(1, 0);
    private final Vector2 aimWobble = new Vector2(1, 0);
    private float currentWobble;

    public PlayerBot(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
                     User user, boolean reset, Event start) {
        super(state, startPos, name, startLoadout, oldData, user, reset, start);
        this.botController = new BotControllerPlayer(this);
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

    private static final float MAX_WOBBLE = 25.0f;
    private static final float WOBBLE_SPEED = 45.0f;
    private static final float AIM_WOBBLE_SPEED = 15.0f;
    /**
     * This is run for weapon-caused wobbles (to charge cola cannon)
     */
    public void weaponWobble() {
        weaponWobble.nor().scl(MAX_WOBBLE);
        weaponWobble.setAngleDeg(weaponWobble.angleDeg() + WOBBLE_SPEED);
    }

    /**
     * This is run for aim-caused wobbles (for bot difficulty settings)
     */
    public void aimWobble() {
        aimWobble.nor().scl(currentWobble);
        aimWobble.setAngleDeg(aimWobble.angleDeg() + AIM_WOBBLE_SPEED);
    }

    /**
     * When not aiming at a target, bot's aim wobble gradually increases to a max amount (depending on their aim)
     */
    public void incrementWeaponWobble(float delta) {
        currentWobble = Math.min(personality.getWobbleMax(), currentWobble + personality.getWobbleIncrement() * delta);
    }

    /**
     * When aiming at a target, bot's aim wobble gradually decreases to a max amount (depending on their aim)
     */
    public void decrementWeaponWobble(float delta) {
        currentWobble = Math.max(personality.getWobbleMin(), currentWobble - personality.getWobbleDecrement() * delta);
    }

    /**
     * Bot's aim wobble is reset to max amount when they start aiming towards a new target
     */
    public void resetWeaponWobble() {
        currentWobble = personality.getWobbleMax();
    }

//    private final Vector2 playerLocation = new Vector2();
//    @Override
//    public void render(SpriteBatch batch) {
//        super.render(batch);
//        playerLocation.set(getPixelPosition());
//
//        HadalGame.FONT_SPRITE.draw(batch, botController.getCurrentMood().toString() + " " + botController.getEventTarget() + " " +
//                botController.getPointPath().size, playerLocation.x + 20, playerLocation.y + 20);
//
//    }
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

    public BotControllerPlayer getBotController() { return botController; }

    public Vector2 getWeaponWobble() { return weaponWobble; }

    public Vector2 getAimWobble() { return aimWobble; }

    public float getCurrentWobble() { return currentWobble; }

    public float getMouseAimSpeed() { return personality.getMouseAimSpeed(); }

    public float getVisionX() { return personality.getVisionX(); }

    public float getVisionY() { return personality.getVisionY(); }

    public float getBoostDesireMultiplier() { return personality.getBoostDesireMultiplier(); }

    public float getWeaponDesireMultiplier() { return personality.getWeaponDesireMultiplier(); }

    public float getHealthDesireMultiplier() { return personality.getHealthDesireMultiplier(); }

    public float getViolenceDesireMultiplier() { return personality.getViolenceDesireMultiplier(); }

    public float getChatWheelDesire() { return personality.getChatWheelDesire(); }
}
