package materialtweaker.handlers;

import materialtweaker.core.MaterialTweaker;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

@Config(modid = MaterialTweaker.MODID)
public class ConfigHandler {
	
	@Config.Comment("Server Config")
	@Config.Name("Server")
	public static final ServerConfig server = new ServerConfig();
	
	public static class ServerConfig {
		private ArrayList<String[]> toolMaterialAttributesList = new ArrayList<>();
		private ArrayList<String[]> toolMaterialRepairList = new ArrayList<>();
		private ArrayList<String[]> armorMaterialAttributesList = new ArrayList<>();
		private ArrayList<String[]> armorMaterialRepairList = new ArrayList<>();
		private ArrayList<String[]> itemOverrideRepairList = new ArrayList<>();

		@Config.Comment("Print information about material attributes when they are registered.")
		@Config.Name("Print Material Info")
		public boolean printInfo = false;

		@Config.Comment(
				"List of Tool Materials and their attributes to be changed. " +
						"(String materialName, " +
						"int harvestLevel, " +
						"int maxUses, " +
						"float efficiency, " +
						"float damage, " +
						"int enchantability)")
		@Config.Name("Tool Material Attribute List")
		@Config.RequiresMcRestart
		public String[] toolMaterialAttributes = {""};

		@Config.Comment(
				"List of Tool Materials and their replacement repair material. " +
						"(String materialName, " +
						"String repairItem, " +
						"int repairItemMetadata (* for any))")
		@Config.Name("Tool Material Repair List")
		@Config.RequiresMcRestart
		public String[] toolMaterialRepair = {""};

		@Config.Comment(
				"List of Armor Materials and their attributes to be changed. " +
						"(String materialName, " +
						"int durabilityFactor, " +
						"int damageReductionFeet, " +
						"int damageReductionLegs, " +
						"int damageReductionChest, " +
						"int damageReductionHead, " +
						"int enchantability, " +
						"float toughness)")
		@Config.Name("Armor Material Attribute List")
		@Config.RequiresMcRestart
		public String[] armorMaterialAttributes = {""};

		@Config.Comment(
				"List of Armor Materials and their replacement repair material. " +
						"(String materialName, " +
						"String repairItem, " +
						"int repairItemMetadata (* for any))")
		@Config.Name("Armor Material Repair List")
		@Config.RequiresMcRestart
		public String[] armorMaterialRepair = {""};

		@Config.Comment(
				"List of individual items to force override their repair material. " +
						"(String itemName, " +
						"String repairItemName, " +
						"int repairItemMetadata (* for any))")
		@Config.Name("Direct Item Repair Override List")
		@Config.RequiresMcRestart
		public String[] itemOverrideRepair = {""};

		public ArrayList<String[]> getToolMaterialAttributesList() {
			if (toolMaterialAttributesList.isEmpty() && toolMaterialAttributes.length > 0) {
				for (String entry : toolMaterialAttributes) {
					toolMaterialAttributesList.add(cleanEntry(entry));
				}
			}
			return toolMaterialAttributesList;
		}

		public ArrayList<String[]> getToolMaterialRepairList() {
			if (toolMaterialRepairList.isEmpty() && toolMaterialRepair.length > 0) {
				for (String entry : toolMaterialRepair) {
					toolMaterialRepairList.add(cleanEntry(entry));
				}
			}
			return toolMaterialRepairList;
		}

		public ArrayList<String[]> getArmorMaterialAttributesList() {
			if (armorMaterialAttributesList.isEmpty() && armorMaterialAttributes.length > 0) {
				for (String entry : armorMaterialAttributes) {
					armorMaterialAttributesList.add(cleanEntry(entry));
				}
			}
			return armorMaterialAttributesList;
		}

		public ArrayList<String[]> getArmorMaterialRepairList() {
			if (armorMaterialRepairList.isEmpty() && armorMaterialRepair.length > 0) {
				for (String entry : armorMaterialRepair) {
					armorMaterialRepairList.add(cleanEntry(entry));
				}
			}
			return armorMaterialRepairList;
		}

		public ArrayList<String[]> getItemOverrideRepairList() {
			if (itemOverrideRepairList.isEmpty() && itemOverrideRepair.length > 0) {
				for (String entry : itemOverrideRepair) {
					itemOverrideRepairList.add(cleanEntry(entry));
				}
			}
			return itemOverrideRepairList;
		}

		private String[] cleanEntry(String entry) {
			return Arrays.stream(entry.split(",")).map(String::trim).toArray(String[]::new);
		}
	}

	
	@Mod.EventBusSubscriber(modid = MaterialTweaker.MODID)
	private static class EventHandler{
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(MaterialTweaker.MODID)) ConfigManager.sync(MaterialTweaker.MODID, Config.Type.INSTANCE);
		}
	}
}