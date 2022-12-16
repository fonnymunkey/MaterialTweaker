package materialtweaker.core;

import materialtweaker.handlers.CustomConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MaterialTweaker.MODID, version = MaterialTweaker.VERSION, name = MaterialTweaker.NAME)
public class MaterialTweaker
{
    public static final String MODID = "materialtweaker";
    public static final String VERSION = "1.1.1";
    public static final String NAME = "MaterialTweaker";

    public static final Logger LOGGER = LogManager.getLogger();
	
	@Instance(MODID)
	public static MaterialTweaker instance;

    //Force all files to generate if they haven't already
    @Mod.EventHandler
    public static void postInitialization(FMLPostInitializationEvent e) {
        CustomConfigHandler.forceInitializeLazyFiles();
    }
}
