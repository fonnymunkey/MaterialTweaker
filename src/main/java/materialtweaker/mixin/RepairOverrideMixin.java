package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import net.minecraft.item.*;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ItemTool.class, ItemElytra.class, ItemArmor.class, ItemSword.class, Item.class, ItemShield.class})
public abstract class RepairOverrideMixin {
    private boolean checked = false;
    private boolean overrideRepair = false;
    private boolean strict = false;
    private ItemStack repairItemStack;

    @Inject(
            method = "getIsRepairable",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_getIsRepairable(ItemStack toRepair, ItemStack repairInput, CallbackInfoReturnable<Boolean> cir) {
        if(toRepair.isEmpty() || repairInput.isEmpty()) return;
        if(!this.checked) this.checkOverrides();
        if(this.overrideRepair) {
            cir.setReturnValue(net.minecraftforge.oredict.OreDictionary.itemMatches(repairItemStack, repairInput, this.strict));
        }
    }

    private void checkOverrides() {
        this.checked = true;
        try {
            String[] entry = CustomConfigHandler.getItemOverrideRepairs(((Item)(Object)this).getRegistryName().toString());
            if(entry != null) {
                this.repairItemStack = new ItemStack(Item.getByNameOrId(entry[0]), 1, entry[1].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(entry[1]));
                this.strict = !entry[1].equals("*");
                this.overrideRepair = true;
            }
        }
        catch(Exception ex) {
            MaterialTweaker.LOGGER.log(Level.WARN, MaterialTweaker.MODID + ": " + "Error in force overriding repair material: " + ex);
        }
    }
}