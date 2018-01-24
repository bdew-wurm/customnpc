package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.movement.script.MovementVillage;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.FINISH_ACTION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_SERVER_PROPAGATION;

public class MovementVillageAction extends BaseManagementAction {
    public MovementVillageAction() {
        super(ActionEntry.createEntry((short) ModActions.getNextActionId(), "Village wander", "managing", new int[]{
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */
        }));
    }

    public boolean canUse(Creature performer, Creature target) {
        return super.canUse(performer, target) && target.getCurrentTile().getVillage() != null;
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
            data.setNextMovement(null);
            performer.getCommunicator().sendNormalServerMessage(String.format("%s will now wander around %s.", target.getName(), village.getName()));

        }
        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}
