package net.bdew.wurm.customnpc;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;
import net.bdew.wurm.customnpc.manage.ManageBehaviourProvider;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomNpcMod implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener {
    private static final Logger logger = Logger.getLogger("CustomNpcMod");

    public static void logException(String msg, Throwable e) {
        if (logger != null)
            logger.log(Level.SEVERE, msg, e);
    }

    public static void logWarning(String msg) {
        if (logger != null)
            logger.log(Level.WARNING, msg);
    }

    public static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
    }

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void preInit() {
        try {
            ClassPool classPool = HookManager.getInstance().getClassPool();

            classPool.getCtClass("com.wurmonline.server.creatures.Creature")
                    .getMethod("doNew", "(IZFFFILjava/lang/String;BBBZB)Lcom/wurmonline/server/creatures/Creature;")
                    .instrument(new ExprEditor() {
                        @Override
                        public void edit(NewExpr c) throws CannotCompileException {
                            if (c.getClassName().equals("com.wurmonline.server.creatures.Creature")) {
                                logInfo(String.format("Patched Creature.doNew at %d", c.getLineNumber()));
                                c.replace("if (net.bdew.wurm.customnpc.Hooks.isNpcTemplate($1)) $_ = new com.wurmonline.server.creatures.Npc($$); else $_ = new com.wurmonline.server.creatures.Creature($$);");
                            };
                        }
                    });

            classPool.getCtClass("com.wurmonline.server.creatures.Creatures")
                    .getMethod("loadAllCreatures", "()I")
                    .instrument(new ExprEditor() {
                        @Override
                        public void edit(NewExpr c) throws CannotCompileException {
                            if (c.getClassName().equals("com.wurmonline.server.creatures.Creature")) {
                                logInfo(String.format("Patched Creatures.loadAllCreatures at %d", c.getLineNumber()));
                                c.replace("if (net.bdew.wurm.customnpc.Hooks.isNpcTemplateName(templateName)) $_ = new com.wurmonline.server.creatures.Npc($$); else $_ = new com.wurmonline.server.creatures.Creature($$);");
                            };
                        }
                    });

            classPool.getCtClass("com.wurmonline.server.creatures.Communicator")
                    .getMethod("reallyHandle_CMD_NEW_FACE","(Ljava/nio/ByteBuffer;)V")
                    .insertBefore("if (net.bdew.wurm.customnpc.Hooks.handleNewFace($1)) return;");


            classPool.getCtClass("com.wurmonline.server.creatures.Npc")
                    .getMethod("getFace","()J")
                    .insertBefore("if (net.bdew.wurm.customnpc.Hooks.isNpcTemplate(this.getTemplate())) return net.bdew.wurm.customnpc.Hooks.getFace(this);");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        ModActions.init();
    }

    @Override
    public void onItemTemplatesCreated() {
        try {
            NpcTemplate.registerTempalate();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onServerStarted() {
        ModActions.registerBehaviourProvider(new ManageBehaviourProvider());
    }
}
