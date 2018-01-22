package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public abstract class BaseManagementAction implements ActionPerformer {
    private final ActionEntry actionEntry;

    public BaseManagementAction(ActionEntry actionEntry) {
        this.actionEntry = actionEntry;

        try {
            ReflectionUtil.setPrivateField(actionEntry, ReflectionUtil.getField(ActionEntry.class, "maxRange"), 100);
            ReflectionUtil.setPrivateField(actionEntry, ReflectionUtil.getField(ActionEntry.class, "blockType"), 0);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        ModActions.registerAction(actionEntry);
        ModActions.registerActionPerformer(this);
    }

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    public ActionEntry getActionEntry() {
        return actionEntry;
    }

    public boolean canUse(Creature performer, Creature target) {
        return ManageBehaviourProvider.canManage(performer, target);
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
        return action(action, performer, target, num, counter);
    }
}
