package net.bdew.wurm.customnpc.movement.step;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.ai.NoPathException;
import com.wurmonline.server.creatures.ai.Path;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.movement.MovementUtil;
import net.bdew.wurm.customnpc.movement.TilePosLayer;

public class MovementPathfind implements IMovementStep {
    private boolean started = false;
    private final TilePosLayer target;
    private final Creature reportTo;
    private int lastTileX, lastTileY, lastCounter = 0;

    public MovementPathfind(TilePosLayer target, Creature reportTo) {
        this.target = target;
        this.reportTo = reportTo;
    }

    private void reportFailed(String msg) {
        if (reportTo != null && reportTo.hasLink()) {
            reportTo.getCommunicator().sendNormalServerMessage(msg);
        } else {
            CustomNpcMod.logInfo(msg);
        }
    }

    @Override
    public boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta) {
        if (creature.isOnSurface() != target.onSurface || creature.getFloorLevel() != target.floor) {
            reportFailed(String.format("%s can't pathfind across layers or floors", creature.getName()));
            return true;
        }
        if (!started) {
            lastTileX = creature.getTileX();
            lastTileY = creature.getTileY();
            MovementUtil.clearPath(status);
            try {
                creature.setPathfindcounter(0);
                Path path = creature.findPath(target.x, target.y, MovementUtil.pathFinder);
                if (path != null) {
                    creature.getStatus().setPath(path);
                    started = true;
                } else {
                    reportFailed(String.format("%s failed to find path", creature.getName()));
                    return true;
                }
            } catch (NoPathException e) {
                reportFailed(String.format("%s failed to find path: %s", creature.getName(), e.getMessage()));
                return true;
            }
        }
        if (!MovementUtil.followPath(creature, status, ai)) return true;
        if (creature.getTileX() == lastTileX && creature.getTileY() == lastTileY) {
            if (lastCounter++ > 30) {
                reportFailed(String.format("%s failed to make progress in path, aborting", creature.getName()));
                return true;
            }
        } else {
            lastTileX = creature.getTileX();
            lastTileY = creature.getTileY();
            lastCounter = 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("MovementPathfind(%s, started=%s)", target, started);
    }
}
