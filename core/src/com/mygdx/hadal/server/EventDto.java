package com.mygdx.hadal.server;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * An EventDto contains the info sent between server and client needed to create a single event.
 * This replaces the previous method of sending the entire MapObject.
 * @author Fruntonette Flambly
 */
public class EventDto {

    private String name;
    private float x, y, width, height;
    private Array<Pair> properties;

    public EventDto() {}

    public EventDto(RectangleMapObject mapObject) {
        this.name = mapObject.getName();
        this.x = mapObject.getRectangle().x;
        this.y = mapObject.getRectangle().y;
        this.width = mapObject.getRectangle().width;
        this.height = mapObject.getRectangle().height;

        this.properties = new Array<>();

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

    public Array<Pair> getProperties() { return properties; }

    /**
     * A Pair is just a serializable key-value used to send the event's properties
     */
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
