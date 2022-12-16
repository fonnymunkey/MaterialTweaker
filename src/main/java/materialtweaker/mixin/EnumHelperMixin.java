package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.ForgeConfigHandler;
import net.minecraftforge.common.util.EnumHelper;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnumHelper.class)
public abstract class EnumHelperMixin {

	@ModifyArgs(method = "addToolMaterial", at = @At(value= "INVOKE", target = "Lnet/minecraftforge/common/util/EnumHelper;addEnum(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Enum;"), remap = false)
	private static void materialtweaker_addToolMaterial(Args args) {
		//Args (Class enum, String name, Object[] properties)
		//Object[] properties (int harvestLevel, int maxUses, float efficiency, float damage, int enchantability)
		if(ForgeConfigHandler.server.printInfo) {
			try {
				String name = args.get(1);
				Object[] properties = args.get(2);
				MaterialTweaker.LOGGER.log(Level.INFO, MaterialTweaker.MODID + ": " + "Tool Material Registering," +
						" Name: " + name +
						" HarvestLevel: " + properties[0] +
						" MaxUses: " + properties[1] +
						" Efficiency: " + properties[2] +
						" Damage: " + properties[3] +
						" Enchantability: " + properties[4]);
			}
			catch(Exception ex) {
				MaterialTweaker.LOGGER.log(Level.ERROR, MaterialTweaker.MODID + ": " + "Tool Material Attribute printing failed: " + ex);
			}
		}
	}

	@ModifyArgs(method = "addArmorMaterial", at = @At(value= "INVOKE", target = "Lnet/minecraftforge/common/util/EnumHelper;addEnum(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Enum;"), remap = false)
	private static void materialtweaker_addArmorMaterial(Args args) {
		//Args (Class enum, String name, Object[] properties)
		//Object[] properties (String textureName, int durabilityFactor, int[4] reductionAmounts, int enchantability, SoundEvent soundEquip, float toughness)
		if(ForgeConfigHandler.server.printInfo) {
			try {
				String name = args.get(1);
				Object[] properties = args.get(2);
				MaterialTweaker.LOGGER.log(Level.INFO, MaterialTweaker.MODID + ": " + "Armor Material Registering," +
						" Name: " + name +
						" DurabilityFactor: " + properties[1] +
						" ReductionAmounts: " + ((int[])(properties[2]))[0] + " " + ((int[])(properties[2]))[1] + " " + ((int[])(properties[2]))[2] + " " + ((int[])(properties[2]))[3] +
						" Enchantability: " + properties[3] +
						" Toughness: " + properties[5]);
			}
			catch(Exception ex) {
				MaterialTweaker.LOGGER.log(Level.ERROR, MaterialTweaker.MODID + ": " + "Armor Material Attribute printing failed: " + ex);
			}
		}
	}
}