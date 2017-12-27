package com.mygdx.hadal.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.mygdx.hadal.managers.LevelManager;

public class FlyingHeuristic implements Heuristic<Node> {
    @Override
    public float estimate(Node startNode, Node endNode) {
        int startIndex = startNode.getIndex();
        int endIndex = endNode.getIndex();

        int startY = startIndex / LevelManager.lvlTileWidth;
        int startX = startIndex % LevelManager.lvlTileWidth;

        int endY = endIndex / LevelManager.lvlTileWidth;
        int endX = endIndex % LevelManager.lvlTileWidth;

        // Pythagorean distance
        float distance = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));

        return distance;
    }
}