package com.mygdx.hadal.pathfinding;

import com.badlogic.gdx.ai.pfa.PathFinderRequest;

public interface Pather<N> {
    public void acceptPath(PathFinderRequest<N> request);
}