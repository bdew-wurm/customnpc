package net.bdew.wurm.customnpc;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ClassEdits {
    public static void createPathFinderCustom(ClassPool classPool) throws NotFoundException, CannotCompileException {
        // Grab vanilla StaticPathFinderNPC class
        CtClass ctPathFinderNPC = classPool.getCtClass("com.wurmonline.server.creatures.ai.StaticPathFinderNPC");

        // Make it non-final
        ctPathFinderNPC.setModifiers(ctPathFinderNPC.getModifiers() & ~Modifier.FINAL);

        // Change private fields to protected so we can access them from our subclass
        for (CtField field : ctPathFinderNPC.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.PRIVATE) != 0)
                field.setModifiers((field.getModifiers() & ~Modifier.PRIVATE) | Modifier.PROTECTED);
        }

        // Make a class that extends it
        CtClass ctPathFinderCustom = classPool.makeClass("com.wurmonline.server.creatures.ai.StaticPathFinderCustomNPC", ctPathFinderNPC);

        // Add field for our custom cost function
        CtField ctPathFinderCostFunc = CtField.make("private net.bdew.wurm.customnpc.movement.PathCostFunc costFunc;", ctPathFinderCustom);
        ctPathFinderCustom.addField(ctPathFinderCostFunc);

        // Add field for raycast block setting
        CtField ctAvoidRaycast = CtField.make("private boolean avoidRaycast;", ctPathFinderCustom);
        ctPathFinderCustom.addField(ctAvoidRaycast);


        // Override findPath method to grab the cost function and store it for later
        CtMethod mFindPathOverride = CtMethod.make(
                "public com.wurmonline.server.creatures.ai.Path findPath(com.wurmonline.server.creatures.Creature aCreature, int startTileX, int startTileY, int endTileX, int endTileY, boolean surf, int areaSz) {" +
                        "this.costFunc = net.bdew.wurm.customnpc.Hooks.getCostFunction(aCreature);" +
                        "this.avoidRaycast = net.bdew.wurm.customnpc.Hooks.getAvoidRaycast(aCreature);" +
                        "return super.findPath(aCreature, startTileX, startTileY, endTileX, endTileY, surf, areaSz);" +
                        "}", ctPathFinderCustom);
        ctPathFinderCustom.addMethod(mFindPathOverride);

        // Override raycast method to block it from running when it shouldn't
        CtMethod mRayCastOverride = CtMethod.make(
                "com.wurmonline.server.creatures.ai.Path rayCast(int startTileX, int startTileY, int endTileX, int endTileY, boolean aSurfaced) {" +
                        "if (this.avoidRaycast) throw new com.wurmonline.server.creatures.ai.NoPathException(\"raycast not allowed\");" +
                        "return super.rayCast(startTileX, startTileY, endTileX, endTileY, aSurfaced);" +
                        "}"
                , ctPathFinderCustom);
        ctPathFinderCustom.addMethod(mRayCastOverride);

        // Clone step2 method from original class
        CtMethod mStep2Original = ctPathFinderNPC.getMethod("step2", "()I");
        CtMethod mStep2Clone = new CtMethod(mStep2Original, ctPathFinderCustom, null);

        // Change the clone to call our cost function
        mStep2Clone.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("getCost"))
                    m.replace("$_ = this.costFunc.apply(now);");
            }
        });

        // And put the cloned method in our custom class
        ctPathFinderCustom.addMethod(mStep2Clone);

        ctPathFinderCustom.debugWriteFile();
    }
}
