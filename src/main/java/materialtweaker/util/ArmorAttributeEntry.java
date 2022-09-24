package materialtweaker.util;

public class ArmorAttributeEntry {
    public final int durabilityFactor;
    public final int damageReductionFeet;
    public final int damageReductionLegs;
    public final int damageReductionChest;
    public final int damageReductionHead;
    public final int enchantability;
    public final float toughness;
    public ArmorAttributeEntry(int durabilityFactor, int damageReductionFeet, int damageReductionLegs, int damageReductionChest, int damageReductionHead, int enchantability, float toughness) {
        this.durabilityFactor = durabilityFactor;
        this.damageReductionFeet = damageReductionFeet;
        this.damageReductionLegs = damageReductionLegs;
        this.damageReductionChest = damageReductionChest;
        this.damageReductionHead = damageReductionHead;
        this.enchantability = enchantability;
        this.toughness = toughness;
    }
}
