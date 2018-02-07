package net.bdew.wurm.customnpc;

import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.*;
import com.wurmonline.server.creatures.ai.PathTile;
import net.bdew.wurm.customnpc.manage.ManageBehaviourProvider;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class Hooks {
    public static boolean isNpcTemplate(CreatureTemplate tpl) {
        return NpcTemplate.npcTemplateIds.contains(tpl.getTemplateId());
    }

    public static boolean isNpcTemplateName(String name) {
        return NpcTemplate.npcTemplateNames.contains(name);
    }

    public static boolean handleNewFace(Communicator comm, ByteBuffer bb) {
        bb.mark();
        long face = bb.getLong();
        long item = bb.getLong();
        if (WurmId.getType(item) == WurmId.COUNTER_TYPE_CREATURES) {
            try {
                Creature creature = Creatures.getInstance().getCreature(item);
                if (canManage(comm.getPlayer(), creature)) {
                    CustomAIData data = ((CustomAIData) (creature.getCreatureAIData()));
                    data.getConfig().setFace(face);
                    data.configUpdated();
                    creature.getCurrentTile().setNewFace(creature);
                    comm.sendChangeModelName(comm.getPlayer().getWurmId(), comm.getPlayer().getModelName());
                    comm.sendNewFace(-10L, comm.getPlayer().getFace());
                    return true;
                }
            } catch (NoSuchCreatureException e) {
                e.printStackTrace();
            }
        }
        bb.reset();
        return false;
    }

    public static boolean canManage(Creature performer, Creature target) {
        return isNpcTemplate(target.getTemplate()) && ManageBehaviourProvider.canManage(performer, target);
    }

    public static long getFace(Npc npc) {
        return ((CustomAIData) (npc.getCreatureAIData())).getConfig().getFace();
    }

    public static Function<PathTile, Float> getCostFunction(Creature creature) {
        return ((CustomAIData) (creature.getCreatureAIData())).getConfig().getMovementScript().getCostFunction(creature);
    }
}
