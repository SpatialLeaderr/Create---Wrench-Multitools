package nz.gks.wrenchmultitools.item;

/**
 * Defines stats for each wrench tier.
 *
 * miningSpeed: how fast blocks are mined (vanilla reference: wood=2, stone=4, iron=6,
 *              diamond=8, netherite=9). We're giving these tool-like values.
 * enchantability: how good enchantments are (vanilla: wood=15, stone=5, iron=14,
 *                 gold=22, diamond=10, netherite=15).
 */
public enum WrenchTier {
    COPPER("copper",    4.0f, 12),
    IRON("iron",        6.0f, 14),
    BRASS("brass",      7.0f, 18),   // Brass is a Create material — gold-adjacent, so high enchantability
    DIAMOND("diamond",  8.0f, 10),
    NETHERITE("netherite", 9.0f, 15);

    public final String name;
    public final float miningSpeed;
    public final int enchantability;

    WrenchTier(String name, float miningSpeed, int enchantability) {
        this.name = name;
        this.miningSpeed = miningSpeed;
        this.enchantability = enchantability;
    }

    public String itemId() {
        return name + "_wrench";
    }
}
