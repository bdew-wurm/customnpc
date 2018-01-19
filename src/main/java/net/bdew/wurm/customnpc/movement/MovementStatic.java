package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;

import java.io.PrintStream;
import java.util.Map;

public class MovementStatic implements IMovementScript {
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
}
