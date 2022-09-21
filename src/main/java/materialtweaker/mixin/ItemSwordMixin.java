package materialtweaker.mixin;

import materialtweaker.handlers.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemSword.class)
public class ItemSwordMixin {
    private boolean checked = false;
    private boolean overrideRepair = false;
    private boolean strict = false;
    private ItemStack repairItemStack;

    @Inject(at = @At(value = "HEAD"), method = "getIsRepairable", cancellable = true)
    private void materialtweaker_getIsRepairable(ItemStack toRepair, ItemStack repairInput, CallbackInfoReturnable<Boolean> cir) {
        if(!this.checked) this.checkOverrides();
        if(this.overrideRepair) cir.setReturnValue(net.minecraftforge.oredict.OreDictionary.itemMatches(repairItemStack, repairInput, strict));
    }

    private void checkOverrides() {
        this.checked = true;
        try {
            for(String[] entry : ConfigHandler.server.getItemOverrideRepairList()) {
                if(((Item)(Object)this).getRegistryName().toString().equals(entry[0])) {
                    this.strict = !entry[2].equals("*");
                    this.repairItemStack = new ItemStack(Item.getByNameOrId(entry[1]), 1, entry[2].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(entry[2]));
                    this.overrideRepair = true;
                    break;
                }
            }
        }
        catch(Exception ex) {
            System.out.println("Error in force overriding repair material: " + ex);
        }
    }
}
