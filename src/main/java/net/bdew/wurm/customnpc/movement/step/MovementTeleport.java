package net.bdew.wurm.customnpc.movement.step;

import com.wurmonline.server.behaviours.CreatureBehaviour;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.movement.TilePosLayer;

public class MovementTeleport implements IMovementStep {
    private TilePosLayer target;

    public MovementTeleport(TilePosLayer target) {
        this.target = target;
    }

    @Override
    public boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta) {
        try {
            float posZ = Zones.calculateHeight(target.x, target.y, target.onSurface);
            CreatureBehaviour.blinkTo(creature, target.x * 4 + 2, target.y * 4 + 2, target.onSurface ? 0 : -1, posZ, -10L, 0);
        } catch (NoSuchZoneException e) {
            CustomNpcMod.logException("Error getting position for ai teleport", e);
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("MovementTeleport(%s)", target);
    }
}
