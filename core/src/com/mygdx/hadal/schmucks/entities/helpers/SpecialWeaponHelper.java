package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;

public class SpecialWeaponHelper {

    private final Queue<Hitbox> stickyBombs = new Queue<>();
    private final Array<Hitbox> teslaCoils = new Array<>();
    private final Array<Hitbox> leapFrogs = new Array<>();
    private final Array<Enemy> bits = new Array<>();
    private Hitbox deathOrbHbox;
    private Hitbox diamondCutterHbox;
    private Hitbox morningStarBase, morningStar;

    private int sprayWeaponShotNumber;

    public SpecialWeaponHelper() {}

    public Queue<Hitbox> getStickyBombs() { return stickyBombs; }

    public Array<Hitbox> getTeslaCoils() { return teslaCoils; }

    public Array<Hitbox> getLeapFrogs() { return leapFrogs; }

    public Array<Enemy> getBits() { return bits; }

    public Hitbox getDeathOrbHbox() { return deathOrbHbox; }

    public void setDeathOrbHbox(Hitbox deathOrbHbox) { this.deathOrbHbox = deathOrbHbox; }

    public Hitbox getDiamondCutterHbox() { return diamondCutterHbox; }

    public void setDiamondCutterHbox(Hitbox diamondCutterHbox) { this.diamondCutterHbox = diamondCutterHbox; }

    public Hitbox getMorningStarBase() { return morningStarBase; }

    public void setMorningStarBase(Hitbox morningStarBase) { this.morningStarBase = morningStarBase; }

    public Hitbox getMorningStar() { return morningStar; }

    public void setMorningStar(Hitbox morningStar) { this.morningStar = morningStar; }

    public int getSprayWeaponShotNumber() { return sprayWeaponShotNumber; }

    public void setSprayWeaponShotNumber(int sprayWeaponShotNumber) { this.sprayWeaponShotNumber = sprayWeaponShotNumber; }
}
