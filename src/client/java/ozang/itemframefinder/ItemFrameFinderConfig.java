package ozang.itemframefinder;


import java.util.Arrays;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;





@Config(name = "item-frame-finder")
public class ItemFrameFinderConfig implements ConfigData {

    float Height = 1.0f;
    float Width = 1.0f;
    float yOffset = 0.0f;
    double searchDistance = 50.0D;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public RENDER_MODE renderMode = RENDER_MODE.EDGES;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public FRAME_TYPES frameType = FRAME_TYPES.NORMAL;

    enum RENDER_MODE {
        EDGES,
        FACES
    }

    enum FRAME_TYPES {
        NORMAL,
        GLOW,
        BOTH
    }


    public List<Preset> presetsPanel = Arrays.asList(new Preset());
    
    public static class Preset {   

        public String name;

        
        public List<String> items;

        public Preset() {
            this("NewPreset", Arrays.asList("minecraft:stone"));
        }

        public Preset(String name, List<String> items) {
            this.name = name;
            this.items = items;
        }
    }


    
}


