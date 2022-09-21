package materialtweaker.mixin;

import materialtweaker.handlers.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.ToolMaterial.class)
public abstract class ItemToolMaterialMixin {

    @Shadow(remap = false) private ItemStack repairMaterial;
    private int harvestLevelReplace;
    private int maxUsesReplace;
    private float efficiencyReplace;
    private float attackDamageReplace;
    private int enchantabilityReplace;

    private boolean checked = false;
    private boolean replacedAttributes = false;
    private boolean replacedRepair = false;

    @Inject(at = @At(value = "HEAD"), method = "getMaxUses", cancellable = true)
    private void materialtweaker_tool_getMaxUses(CallbackInfoReturnable<Integer> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedAttributes) cir.setReturnValue(this.maxUsesReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getEfficiency", cancellable = true)
    private void materialtweaker_tool_getEfficiency(CallbackInfoReturnable<Float> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedAttributes) cir.setReturnValue(this.efficiencyReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getAttackDamage", cancellable = true)
    private void materialtweaker_tool_getAttackDamage(CallbackInfoReturnable<Float> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedAttributes) cir.setReturnValue(this.attackDamageReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getHarvestLevel", cancellable = true)
    private void materialtweaker_tool_getHarvestLevel(CallbackInfoReturnable<Integer> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedAttributes) cir.setReturnValue(this.harvestLevelReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getEnchantability", cancellable = true)
    private void materialtweaker_tool_getEnchantability(CallbackInfoReturnable<Integer> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedAttributes) cir.setReturnValue(this.enchantabilityReplace);
    }

    @Inject(at = @At(value = "HEAD"), method = "getRepairItem", cancellable = true)
    private void materialtweaker_tool_getRepairItem(CallbackInfoReturnable<Item> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial.getItem());
    }
    @Inject(at = @At(value = "HEAD"), method = "getRepairItemStack", remap = false, cancellable = true)
    private void materialtweaker_tool_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
        if(!this.checked) this.checkReplacementsTool();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial);
    }

    private void checkReplacementsTool() {
        this.checked = true;
        try {
            for(String[] entry : ConfigHandler.server.getToolMaterialAttributesList()) {
                if(((Item.ToolMaterial)(Object)this).name().equals(entry[0])) {
                    this.harvestLevelReplace = Integer.parseInt(entry[1]);
                    this.maxUsesReplace = Integer.parseInt(entry[2]);
                    this.efficiencyReplace = Float.parseFloat(entry[3]);
                    this.attackDamageReplace = Float.parseFloat(entry[4]);
                    this.enchantabilityReplace = Integer.parseInt(entry[5]);
                    this.replacedAttributes = true;
                    break;
                }
            }
            for(String[] entry : ConfigHandler.server.getToolMaterialRepairList()) {
                if(((Item.ToolMaterial)(Object)this).name().equals(entry[0])) {
                    this.repairMaterial = new ItemStack(Item.getByNameOrId(entry[1]), 1, entry[2].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(entry[2]));
                    this.replacedRepair = true;
                    break;
                }
            }
        }
        catch(Exception ex) {
            System.out.println("Caught exception while checking tool material replacement: " + ex);
        }
    }
}
