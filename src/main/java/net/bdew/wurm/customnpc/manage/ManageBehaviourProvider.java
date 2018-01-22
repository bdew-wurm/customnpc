package net.bdew.wurm.customnpc.manage;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import net.bdew.wurm.customnpc.NpcTemplate;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.LinkedList;
import java.util.List;

public class ManageBehaviourProvider implements BehaviourProvider {
    private final MovementStaticAction movementStatic;
    private final MovementVillageAction movementVillage;
    private final MovementHouseAction movementHouse;
    private final CustomizeFaceAction customizeFace;

    public ManageBehaviourProvider() {
        movementStatic = new MovementStaticAction();
        movementHouse = new MovementHouseAction();
        movementVillage = new MovementVillageAction();
        customizeFace = new CustomizeFaceAction();
        ModActions.registerActionPerformer(movementStatic);
        ModActions.registerActionPerformer(movementHouse);
        ModActions.registerActionPerformer(movementVillage);
        ModActions.registerActionPerformer(customizeFace);
    }

    static boolean canManage(Creature performer, Creature target) {
        return performer.getPower() >= 2 && NpcTemplate.npcTemplateIds.contains(target.getTemplate().getTemplateId());
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
        if (!canManage(performer, target)) return null;

        LinkedList<ActionEntry> manage = new LinkedList<>();

        manage.add(customizeFace.actionEntry);

        int manageEntries = manage.size();

        LinkedList<ActionEntry> movement = new LinkedList<>();
        if (movementStatic.canUse(performer, target)) movement.add(movementStatic.actionEntry);
        if (movementVillage.canUse(performer, target)) movement.add(movementVillage.actionEntry);
        if (movementHouse.canUse(performer, target)) movement.add(movementHouse.actionEntry);

        if (movement.size() > 0) {
            manage.add(new ActionEntry((short) -movement.size(), "Movement", ""));
            manage.addAll(movement);
            manageEntries += 1;
        }

        manage.add(0, new ActionEntry((short) -manageEntries, "Manage NPC", ""));

        return manage;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
        return getBehavioursFor(performer, target);
    }
}
