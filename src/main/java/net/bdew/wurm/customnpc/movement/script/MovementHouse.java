package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.zones.VolaTile;
import net.bdew.wurm.customnpc.config.ConfigLoadError;
import net.bdew.wurm.customnpc.movement.TilePosLayer;

import java.io.PrintStream;
import java.util.Map;

public class MovementHouse extends MovementRandomArea implements IMovementScript {

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
        super.readFromObject(data);
    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println("    Type: House");
        file.println(String.format("    StructureID: %d # %s", house.getWurmId(), house.getName()));
        super.saveToFile(file);
    }


    @Override
    protected boolean isInsideZone(Creature creature) {
        return creature.getCurrentTile().getStructure() == house;
    }

    @Override
    protected TilePosLayer randomTile(Creature creature) {
        VolaTile[] tiles = house.getStructureTiles();
        VolaTile tile = tiles[Server.rand.nextInt(tiles.length)];
        return new TilePosLayer(tile.getTileX(), tile.getTileY(), tile.isOnSurface());
    }

    @Override
    protected TilePosLayer originTile(Creature creature) {
        return randomTile(creature);
    }

    @Override
    public String toString() {
        return String.format("MovementHouse(%s)", this.house.getName());
    }
}
