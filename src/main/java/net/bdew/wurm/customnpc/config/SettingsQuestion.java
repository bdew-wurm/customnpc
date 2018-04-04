package net.bdew.wurm.customnpc.config;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.questions.Question;
import net.bdew.wurm.customnpc.CustomAIData;
import net.bdew.wurm.customnpc.CustomNpcMod;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;

import java.util.Properties;

public class SettingsQuestion implements ModQuestion {
    private final Creature responder, npc;
    private final CustomAIData data;

    public SettingsQuestion(Creature responder, Creature npc) {
        this.responder = responder;
        this.npc = npc;
        data = (CustomAIData) npc.getCreatureAIData();
    }

    @Override
    public void sendQuestion(Question question) {
        final StringBuilder buf = new StringBuilder(ModQuestions.getBmlHeader(question));

        buf.append("harray{label{text='Name:'};input{id='name'; text=\"").append(npc.getName()).append("\";maxchars='50'}}");

        buf.append("text{text=''}");

        buf.append("harray{label{text='Sex:'};");
        buf.append("radio{group='sex';id='0';text='Male';").append(npc.getSex() == 0 ? "selected='true'" : "").append("}");
        buf.append("radio{group='sex';id='1';text='Female';").append(npc.getSex() == 1 ? "selected='true'" : "").append("}");
        buf.append("}");

        buf.append("text{text=''}");

        data.getConfig().getMovementScript().configBML(npc, buf);

        buf.append("text{text=''}");

        buf.append(ModQuestions.createAnswerButton2(question, "Save"));

        question.getResponder().getCommunicator().sendBml(400, 600, true, true, buf.toString(), 200, 200, 200, question.getTitle());
    }

    @Override
    public void answer(Question question, Properties answers) {
        Communicator comm = responder.getCommunicator();

        boolean changed = false;

        String newName = answers.getProperty("name");
        if (newName != null && newName.length() > 0 && !newName.equals(npc.getName())) {
            try {
                changed = true;
                npc.setVisible(false);
                npc.setName(newName);
                ReflectionUtil.callPrivateMethod(npc.getStatus(), ReflectionUtil.getMethod(DbCreatureStatus.class, "saveCreatureName", new Class[]{String.class}), newName);
                data.getConfig().setName(newName);
                data.configUpdated();
            } catch (Exception e) {
                CustomNpcMod.logException("Error setting name", e);
            }
        }

        byte newSex = Byte.parseByte(answers.getProperty("sex"));
        if (newSex != npc.getSex()) {
            if (!changed) {
                changed = true;
                npc.setVisible(false);
            }
            npc.setSex(newSex);
        }

        if (changed) {
            npc.setVisible(true);
        }

        data.getConfig().getMovementScript().processConfig(npc, answers);
        data.configUpdated();
    }

    public static void send(Creature player, Creature npc) {
        ModQuestions.createQuestion(player, "NPC Settings", "", -10, new SettingsQuestion(player, npc)).sendQuestion();
    }
}
