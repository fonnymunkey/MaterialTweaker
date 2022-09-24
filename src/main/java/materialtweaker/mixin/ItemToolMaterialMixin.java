package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import materialtweaker.util.ToolAttributeEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;
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

    private boolean checkedAttributes = false;
    private boolean checkedRepairs= false;
    private boolean replacedAttributes = false;
    private boolean replacedRepair = false;

    @Inject(at = @At(value = "HEAD"), method = "getMaxUses", cancellable = true)
    private void materialtweaker_tool_getMaxUses(CallbackInfoReturnable<Integer> cir) {
        //System.out.println("Checking tool getMaxUses: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.maxUsesReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getEfficiency", cancellable = true)
    private void materialtweaker_tool_getEfficiency(CallbackInfoReturnable<Float> cir) {
        //System.out.println("Checking tool getEfficiency: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.efficiencyReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getAttackDamage", cancellable = true)
    private void materialtweaker_tool_getAttackDamage(CallbackInfoReturnable<Float> cir) {
        //System.out.println("Checking tool getAttackDamage: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.attackDamageReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getHarvestLevel", cancellable = true)
    private void materialtweaker_tool_getHarvestLevel(CallbackInfoReturnable<Integer> cir) {
        //System.out.println("Checking tool getHarvestLevel: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.harvestLevelReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getEnchantability", cancellable = true)
    private void materialtweaker_tool_getEnchantability(CallbackInfoReturnable<Integer> cir) {
        //System.out.println("Checking tool getEnchantability: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.enchantabilityReplace);
    }

    @Inject(at = @At(value = "HEAD"), method = "getRepairItem", cancellable = true)
    private void materialtweaker_tool_getRepairItem(CallbackInfoReturnable<Item> cir) {
        //System.out.println("Checking tool getRepairItem: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedRepairs) this.checkRepairsTool();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial.getItem());
    }
    @Inject(at = @At(value = "HEAD"), method = "getRepairItemStack", remap = false, cancellable = true)
    private void materialtweaker_tool_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
        //System.out.println("Checking tool getRepairItemStack: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedRepairs) this.checkRepairsTool();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial);
    }
    @Inject(at = @At(value = "HEAD"), method = "setRepairItem", remap = false, cancellable = true)
    private void materialtweaker_tool_setRepairItem(CallbackInfoReturnable<Item.ToolMaterial> cir) {
        //System.out.println("Checking tool setRepairItem: " + ((Item.ToolMaterial)(Object)this).name());
        if(!this.checkedRepairs) this.checkRepairsTool();
        if(this.replacedRepair) cir.setReturnValue((Item.ToolMaterial)(Object)this);
    }

    private void checkAttributesTool() {
        this.checkedAttributes = true;
        try {
            ToolAttributeEntry attributeEntry = CustomConfigHandler.getToolAttributes(((Item.ToolMaterial)(Object)this).name());

            //System.out.println("Checking tool material attributes: " + ((Item.ToolMaterial)(Object)this).name());

            if(attributeEntry!=null) {
                this.harvestLevelReplace = attributeEntry.harvestLevel;
                this.maxUsesReplace = attributeEntry.maxUses;
                this.efficiencyReplace = attributeEntry.efficiency;
                this.attackDamageReplace = attributeEntry.attackDamage;
                this.enchantabilityReplace = attributeEntry.enchantability;
                this.replacedAttributes = true;
            }
        }
        catch(Exception ex) {
            MaterialTweaker.LOGGER.log(Level.WARN, MaterialTweaker.MODID + ": " + "Caught exception while checking tool material attributes: " + ex);
        }
    }

    private void checkRepairsTool() {
        this.checkedRepairs = true;
        try {
            String[] repairEntry = CustomConfigHandler.getToolRepairs(((Item.ToolMaterial)(Object)this).name());

            //System.out.println("Checking tool material repairs: " + ((Item.ToolMaterial)(Object)this).name());

            if(repairEntry!=null) {
                this.repairMaterial = new ItemStack(Item.getByNameOrId(repairEntry[0]), 1, repairEntry[1].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(repairEntry[1]));
                this.replacedRepair = true;
            }
        }
        catch(Exception ex) {
            MaterialTweaker.LOGGER.log(Level.WARN, MaterialTweaker.MODID + ": " + "Caught exception while checking tool material repairs: " + ex);
        }
    }
}
