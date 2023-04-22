package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.entities.Player;

public class EventInteractHelper {

    private static final float INTERACT_CD = 0.15f;

    private final Player player;
    private float interactCdCount;

    private final Array<Event> currentEvents = new Array<>();

    public EventInteractHelper(Player player) {
        this.player = player;
    }

    public void controller(float delta) { interactCdCount -= delta; }

    /**
     * Player interacts with an event they are overlapping with
     */
    public void interact() {
        if (!currentEvents.isEmpty() && interactCdCount < 0) {
            interactCdCount = INTERACT_CD;
            currentEvents.get(0).getEventData().preActivate(null, player);
        }
    }

    public Array<Event> getCurrentEvents() { return currentEvents; }
}
