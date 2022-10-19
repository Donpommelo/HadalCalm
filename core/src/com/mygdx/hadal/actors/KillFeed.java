package com.mygdx.hadal.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.PlayerSpriteHelper;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.mygdx.hadal.constants.Constants.*;

/**
 * The Kill Feed is located in the upper right corner of the screen and tracks the kill messages created when players die.
 * This also keeps track of notificationis which are displayed similarly in the top center of the screen
 * This also keeps track of the respawn time window that pops up on the left side of the screen when respawning
 * @author Mukins Murfbort
 */
public class KillFeed {

    private static final int TABLE_X = 10;
    private static final int TABLE_Y = 10;

    private static final int NOTIFICATION_Y = 25;

    private static final int MAX_MESSAGES = 5;
    private static final int MESSAGE_PAD = 12;

    private static final float KILL_FEED_WIDTH = 325;
    private static final int KILL_FEED_HEIGHT = 300;

    private static final int MAX_NOTIFICATIONS = 3;
    private static final int NOTIFICATION_PAD = 12;

    private static final float NOTIFICATION_WIDTH = 500;
    private static final float NOTIFICATION_HEIGHT = 300;

    private static final int DEATH_INFO_X = -460;
    private static final int DEATH_INFO_Y = 270;
    private static final int DEATH_INFO_X_ENABLED = 0;
    private static final int DEATH_INFO_Y_ENABLED = 270;
    private static final int DEATH_INFO_WIDTH = 400;
    private static final int DEATH_INFO_HEIGHT = 300;
    private static final int DEATH_INFO_HEIGHT_SHORT = 180;
    private static final int DEATH_INFO_PAD = 15;

    private static final int KILLER_WIDTH = 200;
    private static final int KILLER_HEIGHT = 180;
    private static final float KILLER_SCALE = 0.55f;
    private static final Vector2 KILLER_OFFSET = new Vector2(100, -50);

    private static final float SCALE = 0.25f;

    //Reference to the gsm. Used to reference gsm fields.
    private final PlayState ps;
    private final VerticalGroup feed, notification;
    private Table deathInfoTable;
    private Table killerPortrait;
    private ScrollPane portrait;
    private HubOptionPlayer killerBustSprite;

    //messages displayed in the feed
    private final Array<KillFeedMessage> messages = new Array<>();

    //messages to be removed from the feed
    private final Array<KillFeedMessage> removeMessages = new Array<>();

    //notifications displayed in the feed
    private final Array<KillFeedMessage> notifications = new Array<>();

    //notifications to be removed from the feed
    private final Array<KillFeedMessage> removeNotifications = new Array<>();

    //this contains information shown when waiting to respawn
    private Text deathInfo;
    private boolean awaitingRevive;
    private Player killerPerp;
    private String killedBy = "";
    private String deathCause = "";

    public KillFeed(PlayState ps) {
        this.ps = ps;

        this.feed = new VerticalGroup() {

            @Override
            public void act(float delta) {

                //if there are too many messages, we remove the oldest one.
                if (messages.size > MAX_MESSAGES) {
                    feed.removeActor(messages.get(0));
                    messages.removeIndex(0);
                }

                //kill feed messages naturally go away after enough time passes
                for (KillFeedMessage message : messages) {
                    if (message.decrementLifespan(delta)) {
                        removeMessages.add(message);
                    }
                }

                //remove messages like this to avoid concurrent modification
                for (KillFeedMessage message : removeMessages) {
                    feed.removeActor(message);
                    messages.removeValue(message, false);
                }
                removeMessages.clear();
            }
        };

        this.notification = new VerticalGroup() {

            @Override
            public void act(float delta) {

                //if there are too many messages, we remove the oldest one.
                if (notifications.size > MAX_NOTIFICATIONS) {
                    notification.removeActor(notifications.get(0));
                    notifications.removeIndex(0);
                }

                //kill feed messages naturally go away after enough time passes
                for (KillFeedMessage message : notifications) {
                    if (message.decrementLifespan(delta)) {
                        removeNotifications.add(message);
                    }
                }

                //remove messages like this to avoid concurrent modification
                for (KillFeedMessage message : removeNotifications) {
                    notification.removeActor(message);
                    notifications.removeValue(message, false);
                }
                removeMessages.clear();
            }
        };

        addTable();
        initialNotification();
    }

