package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.config.ConfigLoadError;
import net.bdew.wurm.customnpc.movement.PathCostFunc;
import net.bdew.wurm.customnpc.movement.TilePosLayer;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public class MovementHouse extends MovementRandomArea implements IMovementScript {

    private Structure house;
    private boolean avoidFurniture = false;

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

        if (data.containsKey("AvoidFurniture")) {
            avoidFurniture = (boolean) data.get("AvoidFurniture");
        }

        super.readFromObject(data);
    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println("    Type: House");
        file.println(String.format("    StructureID: %d # %s", house.getWurmId(), house.getName()));
        file.println(String.format("    AvoidFurniture: %s", avoidFurniture));
        super.saveToFile(file);
    }


    @Override
    protected boolean isInsideZone(Creature creature) {
        return creature.getCurrentTile().getStructure() == house;
    }

    @Override
    protected TilePosLayer randomTile(Creature creature) {
        VolaTile[] tiles = house.getStructureTiles();
        if (avoidFurniture) {
            LinkedList<VolaTile> validTiles = new LinkedList<>();
            for (VolaTile t : tiles) {
                if (!tileHasDeco(t, creature)) validTiles.add(t);
            }
            CustomNpcMod.logInfo(String.format("Filtered %d tiles to %d", tiles.length, validTiles.size()));
            tiles = validTiles.toArray(new VolaTile[validTiles.size()]);
        }
        VolaTile tile = tiles[Server.rand.nextInt(tiles.length)];
        return new TilePosLayer(tile.getTileX(), tile.getTileY(), tile.isOnSurface());
    }

    @Override
    protected TilePosLayer originTile(Creature creature) {
        return randomTile(creature);
    }

    @Override
    public void configBML(Creature creature, StringBuilder buf) {
        buf.append("label{type='bold';text=\"Movement: Wander in house - ").append(house.getName()).append("\"}");
        buf.append("checkbox{id=\"move_avoid_furniture\";selected='" + avoidFurniture + "';text=\"Try to avoid tiles with furniture and decorations\"}");
        super.configBML(creature, buf);
    }

    @Override
    public void processConfig(Creature creature, Properties properties) {
        avoidFurniture = Boolean.parseBoolean(properties.getProperty("move_avoid_furniture"));
        super.processConfig(creature, properties);
    }

    private boolean tileHasDeco(VolaTile vt, Creature creature) {
        for (Item i : vt.getItems()) {
            if (i.isDecoration() && i.getFloorLevel() == creature.getFloorLevel()) return true;
        }
        return false;
    }

    @Override
    public PathCostFunc getCostFunction(Creature creature) {
        return (p) -> {
            if (Tiles.isSolidCave(Tiles.decodeType(p.getTile()))) {
                return Float.MAX_VALUE;
            } else if (avoidFurniture) {
                VolaTile vt = Zones.getTileOrNull(p.getTileX(), p.getTileY(), p.isSurfaced());
                if (vt != null && tileHasDeco(vt, creature))
                    return 1000f;

            }
            return 1f;
        };
    }

    @Override
    public boolean shouldAvoidRaycast(Creature creature) {
        return avoidFurniture;
    }

    @Override
    public String toString() {
        return String.format("MovementHouse(%s)", this.house.getName());
    }
}
