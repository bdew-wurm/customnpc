package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.CreatureBehaviour;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.config.ConfigLoadError;

import java.io.PrintStream;
import java.util.Map;

public class MovementVillage implements IMovementScript {
    private final static int TIMER_MOVE = 1;

    private Village village;

    public Village getVillage() {
        return village;
    }

    public void setVillage(Village village) {
        this.village = village;
    }

    @Override
    public void readFromObject(Map<String, Object> data) throws ConfigLoadError {
        if (data.containsKey("VillageID")) {
            int villageId = (int) data.get("VillageID");
            try {
                village = Villages.getVillage(villageId);
            } catch (NoSuchVillageException e) {
                throw new ConfigLoadError(e);
            }
        } else throw new ConfigLoadError("Missing key VillageID");

    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println("    Type: Village");
        file.println(String.format("    VillageID: %d # %s", village.id, village.getName()));
    }

    @Override
    public boolean pollMovement(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data, long delta) {
        if (creature.getCurrentTile().getVillage() != village) {
            CustomNpcMod.logInfo(String.format("NPC %d is outside it's designated village %s, teleporting back", creature.getWurmId(), village.getName()));
            MovementUtil.clearPath(status);
            try {
                Item token = village.getToken();
                CreatureBehaviour.blinkTo(creature, token.getPosX(), token.getPosY(), token.isOnSurface() ? 0 : -1, token.getPosZ(), -10L, 0);
            } catch (NoSuchItemException e) {
                CustomNpcMod.logException("Error getting token for ai teleport to base", e);
            }
        }
        if (!MovementUtil.followPath(creature, status, ai)) {
            ai.increaseTimer(creature, delta, TIMER_MOVE);
            if (ai.isTimerReady(creature, TIMER_MOVE, 1000L)) {
                if (Server.rand.nextInt(100) < 10) {
                    int targetX = village.startx + Server.rand.nextInt(village.endx - village.startx);
                    int targetY = village.starty + Server.rand.nextInt(village.endy - village.starty);
                    creature.setPathfindcounter(0);
                    creature.startPathingToTile(ai.getMovementTarget(creature, targetX, targetY));
                    CustomNpcMod.logInfo(String.format("NPC %d pathing inside village %s to x=%d y=%d", creature.getWurmId(), village.getName(), targetX, targetY));
                }
                ai.resetTimer(creature, TIMER_MOVE);
            }
        }
        return false;
    }
}
