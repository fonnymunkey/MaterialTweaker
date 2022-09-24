package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemSword.class)
public class ItemSwordMixin {
    private boolean checked = false;
    private boolean overrideRepair = false;
    private ItemStack repairItemStack;

    @Inject(at = @At(value = "HEAD"), method = "getIsRepairable", cancellable = true)
    private void materialtweaker_getIsRepairable(ItemStack toRepair, ItemStack repairInput, CallbackInfoReturnable<Boolean> cir) {
        if(!this.checked) this.checkOverrides();
        if(this.overrideRepair) cir.setReturnValue(net.minecraftforge.oredict.OreDictionary.itemMatches(repairItemStack, repairInput, true));
    }

    private void checkOverrides() {
        this.checked = true;
        try {
            String[] entry = CustomConfigHandler.getItemOverrideRepairs(((Item)(Object)this).getRegistryName().toString());
            if(entry!=null) {
                this.repairItemStack = new ItemStack(Item.getByNameOrId(entry[0]), 1, entry[1].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(entry[1]));
                this.overrideRepair = true;
            }
        }
        catch(Exception ex) {
            MaterialTweaker.LOGGER.log(Level.WARN, MaterialTweaker.MODID + ": " + "Error in force overriding repair material: " + ex);
        }
    }
}
