package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.math.TilePos;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.config.ConfigLoadError;
import net.bdew.wurm.customnpc.movement.TilePosLayer;

import java.io.PrintStream;
import java.util.Map;

public class MovementVillage extends MovementSquareArea {

    private Village village;

    public Village getVillage() {
        return village;
    }

    public void setVillage(Village village) {
        this.village = village;
    }

    @Override
    protected TilePos getNECorner() {
        return TilePos.fromXY(village.startx, village.starty);
    }

    @Override
    protected TilePos getSWCorner() {
        return TilePos.fromXY(village.startx, village.starty);
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
        super.readFromObject(data);
    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println("    Type: Village");
        file.println(String.format("    VillageID: %d # %s", village.id, village.getName()));
        super.saveToFile(file);
    }

    @Override
    protected boolean isInsideZone(Creature creature) {
        return creature.getCurrentTile().getVillage() == village;
    }

    @Override
    protected TilePosLayer originTile(Creature creature) {
        try {
            return TilePosLayer.from(village.getToken());
        } catch (NoSuchItemException e) {
            CustomNpcMod.logException("Error getting token", e);
            return randomTile(creature);
        }
    }

    @Override
    public void configBML(Creature creature, StringBuilder buf) {
        buf.append("label{type='bold';text=\"Movement: Wander in village - ").append(village.getName()).append("\"}");
        super.configBML(creature, buf);
    }

    @Override
    public String toString() {
        return String.format("MovementVillage(%s)", this.village.getName());
    }
}
