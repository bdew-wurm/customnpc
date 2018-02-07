package net.bdew.wurm.customnpc.movement;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.creatures.ai.StaticPathFinderNPC;
import net.bdew.wurm.customnpc.CustomAIScript;

public class MovementUtil {
    public static final StaticPathFinderNPC pathFinder;

    static {
        try {
            pathFinder = (StaticPathFinderNPC) Class.forName("com.wurmonline.server.creatures.ai.StaticPathFinderCustomNPC").newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException("Error creating custom pathfinder", e);
        }
    }

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

    public static float getCostSimple(PathTile p) {
        if (Tiles.isSolidCave(Tiles.decodeType(p.getTile()))) {
            return Float.MAX_VALUE;
        } else {
            return Tiles.decodeHeight(p.getTile()) < 1 ? 3.0F : 1.0F;
        }
    }
}
