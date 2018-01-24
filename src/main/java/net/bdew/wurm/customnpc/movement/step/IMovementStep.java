package net.bdew.wurm.customnpc.movement.step;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;

public interface IMovementStep {
    /**
     * Execute next movement action
     *
     * @return true if finished
     */
    boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta);
}
