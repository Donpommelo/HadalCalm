package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.text.HText;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.mygdx.hadal.utils.Constants.*;

/**
 * The Kill Feed is located in the upper right corner of the screen and tracks the kill messages created when players die.
 * @author Mukins Murfbort
 */
public class KillFeed {

    //Reference to the gsm. Used to reference gsm fields.
    private final PlayState ps;
    private Table deathInfoTable;
    private final VerticalGroup feed, notification;

    private static final int tableX = 10;
    private static final int tableY = 10;

    private static final int notificationY = 25;

    private static final int maxMessages = 5;
    private static final int messagePadding = 12;

    private static final float killFeedWidth = 325;
    private static final int killFeedHeight = 300;

    private static final int maxNotifications = 3;
    private static final int notificationPadding = 12;

    private static final float notificationWidth = 500;
    private static final float notificationHeight = 300;

    private static final int deathInfoX = -460;
    private static final int deathInfoY = 330;
    private static final int deathInfoXEnabled = 0;
    private static final int deathInfoYEnabled = 330;
    private static final int deathInfoWidth = 200;
    private static final int deathInfoHeight = 150;

    private static final float scale = 0.4f;
    private static final float scaleSide = 0.25f;

    //messages displayed in the feed
    private final ArrayList<KillFeedMessage> messages = new ArrayList<>();

    //messages to be removed from the feed
    private final ArrayList<KillFeedMessage> removeMessages = new ArrayList<>();

    //notifications displayed in the feed
    private final ArrayList<KillFeedMessage> notifications = new ArrayList<>();

    //notifications to be removed from the feed
    private final ArrayList<KillFeedMessage> removeNotifications = new ArrayList<>();

    //this contains information shown when waiting to respawn
    private Text deathInfo;

    public KillFeed(PlayState ps) {
        this.ps = ps;

        this.feed = new VerticalGroup() {

            @Override
            public void act(float delta) {

                //if there are too many messages, we remove the oldest one.
                if (messages.size() > maxMessages) {
                    feed.removeActor(messages.get(0));
                    messages.remove(0);
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
                    messages.remove(message);
                }
                removeMessages.clear();
            }
        };

        this.notification = new VerticalGroup() {

            @Override
            public void act(float delta) {

                //if there are too many messages, we remove the oldest one.
                if (notifications.size() > maxNotifications) {
                    notification.removeActor(notifications.get(0));
                    notifications.remove(0);
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
                    notifications.remove(message);
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
     * @param tags: damage tags of the killing instance of damage
     */
    public void addMessage(Player perp, Player vic, EnemyType type, DamageTypes... tags) {
        KillFeedMessage message = new KillFeedMessage(ps, perp, vic, type, tags);
        messages.add(message);
        feed.addActor(message);

        if (perp != null) {
            if (perp.equals(perp.getState().getPlayer()) && perp != vic) {
                String vicName = WeaponUtils.getPlayerColorName(vic, MAX_NAME_LENGTH);
                addNotification(HText.YOU_HAVE_SLAIN.text(vicName), false);
            }
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
            HadalGame.server.sendToTCP(player.getConnId(), new Packets.SyncNotification(text));
        } else {
            addNotification(text, false);
        }
    }

    /**
     * This creates the table and sets its properties.
     */
    public void addTable() {
        ps.getStage().addActor(feed);

        feed.space(messagePadding);
        feed.top();
        feed.setWidth(killFeedWidth);
        feed.setHeight(killFeedHeight);
        feed.setPosition(HadalGame.CONFIG_WIDTH - tableX - killFeedWidth, HadalGame.CONFIG_HEIGHT - tableY - killFeedHeight);

        ps.getStage().addActor(notification);

        notification.space(notificationPadding);
        notification.top();
        notification.setWidth(notificationWidth);
        notification.setHeight(notificationHeight);
        notification.setPosition(HadalGame.CONFIG_WIDTH / 2 - notificationWidth / 2, HadalGame.CONFIG_HEIGHT - notificationY - notificationHeight);

        deathInfoTable = new WindowTable() {

            @Override
            public void act(float delta) {
                super.act(delta);

                //increment displayed respawn time (does not actually control respawn, but should be same number)
                if (respawnTime > 0.0f) {
                    respawnTime -= delta;

                    formatDeathTimer();

                    if (respawnTime <= 0.0f) {
                        deathInfoTable.addAction(Actions.moveTo(deathInfoX, deathInfoY, TRANSITION_DURATION, INTP_FASTSLOW));
                        deathInfoTable.setVisible(false);
                    }
                }
            }
        };
        deathInfoTable.setPosition(deathInfoX, deathInfoY);
        deathInfoTable.setSize(deathInfoWidth, deathInfoHeight);
        ps.getStage().addActor(deathInfoTable);

        deathInfo = new Text("", 0, 0, false);
        deathInfo.setScale(scale);
    }

    /**
     * This is run upon starting a match with mode modifiers.
     * It displays all modifiers
     */
    private void initialNotification() {
        for (String notif: ps.getMode().getInitialNotifications()) {
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
        this.totalRespawnTime = respawnTime;
        this.respawnTime = respawnTime;

        Text deathInfoTitle = new Text(HText.RESPAWN_IN.text(), 0, 0, false);
        deathInfoTitle.setScale(scaleSide);
        deathInfoTable.add(deathInfoTitle).row();
        deathInfoTable.add(deathInfo);
        deathInfoTable.setVisible(true);

        deathInfoTable.addAction(Actions.sequence(
                Actions.moveTo(deathInfoX, deathInfoY, TRANSITION_DURATION, INTP_FASTSLOW),
                Actions.run(this::formatDeathTimer),
                Actions.moveTo(deathInfoXEnabled, deathInfoYEnabled, TRANSITION_DURATION, INTP_FASTSLOW)));
    }

    /**
     * format respawn timer to have 2 digits
     */
    private void formatDeathTimer() {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.DOWN);
        deathInfo.setText(df.format(respawnTime) + " S");
    }

    private static final float spectatorDurationThreshold = 1.5f;
    public boolean isRespawnSpectator() {
        return respawnTime < totalRespawnTime - spectatorDurationThreshold && deathInfoTable.isVisible();
    }
}
