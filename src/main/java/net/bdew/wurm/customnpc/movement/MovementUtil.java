package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.creatures.ai.StaticPathFinderNPC;
import com.wurmonline.server.zones.Zones;
import net.bdew.wurm.customnpc.CustomAIScript;

public class MovementUtil {
    public static final StaticPathFinderNPC pathFinder = new StaticPathFinderNPC();

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

    static public PathTile pathTileToCreature(Creature creature, Creature target) {
        int tilePosX = Zones.safeTileX(target.getTileX());
        int tilePosY = Zones.safeTileY(target.getTileY());
        int tile;

        if (!target.isOnSurface()) {
            tile = Server.caveMesh.getTile(tilePosX, tilePosY);
        } else {
            tile = Server.surfaceMesh.getTile(tilePosX, tilePosY);
        }

        return new PathTile(tilePosX, tilePosY, tile, target.isOnSurface(), target.getFloorLevel());
    }
}
