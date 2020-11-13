package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import java.util.ArrayList;

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

    private final ArrayList<KillFeedMessage> messages = new ArrayList<>();
    private final ArrayList<KillFeedMessage> removeMessages = new ArrayList<>();

    public KillFeed(PlayState ps) {
        this.ps = ps;

        this.feed = new VerticalGroup() {

            @Override
            public void act(float delta) {

                if (messages.size() > maxMessages) {
                    feed.removeActor(messages.get(0));
                    messages.remove(0);
                }

                for (KillFeedMessage message : messages) {
                    if (message.decrementLifespan(delta)) {
                        removeMessages.add(message);
                    }
                }

                for (KillFeedMessage message : removeMessages) {
                    feed.removeActor(message);
                    messages.remove(message);
                }
                removeMessages.clear();
            }
        };
        addTable();
    }

    public void addMessage(Schmuck perp, Player player, DamageTypes... tags) {
        KillFeedMessage message = new KillFeedMessage(ps, perp, player, tags);
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
