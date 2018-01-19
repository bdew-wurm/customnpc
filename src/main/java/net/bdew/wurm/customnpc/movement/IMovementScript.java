package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.config.ConfigLoadError;

import java.io.PrintStream;
import java.util.Map;

public interface IMovementScript {
    void readFromObject(Map<String, Object> data) throws ConfigLoadError;

    void saveToFile(PrintStream file);

    public boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta);
}
