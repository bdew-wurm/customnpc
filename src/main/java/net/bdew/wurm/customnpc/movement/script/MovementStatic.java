package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.movement.MovementUtil;
import net.bdew.wurm.customnpc.movement.step.IMovementStep;

import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

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

    @Override
    public void configBML(Creature creature, StringBuilder buf) {
        buf.append("label{type='bold';text='Movement: Static'}");
    }

    @Override
    public void processConfig(Creature creature, Properties properties) {

    }
}
