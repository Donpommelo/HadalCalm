package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;

public class SpecialWeaponHelper {

    private final Queue<Hitbox> stickyBombs = new Queue<>();
    private final Array<Hitbox> teslaCoils = new Array<>();
    private final Array<Enemy> bits = new Array<>();
    private Hitbox diamondCutterHbox;

    public SpecialWeaponHelper() {}

    public Queue<Hitbox> getStickyBombs() { return stickyBombs; }

    public Array<Hitbox> getTeslaCoils() { return teslaCoils; }

    public Array<Enemy> getBits() { return bits; }

    public Hitbox getDiamondCutterHbox() { return diamondCutterHbox; }

    public void setDiamondCutterHbox(Hitbox diamondCutterHbox) { this.diamondCutterHbox = diamondCutterHbox; }
}
