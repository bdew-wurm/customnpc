package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.math.TilePos;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import net.bdew.wurm.customnpc.config.ConfigLoadError;
import net.bdew.wurm.customnpc.movement.PathCostFunc;
import net.bdew.wurm.customnpc.movement.TilePosLayer;

import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

public abstract class MovementSquareArea extends MovementRandomArea implements IMovementScript {
    private boolean preferRoads;

    abstract protected TilePos getNECorner();

    abstract protected TilePos getSWCorner();

    @Override
    protected TilePosLayer randomTile(Creature creature) {
        TilePos start = getNECorner();
        TilePos end = getSWCorner();
        int targetX = start.x + Server.rand.nextInt(end.x - start.x);
        int targetY = start.y + Server.rand.nextInt(end.y - start.y);
        return new TilePosLayer(targetX, targetY, creature.isOnSurface());
    }

    @Override
    protected boolean isInsideZone(Creature creature) {
        TilePos start = getNECorner();
        TilePos end = getSWCorner();
        TilePos pos = creature.getTilePos();
        return pos.x >= start.x && pos.x <= end.x && pos.y >= start.y && pos.y <= end.y;
    }

    @Override
    protected TilePosLayer originTile(Creature creature) {
        TilePos start = getNECorner();
        TilePos end = getSWCorner();
        return new TilePosLayer((start.x + end.x) / 2, (start.y + end.y) / 2, creature.isOnSurface());
    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println(String.format("    PreferRoads: %s", preferRoads));
        super.saveToFile(file);
    }

    @Override
    public void readFromObject(Map<String, Object> data) throws ConfigLoadError {
        if (data.containsKey("PreferRoads")) {
            preferRoads = (boolean) data.get("PreferRoads");
        }
        super.readFromObject(data);
    }

    @Override
    public void configBML(Creature creature, StringBuilder buf) {
        buf.append("checkbox{id=\"move_prefer_roads\";selected='" + preferRoads + "';text=\"Prefer roads when moving\"}");
        super.configBML(creature, buf);
    }

    @Override
    public void processConfig(Creature creature, Properties properties) {
        preferRoads = Boolean.parseBoolean(properties.getProperty("move_prefer_roads"));
        super.processConfig(creature, properties);
    }

    @Override
    public PathCostFunc getCostFunction(Creature creature) {
        return (p) -> {
            byte tileType = Tiles.decodeType(p.getTile());
            if (Tiles.isSolidCave(tileType)) {
                return Float.MAX_VALUE;
            } else if (preferRoads) {
                return Tiles.isRoadType(tileType) ? 1f : 100f;
            }
            return 1f;
        };
    }

    @Override
    public boolean shouldAvoidRaycast(Creature creature) {
        return preferRoads;
    }

}
