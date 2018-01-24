package net.bdew.wurm.customnpc;

import com.wurmonline.server.bodys.BodyTemplate;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTypes;
import com.wurmonline.server.creatures.ai.CreatureAI;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.skills.SkillList;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.wurmonline.server.creatures.CreatureTypes.C_TYPE_HUMAN;
import static com.wurmonline.server.creatures.CreatureTypes.C_TYPE_INVULNERABLE;

public class NpcTemplate {
    public static Set<CreatureTemplate> npcTemplates = new HashSet<>();
    public static Set<Integer> npcTemplateIds = new HashSet<>();
    public static Set<String> npcTemplateNames = new HashSet<>();

    private static CreatureTemplate createNpcTemplate(String ident, String name, String desc, CreatureAI ai) throws NoSuchFieldException, IllegalAccessException {
        CreatureTemplate tpl = new CreatureTemplateBuilder(ident)
                .name(name)
                .description(desc)
                .modelName("model.creature.humanoid.human.player")
                .types(new int[]{CreatureTypes.C_TYPE_SENTINEL, C_TYPE_INVULNERABLE, C_TYPE_HUMAN})
                .bodyType(BodyTemplate.TYPE_HUMAN)
                .defaultSkills()
                .skill(SkillList.BODY_STRENGTH, 15.0f)
                .skill(SkillList.BODY_CONTROL, 15.0f)
                .skill(SkillList.BODY_STAMINA, 10.0f)
                .skill(SkillList.MIND_LOGICAL, 10.0f)
                .skill(SkillList.MIND_SPEED, 10.0f)
                .skill(SkillList.SOUL_STRENGTH, 99.0f)
                .skill(SkillList.SOUL_DEPTH, 24.0f)
                .skill(SkillList.WEAPONLESS_FIGHTING, 40.0f)
                .vision((short) 5)
                .dimension((short) 180, (short) 20, (short) 35)
                .deathSounds("sound.death.male", "sound.death.female")
                .hitSounds("sound.combat.hit.male", "sound.combat.hit.female")
                .naturalArmour(1)
                .damages(1.0f, 2.0f, 0.0f, 0.0f, 0.0f)
                .speed(0.8f)
                .moveRate(0)
                .maxHuntDist(3)
                .aggressive(0)
                .meatMaterial(Materials.MATERIAL_MEAT_HUMAN)
                .baseCombatRating(99f)
                .maxGroupAttackSize(4)
                .build();

        ReflectionUtil.setPrivateField(tpl, ReflectionUtil.getField(CreatureTemplate.class, "hasHands"), true);

        tpl.setCreatureAI(ai);

        CustomNpcMod.logInfo(String.format("Registered %s - id=%d", ident, tpl.getTemplateId()));

        return tpl;
    }

    static void registerTemplate() throws NoSuchFieldException, IllegalAccessException {
        npcTemplates.add(createNpcTemplate("bdew.npc.custom", "Custom NPC", "A relatively normal person stands here waiting for something to happen.", new CustomAIScript()));

        npcTemplates.stream().mapToInt(CreatureTemplate::getTemplateId).forEach(npcTemplateIds::add);
        npcTemplates.stream().map(CreatureTemplate::getName).forEach(npcTemplateNames::add);
    }
}
