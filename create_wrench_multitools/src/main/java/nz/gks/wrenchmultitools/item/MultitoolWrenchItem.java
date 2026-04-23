package nz.gks.wrenchmultitools.item;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A wrench that behaves like Create's wrench (rotation/configure/dismantle) but also:
 *   - Breaks any block at the tier's mining speed
 *   - Is correct-tool-for-drops on any block (so drops are always given)
 *   - Has no durability (no MAX_DAMAGE component is set, so the bar never appears)
 *   - Is enchantable (via getEnchantmentValue() + enchantable/* item tags — see ModItemTagsProvider)
 *
 * By extending {@link WrenchItem}, we inherit Create's useOn() behavior for free:
 *   - Right-click rotatable blocks to rotate
 *   - Sneak-right-click to dismantle
 *   - Interact with Create configuration contexts
 *
 * NOTE: If a future Create version marks WrenchItem as final, swap this to implement
 * Create's IWrenchable-aware logic manually via onItemUseFirst / useOn. For Create
 * 6.0.x this class is extensible.
 */
public class MultitoolWrenchItem extends WrenchItem {

    private final WrenchTier tier;

    public MultitoolWrenchItem(Properties properties, WrenchTier tier) {
        super(properties);
        this.tier = tier;
    }

    public WrenchTier getTier() {
        return tier;
    }

    /**
     * Makes the wrench correct for mining any block. Combined with a high
     * destroy speed below, this means any block drops when mined with this tool.
     */
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    /**
     * All blocks get the tier's mining speed. We don't differentiate by material
     * tag — the whole point is "multitool".
     */
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return tier.miningSpeed;
    }

    /**
     * Per-tier enchantability. Higher = better enchantment rolls at the enchanting
     * table. In 1.21.1 this is still the vanilla method (not a DataComponent).
     * Note: for enchantments to actually apply, the wrench also needs to be in the
     * relevant minecraft:enchantable/* tags — see ModItemTagsProvider.
     */
    @Override
    public int getEnchantmentValue() {
        return tier.enchantability;
    }

    /**
     * NeoForge also exposes a stack-taking version via IItemExtension that the
     * enchanting table actually calls. The default delegates to the no-arg version
     * above, but we override it explicitly in case a patch changes that contract.
     */
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return tier.enchantability;
    }

    /**
     * Override the default isEnchantable check. The vanilla default requires the
     * item to have a MAX_DAMAGE component (i.e. durability) — but we're intentionally
     * durability-free. Returning true here lets the enchanting table accept us even
     * though we have no damage bar.
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    /**
     * No durability damage on block breaking. Belt-and-braces: we also don't set
     * MAX_DAMAGE in ModItems, so this shouldn't be called anyway, but explicit is
     * better than implicit.
     */
    @Override
    public boolean mineBlock(ItemStack stack, net.minecraft.world.level.Level level,
                             BlockState state, net.minecraft.core.BlockPos pos,
                             net.minecraft.world.entity.LivingEntity entity) {
        // Intentionally do NOT call super — Create's WrenchItem damages on mine,
        // which we don't want because we're durability-free.
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false; // Just to be safe — never show a durability bar.
    }
}
