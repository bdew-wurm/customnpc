package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.ai.PathTile;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.config.ConfigLoadError;
import net.bdew.wurm.customnpc.movement.step.IMovementStep;

import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public interface IMovementScript {
    void readFromObject(Map<String, Object> data) throws ConfigLoadError;

    void saveToFile(PrintStream file);

    IMovementStep getNextStep(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data);

    void configBML(Creature creature, StringBuilder buf);

    void processConfig(Creature creature, Properties properties);

    Function<PathTile, Float> getCostFunction(Creature creature);
}
