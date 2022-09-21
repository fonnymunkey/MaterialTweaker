package materialtweaker.mixin;

import materialtweaker.handlers.ConfigHandler;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
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

    private boolean checked = false;
    private boolean replacedAttributes = false;
    private boolean replacedRepair = false;

    //Durability = max_damage_array[armorindex] * maxdamagefactor
    @Inject(at = @At(value = "RETURN"), method = "getDurability", cancellable = true)
    private void materialtweaker_armor_getDurability(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
        if(!this.checked) this.checkReplacementsArmor();
        if(this.replacedAttributes) cir.setReturnValue((cir.getReturnValueI()/this.maxDamageFactor)*this.maxDamageFactorReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getDamageReductionAmount", cancellable = true)
    private void materialtweaker_armor_getDamageReductionAmount(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
        if(!this.checked) this.checkReplacementsArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.damageReductionAmountReplace[armorType.getIndex()]);
    }
    @Inject(at = @At(value = "HEAD"), method = "getEnchantability", cancellable = true)
    private void materialtweaker_armor_getEnchantability(CallbackInfoReturnable<Integer> cir) {
        if(!this.checked) this.checkReplacementsArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.enchantabilityReplace);
    }
    @Inject(at = @At(value = "HEAD"), method = "getToughness", cancellable = true)
    private void materialtweaker_armor_getToughness(CallbackInfoReturnable<Float> cir) {
        if(!this.checked) this.checkReplacementsArmor();
        if(this.replacedAttributes) cir.setReturnValue(this.toughnessReplace);
    }

    @Inject(at = @At(value = "HEAD"), method = "getRepairItem", cancellable = true)
    private void materialtweaker_armor_getRepairItem(CallbackInfoReturnable<Item> cir) {
        if(!this.checked) this.checkReplacementsArmor();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial.getItem());
    }
    @Inject(at = @At(value = "HEAD"), method = "getRepairItemStack", remap = false, cancellable = true)
    private void materialtweaker_armor_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
        if(!this.checked) this.checkReplacementsArmor();
        if(this.replacedRepair) cir.setReturnValue(this.repairMaterial);
    }

    private void checkReplacementsArmor() {
        this.checked = true;
        try {
            for(String[] entry : ConfigHandler.server.getArmorMaterialAttributesList()) {
                if(((ItemArmor.ArmorMaterial)(Object)this).name().equals(entry[0])) {
                    this.maxDamageFactorReplace = Integer.parseInt(entry[1]);
                    this.damageReductionAmountReplace = new int[]{Integer.parseInt(entry[2]),Integer.parseInt(entry[3]),Integer.parseInt(entry[4]),Integer.parseInt(entry[5])};
                    this.enchantabilityReplace= Integer.parseInt(entry[6]);
                    this.toughnessReplace = Float.parseFloat(entry[7]);
                    this.replacedAttributes = true;
                    break;
                }
            }
            for(String[] entry : ConfigHandler.server.getArmorMaterialRepairList()) {
                if(((ItemArmor.ArmorMaterial)(Object)this).name().equals(entry[0])) {
                    this.repairMaterial = new ItemStack(Item.getByNameOrId(entry[1]), 1, entry[2].equals("*") ? net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE : Integer.parseInt(entry[2]));
                    this.replacedRepair = true;
                    break;
                }
            }
        }
        catch(Exception ex) {
            System.out.println("Caught exception while checking armor material replacement: " + ex);
        }
    }
}
