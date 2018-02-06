package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureStatus;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomAIScript;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.config.ConfigLoadError;
import net.bdew.wurm.customnpc.movement.TilePosLayer;
import net.bdew.wurm.customnpc.movement.step.IMovementStep;
import net.bdew.wurm.customnpc.movement.step.MovementPathfind;
import net.bdew.wurm.customnpc.movement.step.MovementTeleport;

import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

abstract public class MovementRandomArea implements IMovementScript {

    protected float movementChance = 0.1f;
    protected float movementSpeedMod = 1f;
    protected int pathFindingType = 0;

    public float getMovementChance() {
        return movementChance;
    }

    public void setMovementChance(float movementChance) {
        this.movementChance = movementChance;
    }

    public float getMovementSpeedMod() {
        return movementSpeedMod;
    }

    public void setMovementSpeedMod(float movementSpeedMod) {
        this.movementSpeedMod = movementSpeedMod;
    }

    public int getPathFindingType() {return pathFindingType;}

    public void setPathFindingType(int pathFindingType) { this.pathFindingType = pathFindingType;}

    @Override
    public void saveToFile(PrintStream file) {
        file.println(String.format("    MovementChance: %.6f", this.movementChance));
        file.println(String.format("    MovementSpeedMod: %.6f", this.movementSpeedMod));
        file.println(String.format("    PathFindingType: %s", this.pathFindingType));
    }

    @Override
    public void readFromObject(Map<String, Object> data) throws ConfigLoadError {
        movementChance = ((Number) data.getOrDefault("MovementChance", 0.1f)).floatValue();
        movementSpeedMod = ((Number) data.getOrDefault("MovementSpeedMod", 1f)).floatValue();
        pathFindingType = ((Number) data.getOrDefault("PathFindingType", 0)).intValue();
    }

    protected abstract boolean isInsideZone(Creature creature);

    protected abstract TilePosLayer randomTile(Creature creature);

    protected abstract TilePosLayer originTile(Creature creature);

    @Override
    public IMovementStep getNextStep(Creature creature, CreatureStatus status, CustomAIScript ai, CustomAIData data) {
        if (!isInsideZone(creature)) {
            CustomNpcMod.logInfo(String.format("NPC %d is outside it's designated zone, teleporting back", creature.getWurmId()));
            return new MovementTeleport(originTile(creature));
        } else if (Server.rand.nextFloat() <= movementChance) {
            return new MovementPathfind(randomTile(creature), movementSpeedMod, null);
        } else {
            return null;
        }
    }

    @Override
    public void configBML(Creature creature, StringBuilder buf) {
        buf.append("harray{label{text='Speed Modifier:'};input{id='movementSpeedMod'; text=\"").append(movementSpeedMod).append("\";maxchars='10'}}");
        buf.append("harray{label{text='Movement Chance:'};input{id='movementChance'; text=\"").append(movementChance).append("\";maxchars='10'}}");
        buf.append("harray{label{text='Prefer roads:'};dropdown{id='pathFindingType'; options='No,Yes,Aggressive'; default='")
        .append(pathFindingType).append("';}}");
    }

    @Override
    public void processConfig(Creature creature, Properties properties) {
        if (properties.containsKey("movementSpeedMod"))
            movementSpeedMod = Float.parseFloat(properties.getProperty("movementSpeedMod"));
        if (properties.containsKey("movementChance"))
            movementChance = Float.parseFloat(properties.getProperty("movementChance"));
        if (properties.containsKey("pathFindingType"))
            pathFindingType = Integer.parseInt(properties.getProperty("pathFindingType"));
    }
}
