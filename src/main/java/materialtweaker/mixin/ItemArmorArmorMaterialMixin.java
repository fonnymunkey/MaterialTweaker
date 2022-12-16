package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import materialtweaker.util.ArmorAttributeEntry;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemArmor.ArmorMaterial.class)
public abstract class ItemArmorArmorMaterialMixin {

    @Shadow @Final private int maxDamageFactor;

    @Shadow(remap = false) public ItemStack repairMaterial;

    private int maxDamageFactorReplace;
    private int[] damageReductionAmountReplace;
    private int enchantabilityReplace;
    private float toughnessReplace;

    private boolean checkedAttributes = false;
    private boolean checkedRepair = false;
    private boolean replacedAttributes = false;
    private boolean replacedRepair = false;


    //Durability = max_damage_array[armorindex] * maxdamagefactor
    @Inject(
            method = "getDurability",
            at = @At(value = "RETURN"),
            cancellable = true)
    public void materialtweaker_armor_getDurability(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue((cir.getReturnValueI()/this.maxDamageFactor)*this.maxDamageFactorReplace);
    }
    @Inject(
            method = "getDamageReductionAmount",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_armor_getDamageReductionAmount(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.damageReductionAmountReplace[armorType.getIndex()]);
    }
    @Inject(
            method = "getEnchantability",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_armor_getEnchantability(CallbackInfoReturnable<Integer> cir) {
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.enchantabilityReplace);
    }
    @Inject(
            method = "getToughness",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_armor_getToughness(CallbackInfoReturnable<Float> cir) {
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.toughnessReplace);
    }

    @Inject(
            method = "getRepairItem",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void materialtweaker_armor_getRepairItem(CallbackInfoReturnable<Item> cir) {
        if(!this.checkedRepair) this.checkRepairsArmor();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial.getItem());
    }
    @Inject(
            method = "getRepairItemStack",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false)
    public void materialtweaker_armor_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
        if(!this.checkedRepair) this.checkRepairsArmor();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial);
    }
    @Inject(
            method = "setRepairItem",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false)
    public void materialtweaker_armor_setRepairItem(CallbackInfoReturnable<ItemArmor.ArmorMaterial> cir) {
        if(!this.checkedRepair) this.checkRepairsArmor();
        if(this.replacedRepair) cir.setReturnValue((ItemArmor.ArmorMaterial)(Object)this);
    }

    private void checkAttributesArmor() {
        this.checkedAttributes = true;
        try {
            ArmorAttributeEntry attributeEntry = CustomConfigHandler.getArmorAttributes(((ItemArmor.ArmorMaterial)(Object)this).name());

            if(attributeEntry!=null) {
                this.maxDamageFactorReplace = attributeEntry.durabilityFactor;
                this.damageReductionAmountReplace = new int[]{attributeEntry.damageReductionFeet, attributeEntry.damageReductionLegs, attributeEntry.damageReductionChest, attributeEntry.damageReductionHead};
                this.enchantabilityReplace = attributeEntry.enchantability;
                this.toughnessReplace = attributeEntry.toughness;
                this.replacedAttributes = true;
            }
        }
        catch(Exception ex) {
            MaterialTweaker.LOGGER.log(Level.ERROR, MaterialTweaker.MODID + ": " + "Caught exception while checking armor material attributes: " + ex);
        }
    }

    private void checkRepairsArmor() {
        this.checkedRepair = true;
        try {
            String[] repairEntry = CustomConfigHandler.getArmorRepairs(((ItemArmor.ArmorMaterial)(Object)this).name());

            if(repairEntry!=null) {
                this.repairMaterial = new ItemStack(Item.getByNameOrId(repairEntry[0]), 1, repairEntry[1].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(repairEntry[1]));
                this.replacedRepair = true;
            }
        }
        catch(Exception ex) {
            MaterialTweaker.LOGGER.log(Level.ERROR, MaterialTweaker.MODID + ": " + "Caught exception while checking armor material repairs: " + ex);
        }
    }
}