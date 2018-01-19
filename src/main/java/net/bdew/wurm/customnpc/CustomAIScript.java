package net.bdew.wurm.customnpc;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAI;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.creatures.ai.PathTile;

public class CustomAIScript extends CreatureAI {
    @Override
    protected boolean pollMovement(Creature creature, long delta) {
        CustomAIData data = (CustomAIData) (creature.getCreatureAIData());
        return data.getConfig().getMovementScript().pollMovement(creature, creature.getStatus(), this, data, delta);
    }

    @Override
    protected boolean pollAttack(Creature creature, long l) {
        return false;
    }

    @Override
    protected boolean pollBreeding(Creature creature, long l) {
        return false;
    }

    @Override
    public CreatureAIData createCreatureAIData() {
        return new CustomAIData();
    }

    @Override
    public void creatureCreated(Creature creature) {
        creature.getCreatureAIData().setCreature(creature);
    }

    // === Make stuff public, so that it can be accessed from implementing classes ===

    @Override
    public void pathedMovementTick(Creature c) {
        super.pathedMovementTick(c);
    }

    @Override
    public PathTile getMovementTarget(Creature c, int tilePosX, int tilePosY) {
        return super.getMovementTarget(c, tilePosX, tilePosY);
    }

    @Override
    public boolean isTimerReady(Creature c, int timerId, long minTime) {
        return super.isTimerReady(c, timerId, minTime);
    }

    @Override
    public void increaseTimer(Creature c, long delta, int... timerIds) {
        super.increaseTimer(c, delta, timerIds);
    }

    @Override
    public void resetTimer(Creature c, int... timerIds) {
        super.resetTimer(c, timerIds);
    }
}
