package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import net.bdew.wurm.customnpc.NpcTemplate;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

import java.util.LinkedList;
import java.util.List;

public class ManageBehaviourProvider implements BehaviourProvider {
    private final List<BaseManagementAction> manageActions, movementActions;

    public ManageBehaviourProvider() {
        manageActions = new LinkedList<>();
        manageActions.add(new CustomizeFaceAction());
        manageActions.add(new EquipmentAction());
        manageActions.add(new ComeHereAction());
        manageActions.add(new FaceMeAction());

        movementActions = new LinkedList<>();
        movementActions.add(new MovementStaticAction());
        movementActions.add(new MovementHouseAction());
        movementActions.add(new MovementVillageAction());
    }

    public static boolean canManage(Creature performer, Creature target) {
        return performer.getPower() >= 2 && NpcTemplate.npcTemplateIds.contains(target.getTemplate().getTemplateId());
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
        if (!canManage(performer, target)) return null;

        LinkedList<ActionEntry> manage = new LinkedList<>();

        for (BaseManagementAction act : manageActions) {
            if (act.canUse(performer, target))
                manage.add(act.getActionEntry());
        }

        int manageEntries = manage.size();

        LinkedList<ActionEntry> movement = new LinkedList<>();

        for (BaseManagementAction act : movementActions) {
            if (act.canUse(performer, target))
                movement.add(act.getActionEntry());
        }

        if (movement.size() > 0) {
            manage.add(new ActionEntry((short) -movement.size(), "Movement", ""));
            manage.addAll(movement);
            manageEntries += 1;
        }

        if (manageEntries > 0) {
            manage.add(0, new ActionEntry((short) -manageEntries, "Manage NPC", ""));
        }

        return manage;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
        return getBehavioursFor(performer, target);
    }
}
