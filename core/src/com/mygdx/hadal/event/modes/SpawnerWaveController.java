package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.enemies.WaveType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

import static com.mygdx.hadal.schmucks.entities.enemies.WaveType.waveLimit;

/**
 * In arena mode, the SpawnWaveController is created and controls the WaveSpawner events in the map.
 * This event decides which WaveSpawners should be activated and when
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * waves: This is a list of waves to be spawned in order. Empty means infinite waves (survival mode)
 */
public class SpawnerWaveController extends Event {

    private static final float FIRST_WAVE_DELAY = 5.0f;
    private static final float DEFAULT_WAVE_DELAY = 15.0f;

    private final Array<WaveType.WaveTag> currentTags = new Array<>();
    private final Array<WaveType> currentWaves = new Array<>();
    private final Array<WaveType.WaveEnemy> currentEnemies = new Array<>();
    private final Array<WaveType.WaveEnemy> removeEnemies = new Array<>();
    private int waveNum;
    private boolean infinite;
    private float nextWaveTime = FIRST_WAVE_DELAY;

    public SpawnerWaveController(PlayState state, WaveType... waves) {
        super(state);
        currentTags.add(WaveType.WaveTag.STANDARD);
        if (waves.length == 0) {
            infinite = true;
        } else {
            currentWaves.addAll(waves);
        }
    }

    @Override
    public void create() {
        this.eventData = new EventData(this);
    }

    @Override
    public void controller(float delta) {
        nextWaveTime -= delta;

        if (nextWaveTime <= 0.0f) {
            nextWaveTime = DEFAULT_WAVE_DELAY;
            getNextWave(currentTags);
        }

        for (WaveType.WaveEnemy enemy : currentEnemies) {
            enemy.setCurrentDelay(enemy.getCurrentDelay() - delta);
            if (enemy.getCurrentDelay() <= 0) {
                enemy.createEnemy();
                removeEnemies.add(enemy);
            }
        }
        for (WaveType.WaveEnemy enemy : removeEnemies) {
            currentEnemies.removeValue(enemy, false);
        }
        removeEnemies.clear();
    }

    /**
     * Based on the tags, we decide what the next wave of enemies should be.
     */
    private void getNextWave(Array<WaveType.WaveTag> tags) {
        WaveType nextWave = WaveType.WAVE1;
        if (infinite) {
            Array<WaveType> waves = new Array<>();

            //find a wave that complies with tag restrictions
            for (WaveType wave: WaveType.values()) {

                boolean get = false;

                for (WaveType.WaveTag tag: tags) {
                    if (wave.getTags().contains(tag, false)) {
                        get = true;
                        break;
                    }
                }
                if (get) {
                    waves.add(wave);
                }
            }
            if (!waves.isEmpty()) {
                nextWave = waves.get(MathUtils.random(waves.size - 1));
            }
        } else {
            if (!currentWaves.isEmpty()) {
                nextWave = currentWaves.pop();
            }
        }
        processNextWave(nextWave);
    }

    private final ObjectMap<Integer, Array<SpawnerWave>> exclusiveSpawn = new ObjectMap<>();
    private final ObjectMap<Integer, SpawnerWave> inclusiveSpawn = new ObjectMap<>();
    private final Array<SpawnerWave> possibleSpawns = new Array<>();

    /**
     * Based on the next wave, we decide which spawners should handle creating it.
     * Some enemies have wave number restrictions
     */
    private void processNextWave(WaveType nextWave) {
        waveNum++;

        if (TiledObjectUtil.waveSpawners.isEmpty()) { return; }

        exclusiveSpawn.clear();
        inclusiveSpawn.clear();
        for (WaveType.WaveEnemy enemy : nextWave.getEnemies()) {
            if (waveNum < enemy.getMinWave() || (waveNum > enemy.getMaxWave() && enemy.getMaxWave() != waveLimit)) {
                continue;
            }

            enemy.setSpawner(null);
            enemy.resetDelay();

            possibleSpawns.clear();
            if (enemy.getExclusiveIndex() != 0) {

                //enemy cannot use the same spawner as another enemy with the same exclusive index
                if (exclusiveSpawn.containsKey(enemy.getExclusiveIndex())) {
                    for (SpawnerWave i : TiledObjectUtil.waveSpawners) {
                        if (i.getTags().contains(enemy.getTag(),false) &&
                                !exclusiveSpawn.get(enemy.getExclusiveIndex()).contains(i, false)) {
                            possibleSpawns.add(i);
                        }
                    }
                } else {
                    for (SpawnerWave i : TiledObjectUtil.waveSpawners) {
                        if (i.getTags().contains(enemy.getTag(),false)) {
                            possibleSpawns.add(i);
                        }
                    }
                }
                if (possibleSpawns.isEmpty()) {
                    possibleSpawns.addAll(TiledObjectUtil.waveSpawners);
                }

                //decide which of the possible spawners will create this specific enemy
                SpawnerWave spawner = possibleSpawns.random();
                enemy.setSpawner(spawner);
                if (exclusiveSpawn.containsKey(enemy.getExclusiveIndex())) {
                    exclusiveSpawn.get(enemy.getExclusiveIndex()).add(spawner);
                } else {
                    Array<SpawnerWave> newVal = new Array<>();
                    newVal.add(spawner);
                    exclusiveSpawn.put(enemy.getExclusiveIndex(), newVal);
                }
            }

            possibleSpawns.clear();
            if (enemy.getInclusiveIndex() != 0) {

                //null check in case exclusive spawn check already found a spawner
                if (enemy.getSpawner() != null) {
                    inclusiveSpawn.put(enemy.getInclusiveIndex(), enemy.getSpawner());
                } else {

                    //enemy must use the same spawner as another enemy with the same inclusive index
                    if (inclusiveSpawn.containsKey(enemy.getInclusiveIndex())) {
                        enemy.setSpawner(inclusiveSpawn.get(enemy.getInclusiveIndex()));
                    } else {
                        for (SpawnerWave i : TiledObjectUtil.waveSpawners) {
                            if (i.getTags().contains(enemy.getTag(),false)) {
                                possibleSpawns.add(i);
                            }
                        }
                        if (possibleSpawns.isEmpty()) {
                            possibleSpawns.addAll(TiledObjectUtil.waveSpawners);
                        }
                        SpawnerWave spawner = possibleSpawns.random();
                        enemy.setSpawner(spawner);
                        inclusiveSpawn.put(enemy.getInclusiveIndex(), spawner);
                    }
                }
            }

            possibleSpawns.clear();
            if (enemy.getSpawner() == null) {
                for (SpawnerWave i : TiledObjectUtil.waveSpawners) {
                    if (i.getTags().contains(enemy.getTag(),false)) {
                        possibleSpawns.add(i);
                    }
                }
                if (possibleSpawns.isEmpty()) {
                    possibleSpawns.addAll(TiledObjectUtil.waveSpawners);
                }
                SpawnerWave spawner = possibleSpawns.random();
                enemy.setSpawner(spawner);
            }

            currentEnemies.add(enemy);
        }
    }
}

