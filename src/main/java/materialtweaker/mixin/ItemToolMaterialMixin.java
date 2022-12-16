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
    private boolean checkedRepairs = false;
    private boolean replacedAttributes = false;
    private boolean replacedRepair = false;


    @Inject(
            method = "getMaxUses",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_tool_getMaxUses(CallbackInfoReturnable<Integer> cir) {
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.maxUsesReplace);
    }
    @Inject(
            method = "getEfficiency",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_tool_getEfficiency(CallbackInfoReturnable<Float> cir) {
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.efficiencyReplace);
    }
    @Inject(
            method = "getAttackDamage",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_tool_getAttackDamage(CallbackInfoReturnable<Float> cir) {
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.attackDamageReplace);
    }
    @Inject(
            method = "getHarvestLevel",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_tool_getHarvestLevel(CallbackInfoReturnable<Integer> cir) {
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.harvestLevelReplace);
    }
    @Inject(
            method = "getEnchantability",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_tool_getEnchantability(CallbackInfoReturnable<Integer> cir) {
        if(!this.checkedAttributes) this.checkAttributesTool();
        if(this.replacedAttributes) cir.setReturnValue(this.enchantabilityReplace);
    }

    @Inject(
            method = "getRepairItem",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_tool_getRepairItem(CallbackInfoReturnable<Item> cir) {
        if(!this.checkedRepairs) this.checkRepairsTool();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial.getItem());
    }
    @Inject(
            method = "getRepairItemStack",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false)
    public void materialtweaker_tool_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
        if(!this.checkedRepairs) this.checkRepairsTool();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial);
    }
    @Inject(
            method = "setRepairItem",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false)
    public void materialtweaker_tool_setRepairItem(CallbackInfoReturnable<Item.ToolMaterial> cir) {
        if(!this.checkedRepairs) this.checkRepairsTool();
        if(this.replacedRepair) cir.setReturnValue((Item.ToolMaterial)(Object)this);
    }

    private void checkAttributesTool() {
        this.checkedAttributes = true;
        try {
            ToolAttributeEntry attributeEntry = CustomConfigHandler.getToolAttributes(((Item.ToolMaterial)(Object)this).name());

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