    /**
     * This adds a single kill message to the feed
     * @param perp: the killer (if it is a player. Otherwise, this is null)
     * @param vic: The victim
     * @param type: if the victim died to an enemy, this is the enemy type (null otherwise)
     * @param source: damage source of the killing instance of damage
     * @param tags: damage tags of the killing instance of damage
     */
    public void addMessage(Player perp, Player vic, EnemyType type, DamageSource source, DamageTag... tags) {
        KillFeedMessage message = new KillFeedMessage(ps, perp, vic, type, source, tags);
        messages.add(message);
        feed.addActor(message);

        if (null != perp) {
            if (perp.equals(perp.getState().getPlayer()) && perp != vic) {
                String vicName = WeaponUtils.getPlayerColorName(vic, MAX_NAME_LENGTH);
                addNotification(UIText.YOU_HAVE_SLAIN.text(vicName), false);
            }
        }
        if (vic == ps.getPlayer()) {
            setKillSource(perp, type, source);
        }
    }

    /**
     * Adds a notification to the center messages.
     * @param text: string notification to be displayed
     * @param global: does this notification play for all players?
     */
    public void addNotification(String text, boolean global) {
        KillFeedMessage message = new KillFeedMessage(text, false);
        notifications.add(message);
        notification.addActor(message);

        if (global && ps.isServer()) {
            HadalGame.server.sendToAllTCP(new Packets.SyncNotification(text));
        }
    }

    /**
     * This is run by the server to send a notification to a specific client
     * @param text: the string to be displayed
     * @param player: the client to receive the notification
     */
    public void sendNotification(String text, Player player) {
        if (!ps.getPlayer().equals(player)) {
            HadalGame.server.sendToTCP(player.getConnID(), new Packets.SyncNotification(text));
        } else {
            addNotification(text, false);
        }
    }

    /**
     * This creates the table and sets its properties.
     */
    public void addTable() {
        final KillFeed me = this;
        ps.getStage().addActor(feed);

        feed.space(MESSAGE_PAD);
        feed.top();
        feed.setWidth(KILL_FEED_WIDTH);
        feed.setHeight(KILL_FEED_HEIGHT);
        feed.setPosition(HadalGame.CONFIG_WIDTH - TABLE_X - KILL_FEED_WIDTH,
                HadalGame.CONFIG_HEIGHT - TABLE_Y - KILL_FEED_HEIGHT);

        ps.getStage().addActor(notification);

        notification.space(NOTIFICATION_PAD);
        notification.top();
        notification.setWidth(NOTIFICATION_WIDTH);
        notification.setHeight(NOTIFICATION_HEIGHT);
        notification.setPosition(HadalGame.CONFIG_WIDTH / 2 - NOTIFICATION_WIDTH / 2,
                HadalGame.CONFIG_HEIGHT - NOTIFICATION_Y - NOTIFICATION_HEIGHT);

        deathInfoTable = new TableWindow() {

            @Override
            public void act(float delta) {
                super.act(delta);

                //increment displayed respawn time (does not actually control respawn, but should be same number)
                if (0.0f < respawnTime) {
                    respawnTime -= delta;

                    formatDeathTimer();

                    if (0.0f >= respawnTime) {
                        deathInfoTable.addAction(Actions.sequence(
                                Actions.moveTo(DEATH_INFO_X, DEATH_INFO_Y, TRANSITION_DURATION, INTP_FASTSLOW),
                                Actions.run(me::clearKillerBustSprite)));
                        deathInfoTable.setVisible(false);
                    }
                }
            }
        };
        deathInfoTable.setPosition(DEATH_INFO_X, DEATH_INFO_Y);
        deathInfoTable.setSize(DEATH_INFO_WIDTH, DEATH_INFO_HEIGHT);
        ps.getStage().addActor(deathInfoTable);

        deathInfo = new Text("");
        deathInfo.setScale(SCALE);

        killerPortrait = new TableWindow();
        killerPortrait.setSize(KILLER_WIDTH, KILLER_HEIGHT);

        portrait = new ScrollPane(killerPortrait, GameStateManager.getSkin());
        portrait.setFadeScrollBars(true);
        portrait.setScrollingDisabled(true, true);
    }

    /**
     * This is run upon starting a match with mode modifiers.
     * It displays all modifiers
     */
    private void initialNotification() {
        for (String notif : ps.getMode().getInitialNotifications()) {
            addNotification(notif, false);
        }
    }

