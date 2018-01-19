package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.CreatureBehaviour;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.config.ConfigLoadError;

import java.io.PrintStream;
import java.util.Map;

public class MovementHouse implements IMovementScript {
    private final static int TIMER_MOVE = 1;

    private Structure house;

    public Structure getHouse() {
        return house;
    }

    public void setHouse(Structure house) {
        this.house = house;
    }

    @Override
    public void readFromObject(Map<String, Object> data) throws ConfigLoadError {
        if (data.containsKey("StructureID")) {
            long structureId = (long) data.get("StructureID");
            try {
                house = Structures.getStructure(structureId);
            } catch (NoSuchStructureException e) {
                throw new ConfigLoadError(e);
            }
        } else throw new ConfigLoadError("Missing key StructureID");

    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println("    Type: House");
        file.println(String.format("    StructureID: %d # %s", house.getWurmId(), house.getName()));
    }

    @Override
    public boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta) {
        if (creature.getCurrentTile().getStructure() != house) {
            CustomNpcMod.logInfo(String.format("NPC %d is outside it's designated structure %s, teleporting back", creature.getWurmId(), house.getName()));
            MovementUtil.clearPath(status);
            VolaTile[] tiles = house.getStructureTiles();
            VolaTile tile = tiles[Server.rand.nextInt(tiles.length)];
            try {
                float posZ = Zones.calculateHeight(tile.getPosX(), tile.getPosY(), tile.isOnSurface());
                CreatureBehaviour.blinkTo(creature, tile.getPosX(), tile.getPosY(), tile.isOnSurface() ? 0 : -1, posZ, -10L, 0);
            } catch (NoSuchZoneException e) {
                CustomNpcMod.logException("Error getting position for ai teleport to base", e);
            }
        }
        if (!MovementUtil.followPath(creature, status, ai)) {
            ai.increaseTimer(creature, delta, TIMER_MOVE);
            if (ai.isTimerReady(creature, TIMER_MOVE, 1000L)) {
                if (Server.rand.nextInt(100) < 10) {
                    VolaTile[] tiles = house.getStructureTiles();
                    VolaTile tile = tiles[Server.rand.nextInt(tiles.length)];
                    creature.startPathingToTile(ai.getMovementTarget(creature, tile.tilex, tile.tiley));
                    CustomNpcMod.logInfo(String.format("NPC %d pathing inside structure %s to x=%d y=%d", creature.getWurmId(), house.getName(), tile.tilex, tile.tiley));
                }
                ai.resetTimer(creature, TIMER_MOVE);
            }
        }
        return false;
    }
}
