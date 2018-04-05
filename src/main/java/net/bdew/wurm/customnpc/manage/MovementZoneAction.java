package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.movement.script.MovementZone;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.*;

public class MovementZoneAction extends BaseManagementAction {
    public MovementZoneAction() {
        super(ActionEntry.createEntry((short) ModActions.getNextActionId(), "Preset zone", "managing", new int[]{
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */
        }));
    }

    @Override
    public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
        if (canUse(performer, target)) {
            CustomAIData data = (CustomAIData) (target.getCreatureAIData());
            Village village = target.getCurrentTile().getVillage();
            MovementZone movement = new MovementZone();
            movement.setNeCorner(target.getTilePos());
            movement.setSwCorner(target.getTilePos());
            data.getConfig().setMovementScript(movement);
            data.configUpdated();
            data.setNextMovement(null);
            performer.getCommunicator().sendNormalServerMessage(String.format("%s is set to movement in zone, now configure it in management window.", target.getName()));

        }
        return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
    }
}
