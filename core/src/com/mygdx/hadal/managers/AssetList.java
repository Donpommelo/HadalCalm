package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;

public enum AssetList {

	TITLE_CARD("HADAL_PANIC_3.jpg", Texture.class),
	BUTLER_FONT("fonts/butler.fnt", null),
	LEARNING_FONT("fonts/learning_curve.fnt", null);
	
	//Enum constructor and methods.
	private String pathname;
    private Class<?> type;
    
    AssetList(String s, Class<?> c) {
        this.pathname = s;
        this.type = c;
    }

    @Override
    public String toString() {
        return this.pathname;
    }

    public Class<?> getType() { 
    	return type; 
    }
}
