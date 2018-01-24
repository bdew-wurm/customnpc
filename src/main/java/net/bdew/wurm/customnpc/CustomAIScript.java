package net.bdew.wurm.customnpc;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAI;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.creatures.ai.PathTile;
import net.bdew.wurm.customnpc.movement.step.IMovementStep;

public class CustomAIScript extends CreatureAI {
    @Override
    protected boolean pollMovement(Creature creature, long delta) {
        CustomAIData data = (CustomAIData) (creature.getCreatureAIData());
        IMovementStep next = data.getNextMovement();
        if (next == null) {
            next = data.getConfig().getMovementScript().getNextStep(creature, creature.getStatus(), this, data);
            data.setNextMovement(next);
        }
        if ((next != null) && next.pollMovement(creature, creature.getStatus(), this, data, delta)) {
            CustomNpcMod.logInfo(String.format("Movement finished for %s: %s", creature.getName(), next.toString()));
            data.setNextMovement(null);
        }
        return false;
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
