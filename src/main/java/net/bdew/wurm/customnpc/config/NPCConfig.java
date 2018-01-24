package net.bdew.wurm.customnpc.config;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import net.bdew.wurm.customnpc.CustomNpcMod;
import net.bdew.wurm.customnpc.movement.script.IMovementScript;
import net.bdew.wurm.customnpc.movement.script.MovementHouse;
import net.bdew.wurm.customnpc.movement.script.MovementStatic;
import net.bdew.wurm.customnpc.movement.script.MovementVillage;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.Random;

public class NPCConfig {
    private static Yaml yaml = new Yaml();

    private IMovementScript movementScript;
    private String name;
    private long face;

    public IMovementScript getMovementScript() {
        return movementScript;
    }

    public void setMovementScript(IMovementScript movementScript) {
        this.movementScript = movementScript;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFace() {
        return face;
    }

    public void setFace(long face) {
        this.face = face;
    }

    public void initialize(Creature creature) {
        name = creature.getName();
        Random rng = new Random();
        rng.setSeed(creature.getWurmId());
        face = rng.nextLong();
        movementScript = new MovementStatic();
    }

    @SuppressWarnings("unchecked")
    public void readFromFile(FileReader reader, long wurmId) throws ConfigLoadError {
        Map<String, Object> data = yaml.load(reader);

        if (data.containsKey("Face"))
            face = ((Number) data.get("Face")).longValue();
        else
            face = Server.rand.nextLong();

        try {
            Map<String, Object> movement = (Map<String, Object>) data.get("Movement");
            switch ((String) (movement.get("Type"))) {
                case "Static":
                    movementScript = new MovementStatic();
                    break;
                case "Village":
                    movementScript = new MovementVillage();
                    break;
                case "House":
                    movementScript = new MovementHouse();
                    break;
                default:
                    throw new ConfigLoadError("Unknown movement type: " + movement.get("Type"));
            }
            movementScript.readFromObject(movement);
        } catch (Exception e) {
            CustomNpcMod.logException(String.format("Error loading movement for npc %d, resetting to static", wurmId), e);
            movementScript = new MovementStatic();
        }
    }

    public void saveToFile(PrintStream file) {
        file.println(String.format("# NPC Data for %s", this.name));
        file.println(String.format("Face: %d", this.face));
        file.println("Movement:");
        movementScript.saveToFile(file);
    }
}
