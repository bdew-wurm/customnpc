package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIScript;

public class MovementUtil {
    static void clearPath(CreatureStatus status) {
        if (status.getPath() != null) {
            status.getPath().clear();
            status.setPath(null);
            status.setMoving(false);
        }
    }

    static boolean followPath(Creature creature, CreatureStatus status, CustomAIScript ai) {
        if (status.getPath() != null) {
            ai.pathedMovementTick(creature);
            if (status.getPath().isEmpty()) {
                status.setPath(null);
                status.setMoving(false);
            }
            return true;
        } else return false;
    }
}
