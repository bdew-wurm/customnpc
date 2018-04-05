package net.bdew.wurm.customnpc.movement.script;

import com.wurmonline.math.TilePos;
import com.wurmonline.server.creatures.Creature;
import net.bdew.wurm.customnpc.config.ConfigLoadError;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MovementZone extends MovementSquareArea {
    private TilePos neCorner, swCorner;


    @Override
    protected TilePos getNECorner() {
        return neCorner;
    }

    @Override
    protected TilePos getSWCorner() {
        return swCorner;
    }

    public void setNeCorner(TilePos neCorner) {
        this.neCorner = neCorner;
    }

    public void setSwCorner(TilePos swCorner) {
        this.swCorner = swCorner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readFromObject(Map<String, Object> data) throws ConfigLoadError {
        if (data.containsKey("CornerNE") && data.containsKey("CornerSW")) {
            List<Integer> ne = (List<Integer>) data.get("CornerNE");
            List<Integer> sw = (List<Integer>) data.get("CornerSW");
            if (ne.size() != 2 || sw.size() != 2)
                throw new ConfigLoadError("CornerNE and CornerSW must contain 2 coordinates");
            neCorner = TilePos.fromXY(ne.get(0), ne.get(1));
            swCorner = TilePos.fromXY(sw.get(0), sw.get(1));
        } else throw new ConfigLoadError("Missing keys CornerNE and CornerSW");
        super.readFromObject(data);
    }

    @Override
    public void saveToFile(PrintStream file) {
        file.println("    Type: Zone");
        file.println(String.format("    CornerNE: [%d, %d]", neCorner.x, neCorner.y));
        file.println(String.format("    CornerSW: [%d, %d]", swCorner.x, swCorner.y));
        super.saveToFile(file);
    }


    @Override
    public void configBML(Creature creature, StringBuilder buf) {
        buf.append("label{type='bold';text=\"Movement: Wander in zone\"}");
        buf.append("harray{label{text='NE Corner: X='};input{id='neX'; text=\"").append(neCorner.x).append("\";maxchars='5'};label{text=' Y='};input{id='neY'; text=\"").append(neCorner.y).append("\";maxchars='5'}}");
        buf.append("harray{label{text='SW Corner: X='};input{id='swX'; text=\"").append(swCorner.x).append("\";maxchars='5'};label{text=' Y='}input{id='swY'; text=\"").append(swCorner.y).append("\";maxchars='5'}}");
        super.configBML(creature, buf);
    }

    @Override
    public void processConfig(Creature creature, Properties properties) {
        neCorner = TilePos.fromXY(Integer.parseInt(properties.getProperty("neX")), Integer.parseInt(properties.getProperty("neY")));
        swCorner = TilePos.fromXY(Integer.parseInt(properties.getProperty("swX")), Integer.parseInt(properties.getProperty("swY")));
        super.processConfig(creature, properties);
    }

    @Override
    public String toString() {
        return String.format("MovementZone(%d,%d - %d,%d)", neCorner.x, neCorner.y, swCorner.x, swCorner.y);
    }
}
