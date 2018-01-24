package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.movement.MovementUtil;
import net.bdew.wurm.customnpc.movement.step.IMovementStep;

import java.io.PrintStream;
import java.util.Map;

public class MovementStatic implements IMovementScript, IMovementStep {
    @Override
    public void readFromObject(Map<String, Object> data) {

    }

    @Override
    public void saveToFile(PrintStream file) {
        file.print("    Type: Static");
    }


    @Override
    public boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta) {
        MovementUtil.clearPath(status);
        return false;
    }

    @Override
    public IMovementStep getNextStep(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data) {
        return this;
    }

    @Override
    public String toString() {
        return "MovementStatic()";
    }
}
