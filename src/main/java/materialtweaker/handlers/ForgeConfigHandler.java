package materialtweaker.handlers;

import materialtweaker.core.MaterialTweaker;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = MaterialTweaker.MODID)
public class ForgeConfigHandler {
	
	@Config.Comment("Server Config")
	@Config.Name("Server")
	public static final ServerConfig server = new ServerConfig();
	
	public static class ServerConfig {
		@Config.Comment("Print information about material attributes when they are registered.")
		@Config.Name("Print Material Info")
		public boolean printInfo = false;

	}
	
	@Mod.EventBusSubscriber(modid = MaterialTweaker.MODID)
	private static class EventHandler{
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(MaterialTweaker.MODID)) ConfigManager.sync(MaterialTweaker.MODID, Config.Type.INSTANCE);
		}
	}
}