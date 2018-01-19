package net.bdew.wurm.customnpc;

import com.wurmonline.server.creatures.CreatureTemplate;

public class Hooks {
    public static boolean isNpcTemplate(CreatureTemplate tpl) {
        return NpcTemplate.npcTemplateIds.contains(tpl.getTemplateId());
    }

    public static boolean isNpcTemplateName(String name) {
        return NpcTemplate.npcTemplateNames.contains(name);
    }
}
