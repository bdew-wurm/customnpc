package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.ai.StaticPathFinderNPC;
import net.bdew.wurm.customnpc.CustomAIScript;

public class MovementUtil {
    public static final StaticPathFinderNPC pathFinder = new StaticPathFinderNPC();

    public static void clearPath(CreatureStatus status) {
        if (status.getPath() != null) {
            status.getPath().clear();
            status.setPath(null);
            status.setMoving(false);
        }
    }

    public static boolean followPath(Creature creature, CreatureStatus status, CustomAIScript ai) {
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
