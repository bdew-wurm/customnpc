package net.bdew.wurm.customnpc;

import com.wurmonline.server.ServerDirInfo;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import net.bdew.wurm.customnpc.config.NPCConfig;
import net.bdew.wurm.customnpc.movement.IMovementExecutor;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

public class CustomAIData extends CreatureAIData {
    private static File dataPath;
    private NPCConfig config;
    private IMovementExecutor nextMovement;

    public NPCConfig getConfig() {
        if (config == null) {
            config = new NPCConfig();
            File cfgFile = configPath();
            if (!cfgFile.exists()) {
                CustomNpcMod.logInfo(String.format("No config file for npc %d, creating new", getCreature().getWurmId()));
                config.initialize(getCreature());
                configUpdated();
            } else {
                try (FileReader reader = new FileReader((cfgFile))) {
                    config.readFromFile(reader, getCreature().getWurmId());
                    CustomNpcMod.logInfo(String.format("Loaded config for NPC %d", getCreature().getWurmId()));
                } catch (Exception e) {
                    CustomNpcMod.logException(String.format("Failed to load config file for npc %d, recreating", getCreature().getWurmId()), e);
                    config.initialize(getCreature());
                    configUpdated();
                }
            }
        }
        return config;
    }

    public void configUpdated() {
        try (PrintStream ps = new PrintStream(configPath())) {
            if (config.getName() == null) config.setName(getCreature().getName());
            getConfig().saveToFile(ps);
            CustomNpcMod.logInfo(String.format("Saved config for NPC %d", getCreature().getWurmId()));
        } catch (Exception e) {
            CustomNpcMod.logException(String.format("Failed to save config file for npc %d", getCreature().getWurmId()), e);
        }
    }

    private File configPath() {
        if (dataPath == null) {
            dataPath = new File(ServerDirInfo.getFileDBPath(), "npc_data");
            if (!dataPath.exists()) {
                if (!dataPath.mkdir()) {
                    throw new RuntimeException("Failed to create NPC data folder at " + dataPath.toString());
                }
            }
        }
        return new File(dataPath, getCreature().getWurmId() + ".cfg");
    }

    public IMovementExecutor getNextMovement() {
        return nextMovement;
    }

    public void setNextMovement(IMovementExecutor nextMovement) {
        if (nextMovement != null) {
            CustomNpcMod.logInfo(String.format("New movement for %s: %s", getCreature().getName(), nextMovement.toString()));
        }
        this.nextMovement = nextMovement;
    }
}
