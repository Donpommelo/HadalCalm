package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
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
    private final VerticalGroup feed;

    private static final int tableX = 10;
    private static final int tableY = 10;

    private static final int tableWidth= 325;
    private static final int tableHeight = 300;

    private static final int maxMessages = 5;
    private static final int messagePadding = 12;

    //messages displayed in the feed
    private final ArrayList<KillFeedMessage> messages = new ArrayList<>();

    //messages to be removed from the feed
    private final ArrayList<KillFeedMessage> removeMessages = new ArrayList<>();

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
        addTable();
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
    }

    public void addTable() {
        ps.getStage().addActor(feed);

        feed.space(messagePadding);
        feed.top();
        feed.setWidth(tableWidth);
        feed.setHeight(tableHeight);
        feed.setPosition(HadalGame.CONFIG_WIDTH - tableX - tableWidth, HadalGame.CONFIG_HEIGHT - tableY - tableHeight);
    }
}
