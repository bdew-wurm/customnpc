package net.bdew.wurm.customnpc.movement;

import com.wurmonline.server.creatures.ai.PathTile;

@FunctionalInterface
public interface PathCostFunc {
    float apply(PathTile p);
}