    private float totalRespawnTime;
    private float respawnTime;
    /**
     * This is run upon dying if respawning
     * @param respawnTime: The amount of time it takes before respawning
     */
    public void addKillInfo(float respawnTime) {
        deathInfoTable.clear();
        if (!killedBy.isEmpty() && null != killerPerp) {
            deathInfoTable.setHeight(DEATH_INFO_HEIGHT);
        } else {
            deathInfoTable.setHeight(DEATH_INFO_HEIGHT_SHORT);
        }
        this.totalRespawnTime = respawnTime;
        this.respawnTime = respawnTime;

        awaitingRevive = respawnTime < 0.0f;

        Text deathInfoTitle = new Text("");
        if (awaitingRevive) {
            deathInfoTitle.setText(UIText.AWAITING_REVIVE.text());
        } else {
            deathInfoTitle.setText(UIText.RESPAWN_IN.text());
        }
        deathInfoTitle.setScale(SCALE);

        deathInfoTable.add(deathInfoTitle);
        deathInfoTable.add(deathInfo).pad(DEATH_INFO_PAD).row();

        if (!killedBy.isEmpty()) {
            Text deathPerpTitle = new Text(UIText.KILLED_BY.text());
            deathPerpTitle.setScale(SCALE);

            Text deathPerp = new Text(killedBy);
            deathPerp.setScale(SCALE);

            deathInfoTable.add(deathPerpTitle);
            deathInfoTable.add(deathPerp).pad(DEATH_INFO_PAD).row();

            //if there is a player killer, get their character bust to display in kill feed
            if (null != killerPerp) {
                killerBustSprite = new HubOptionPlayer("", killerPerp,
                        killerPerp.getPlayerData().getLoadout().character,
                        killerPerp.getPlayerData().getLoadout().team,
                        true, null, KILLER_SCALE);
                killerBustSprite.setOptionWidth(KILLER_WIDTH).setOptionHeight(KILLER_HEIGHT);
                killerBustSprite.setAttackAngle(150.0f).setMoveState(MoveState.MOVE_RIGHT)
                        .setPlayerOffset(KILLER_OFFSET).setBob(false);

                deathInfoTable.add(portrait).width(KILLER_WIDTH).height(KILLER_HEIGHT).colspan(2).row();

                killerPortrait.clear();
                killerPortrait.add(killerBustSprite);
            }
        }

        //Display death source in ui
        if (!deathCause.isEmpty()) {
            Text deathSourceTitle = new Text(UIText.DEATH_CAUSE.text());
            deathSourceTitle.setScale(SCALE);

            Text deathSource = new Text(deathCause);
            deathSource.setScale(SCALE);

            deathInfoTable.add(deathSourceTitle);
            deathInfoTable.add(deathSource).pad(DEATH_INFO_PAD).row();
        }

        deathInfoTable.setVisible(true);

        deathInfoTable.addAction(Actions.sequence(
                Actions.moveTo(DEATH_INFO_X, DEATH_INFO_Y, TRANSITION_DURATION, INTP_FASTSLOW),
                Actions.run(this::formatDeathTimer),
                Actions.moveTo(DEATH_INFO_X_ENABLED, DEATH_INFO_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW)));
    }

    /**
     * format respawn timer to have 2 digits
     */
    private void formatDeathTimer() {
        if (awaitingRevive) {
            deathInfo.setText("");
            return;
        }
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.DOWN);
        deathInfo.setText(df.format(respawnTime) + " S");
    }

    //when this actor is removed, we want to dispose of the character bust in the kill feed to avoid memory leak
    private void clearKillerBustSprite() {
        if (null != killerBustSprite) {
            killerBustSprite.getPlayerSpriteHelper().dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
        }
    }

    //this is the number of seconds before the player can enter spectator mode (to avoid accidentally transitions)
    private static final float SPECTATOR_DURATION_THRESHOLD = 2.0f;
    /**
     * This keeps track of whether the game is currently in either spectator mode, or respawning (with some delay after death)
     * This is used to determine if spectator camera features should be active (camera controls, screen shake)
     */
    public boolean isRespawnSpectator() {
        return (respawnTime < totalRespawnTime - SPECTATOR_DURATION_THRESHOLD || awaitingRevive) && deathInfoTable.isVisible();
    }

    /**
     * This is called when the player dies. It adds information to the kill screen to inform them of the perp and
     * cause of death
     */
    public void setKillSource(Player perp, EnemyType type, DamageSource source) {
        killedBy = "";
        killerPerp = null;
        if (null != perp) {
            killerPerp = perp;
            if (perp.equals(ps.getPlayer())) {
                killedBy = UIText.DEATH_CAUSE_YOURSELF.text();
            } else {
                killedBy = perp.getName();
            }
        }
        if (null != type) {
            killedBy = type.getName();
        }
        this.deathCause = source.getKillSource();
    }
}
