package net.bdew.wurm.customnpc;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
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

            CtClass ctCreature = classPool.getCtClass("com.wurmonline.server.creatures.Creature");
            ctCreature.getMethod("doNew", "(IZFFFILjava/lang/String;BBBZB)Lcom/wurmonline/server/creatures/Creature;")
                    .instrument(new ExprEditor() {
                        @Override
                        public void edit(NewExpr c) throws CannotCompileException {
                            if (c.getClassName().equals("com.wurmonline.server.creatures.Creature")) {
                                logInfo(String.format("Patched instance creation in Creature.doNew at %d", c.getLineNumber()));
                                c.replace("if (net.bdew.wurm.customnpc.Hooks.isNpcTemplate($1)) $_ = new com.wurmonline.server.creatures.Npc($$); else $_ = new com.wurmonline.server.creatures.Creature($$);");
                            }
                        }
                    });

            ctCreature.getMethod("mayAccessHold", "(Lcom/wurmonline/server/creatures/Creature;)Z")
                    .insertAfter("$_ = $_ || net.bdew.wurm.customnpc.Hooks.canManage($1,this);");

            classPool.getCtClass("com.wurmonline.server.creatures.Creatures")
                    .getMethod("loadAllCreatures", "()I")
                    .instrument(new ExprEditor() {
                        @Override
                        public void edit(NewExpr c) throws CannotCompileException {
                            if (c.getClassName().equals("com.wurmonline.server.creatures.Creature")) {
                                logInfo(String.format("Patched instance creation in Creatures.loadAllCreatures at %d", c.getLineNumber()));
                                c.replace("if (net.bdew.wurm.customnpc.Hooks.isNpcTemplateName(templateName)) $_ = new com.wurmonline.server.creatures.Npc($$); else $_ = new com.wurmonline.server.creatures.Creature($$);");
                            }
                        }
                    });

            CtClass ctCommunicator = classPool.getCtClass("com.wurmonline.server.creatures.Communicator");

            ctCommunicator.getMethod("reallyHandle_CMD_NEW_FACE", "(Ljava/nio/ByteBuffer;)V")
                    .insertBefore("if (net.bdew.wurm.customnpc.Hooks.handleNewFace(this, $1)) return;");

            ctCommunicator.getMethod("reallyHandle_CMD_MOVE_INVENTORY", "(Ljava/nio/ByteBuffer;)V").instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("getDominator")) {
                        m.replace("if (net.bdew.wurm.customnpc.Hooks.canManage(this.player, $0)) {" +
                                "$_ = this.player;" +
                                "} else {" +
                                "$_ = $proceed($$);" +
                                "}");
                        logInfo(String.format("Patched getDominator in Communicator.reallyHandle_CMD_MOVE_INVENTORY at %d", m.getLineNumber()));
                    } else if (m.getMethodName().equals("isReborn")) {
                        m.replace("$_ = $proceed() || net.bdew.wurm.customnpc.Hooks.canManage(this.player, $0);");
                        logInfo(String.format("Patched isReborn in Communicator.reallyHandle_CMD_MOVE_INVENTORY at %d", m.getLineNumber()));
                    }
                }
            });

            classPool.getCtClass("com.wurmonline.server.creatures.Npc")
                    .getMethod("getFace", "()J")
                    .insertBefore("if (net.bdew.wurm.customnpc.Hooks.isNpcTemplate(this.getTemplate())) return net.bdew.wurm.customnpc.Hooks.getFace(this);");

            // Grab vanilla StaticPathFinderNPC class
            CtClass ctPathFinderNPC = classPool.getCtClass("com.wurmonline.server.creatures.ai.StaticPathFinderNPC");

            // Make it non-final
            ctPathFinderNPC.setModifiers(ctPathFinderNPC.getModifiers() & ~Modifier.FINAL);

            // Make a class that extends it
            CtClass ctPathFinderCustom = classPool.makeClass("com.wurmonline.server.creatures.ai.StaticPathFinderCustomNPC", ctPathFinderNPC);

            // Add field for our custom cost function
            CtField ctPathFinderCostFunc = CtField.make("private java.util.function.Function costFunc;", ctPathFinderCustom);
            ctPathFinderCustom.addField(ctPathFinderCostFunc);

            // Override findPath method to grab the cost function and store it for later
            CtMethod mFindPathOverride = CtMethod.make(
                    "public com.wurmonline.server.creatures.ai.Path findPath(com.wurmonline.server.creatures.Creature aCreature, int startTileX, int startTileY, int endTileX, int endTileY, boolean surf, int areaSz) {" +
                            "this.costFunc = net.bdew.wurm.customnpc.Hooks.getCostFunction(aCreature);" +
                            "return super.findPath(aCreature, startTileX, startTileY, endTileX, endTileY, surf, areaSz);" +
                            "}", ctPathFinderCustom);
            ctPathFinderCustom.addMethod(mFindPathOverride);

            // Clone step2 method from original class
            CtMethod mStep2Original = ctPathFinderNPC.getMethod("step2", "()I");
            CtMethod mStep2Clone = new CtMethod(mStep2Original, ctPathFinderCustom, null);

            // Change the clone to call our cost function
            mStep2Clone.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("getCost"))
                        m.replace("$_ = (Float) this.costFunc.apply(now);");
                }
            });

            // And put the cloned method in our custom class
            ctPathFinderCustom.addMethod(mStep2Clone);

            // fixme: save generated class for debugging, remove later
            ctPathFinderCustom.writeFile();

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
            NpcTemplate.registerTemplate();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onServerStarted() {
        ModActions.registerBehaviourProvider(new ManageBehaviourProvider());
    }
}
