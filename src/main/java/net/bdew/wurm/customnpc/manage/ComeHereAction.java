package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.movement.TilePosLayer;
import net.bdew.wurm.customnpc.movement.step.MovementPathfind;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.FINISH_ACTION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_SERVER_PROPAGATION;

public class ComeHereAction extends BaseManagementAction {
    public ComeHereAction() {
        super(ActionEntry.createEntry((short) ModActions.getNextActionId(), "Come here", "managing", new int[]{
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */
        }));

    }

    @Override
    public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
        if (canUse(performer, target)) {
            CustomAIData data = (CustomAIData) target.getCreatureAIData();
            data.setNextMovement(new MovementPathfind(TilePosLayer.from(performer), performer));
        }
        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}
