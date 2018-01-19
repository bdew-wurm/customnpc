package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.movement.MovementVillage;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.*;

public class MovementVillageAction implements ActionPerformer {
    ActionEntry actionEntry;

    public MovementVillageAction() {
        actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "Village wander", "managing", new int[]{
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */
        });
        ModActions.registerAction(actionEntry);
    }

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }


    public boolean canUse(Creature performer, Creature target) {
        return ManageBehaviourProvider.canManage(performer, target) && target.getCurrentTile().getVillage() != null;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
        return action(action, performer, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
        if (canUse(performer, target)) {
            CustomAIData data = (CustomAIData) (target.getCreatureAIData());
            Village village = target.getCurrentTile().getVillage();
            MovementVillage movement = new MovementVillage();
            movement.setVillage(village);
            data.getConfig().setMovementScript(movement);
            data.configUpdated();
            performer.getCommunicator().sendNormalServerMessage(String.format("%s will now wander around %s.", target.getName(), village.getName()));

        }
        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}
