package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import java.util.ArrayList;

/**
 * The Kill Feed is located in the upper right corner of the screen and tracks the kill messages created when players die.
 * @author Mukins Murfbort
 */
public class KillFeed {

    //Reference to the gsm. Used to reference gsm fields.
    private final PlayState ps;
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

    //messages displayed in the feed
    private final ArrayList<KillFeedMessage> messages = new ArrayList<>();

    //messages to be removed from the feed
    private final ArrayList<KillFeedMessage> removeMessages = new ArrayList<>();

    //notifications displayed in the feed
    private final ArrayList<KillFeedMessage> notifications = new ArrayList<>();

    //notifications to be removed from the feed
    private final ArrayList<KillFeedMessage> removeNotifications = new ArrayList<>();

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
    }

    private static final int maxNameLength = 25;
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
                String vicName = WeaponUtils.getPlayerColorName(vic, maxNameLength);
                addNotification("YOU HAVE SLAIN " + vicName, false);
            }
        }
    }

    /**
     * Adds a notification to the kill feed.
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

    public void sendNotification(String text, Player player) {
        if (!ps.getPlayer().equals(player)) {
            User user = HadalGame.server.getUsers().get(player.getConnID());
            if (user != null) {
                HadalGame.server.sendToTCP(player.getConnID(), new Packets.SyncNotification(text));
            }
        } else {
            addNotification(text, false);
        }
    }

    /**
     * This simply creates the table and sets its properties.
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
    }
}
