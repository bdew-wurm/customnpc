package net.bdew.wurm.customnpc;

import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.*;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.WurmColor;
import net.bdew.wurm.customnpc.manage.ManageBehaviourProvider;
import net.bdew.wurm.customnpc.movement.PathCostFunc;

import java.nio.ByteBuffer;

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

    public static PathCostFunc getCostFunction(Creature creature) {
        return ((CustomAIData) (creature.getCreatureAIData())).getConfig().getMovementScript().getCostFunction(creature);
    }

    public static boolean getAvoidRaycast(Creature creature) {
        return ((CustomAIData) (creature.getCreatureAIData())).getConfig().getMovementScript().shouldAvoidRaycast(creature);
    }

    public static void sendWear(Creature owner, short place, Item item) {
        if (owner != null && isNpcTemplate(owner.getTemplate())) {
            owner.getCurrentTile().sendWieldItem(owner.getWurmId(), (byte) (place == 13 ? 0 : 1), item.getModelName(), item.getRarity(), WurmColor.getColorRed(item.color), WurmColor.getColorGreen(item.color), WurmColor.getColorBlue(item.color), WurmColor.getColorRed(item.color2), WurmColor.getColorGreen(item.color2), WurmColor.getColorBlue(item.color2));
        }
    }
}
