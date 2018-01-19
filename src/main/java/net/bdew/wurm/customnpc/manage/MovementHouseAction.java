package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Structure;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.movement.MovementHouse;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.*;

public class MovementHouseAction implements ActionPerformer {
    ActionEntry actionEntry;

    public MovementHouseAction() {
        actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "House wander", "managing", new int[]{
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
        if (!ManageBehaviourProvider.canManage(performer, target)) return false;
        Structure structure = target.getCurrentTile().getStructure();
        return structure != null && structure.isTypeHouse();
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
        return action(action, performer, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
        if (canUse(performer, target)) {
            CustomAIData data = (CustomAIData) (target.getCreatureAIData());
            Structure structure = target.getCurrentTile().getStructure();
            MovementHouse movement = new MovementHouse();
            movement.setHouse(structure);
            data.getConfig().setMovementScript(movement);
            data.configUpdated();
            performer.getCommunicator().sendNormalServerMessage(String.format("%s will now wander inside %s.", target.getName(), structure.getName()));

        }
        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}