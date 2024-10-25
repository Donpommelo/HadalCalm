package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.ChatWheel;
import com.mygdx.hadal.states.PlayState;

public class ChatWheelHeadless extends ChatWheel {

    public ChatWheelHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void setVisibility(boolean visible) {}
}
