package net.bdew.wurm.customnpc.movement;

import com.wurmonline.math.TilePos;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

import java.util.Objects;

public final class TilePosLayer {
    public final int x, y, floor;
    public final boolean onSurface;


    public TilePosLayer(int x, int y, boolean onSurface) {
        this(x, y, onSurface, 0);
    }

    public TilePosLayer(int x, int y, boolean onSurface, int floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
        this.onSurface = onSurface;
    }

    public TilePosLayer(TilePos tp, boolean onSurface) {
        this(tp, onSurface, 0);
    }


    public TilePosLayer(TilePos tp, boolean onSurface, int floor) {
        this(tp.x, tp.y, onSurface, floor);
    }

    public static TilePosLayer from(Item item) {
        return new TilePosLayer(item.getTileX(), item.getTileY(), item.isOnSurface(), item.getFloorLevel());
    }

    public static TilePosLayer from(Creature creature) {
        return new TilePosLayer(creature.getTileX(), creature.getTileY(), creature.isOnSurface(), creature.getFloorLevel());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof TilePosLayer)) return false;
        TilePosLayer that = (TilePosLayer) o;
        return x == that.x && y == that.y && floor == that.floor && onSurface == that.onSurface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, floor, onSurface);
    }

    @Override
    public String toString() {
        return String.format("TilePosLayer(x=%d, y=%d, onSurface=%s, floor=%d)", x, y, onSurface, floor);
    }
}
