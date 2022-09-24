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
public class ItemArmorArmorMaterialMixin {

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
    @Inject(at = @At(value = "RETURN"), method = "getDurability", cancellable = true)
    private void materialtweaker_armor_getDurability(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
        //System.out.println("Checking armor getDurability: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue((cir.getReturnValueI()/this.maxDamageFactor)*this.maxDamageFactorReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getDamageReductionAmount", cancellable = true)
    private void materialtweaker_armor_getDamageReductionAmount(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
        //System.out.println("Checking armor getDamageReductionAmount: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.damageReductionAmountReplace[armorType.getIndex()]);
    }
    @Inject(at = @At(value = "HEAD"), method = "getEnchantability", cancellable = true)
    private void materialtweaker_armor_getEnchantability(CallbackInfoReturnable<Integer> cir) {
        //System.out.println("Checking armor getEnchantability: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.enchantabilityReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getToughness", cancellable = true)
    private void materialtweaker_armor_getToughness(CallbackInfoReturnable<Float> cir) {
        //System.out.println("Checking armor getToughness: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedAttributes) this.checkAttributesArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.toughnessReplace);
    }

    @Inject(at = @At(value = "HEAD"), method = "getRepairItem", cancellable = true)
    private void materialtweaker_armor_getRepairItem(CallbackInfoReturnable<Item> cir) {
        //System.out.println("Checking armor getRepairItem: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedRepair) this.checkRepairsArmor();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial.getItem());
    }
    @Inject(at = @At(value = "HEAD"), method = "getRepairItemStack", remap = false, cancellable = true)
    private void materialtweaker_armor_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
        //System.out.println("Checking armor getRepairItemStack: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedRepair) this.checkRepairsArmor();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial);
    }
    @Inject(at = @At(value = "HEAD"), method = "setRepairItem", remap = false, cancellable = true)
    private void materialtweaker_armor_setRepairItem(CallbackInfoReturnable<ItemArmor.ArmorMaterial> cir) {
        //System.out.println("Checking armor setRepairItem: " + ((ItemArmor.ArmorMaterial)(Object)this).name());
        if(!this.checkedRepair) this.checkRepairsArmor();
        if(this.replacedRepair) cir.setReturnValue((ItemArmor.ArmorMaterial)(Object)this);
    }

    private void checkAttributesArmor() {
        this.checkedAttributes = true;
        try {
            ArmorAttributeEntry attributeEntry = CustomConfigHandler.getArmorAttributes(((ItemArmor.ArmorMaterial)(Object)this).name());

            //System.out.println("Checking armor material attributes: " + ((ItemArmor.ArmorMaterial)(Object)this).name());

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

            //System.out.println("Checking armor material repairs: " + ((ItemArmor.ArmorMaterial)(Object)this).name());

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
