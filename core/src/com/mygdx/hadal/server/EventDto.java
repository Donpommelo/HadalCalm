package com.mygdx.hadal.server;

import com.badlogic.gdx.maps.objects.RectangleMapObject;

import java.util.ArrayList;
import java.util.Iterator;

public class EventDto {

    private String name;
    private float x, y, width, height;
    private ArrayList<Pair> properties;

    public EventDto() {}

    public EventDto(String name, float x, float y, float width, float height, ArrayList<Pair> properties) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.properties = properties;
    }

    public EventDto(RectangleMapObject mapObject) {
        this.name = mapObject.getName();
        this.x = mapObject.getRectangle().x;
        this.y = mapObject.getRectangle().y;
        this.width = mapObject.getRectangle().width;
        this.height = mapObject.getRectangle().height;

        this.properties = new ArrayList<>();

        for (Iterator<String> it = mapObject.getProperties().getKeys(); it.hasNext(); ) {
            final String key = it.next();
            properties.add(new Pair(key, mapObject.getProperties().get(key)));
        }
    }

    public String getName() { return name; }

    public float getX() { return x; }

    public float getY() { return y; }

    public float getHeight() { return height; }

    public float getWidth() { return width; }

    public ArrayList<Pair> getProperties() { return properties; }

    public static class Pair {

        private final String key;
        private final Object value;

        public Pair() { this.key = ""; this.value = null; }

        public Pair(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return key; }
        public Object getValue() { return value; }
    }
}
