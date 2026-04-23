package nz.gks.wrenchmultitools.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;
import nz.gks.wrenchmultitools.item.ModItems;

import java.util.concurrent.CompletableFuture;

/**
 * Adds our wrenches to the vanilla {@code minecraft:enchantable/*} tags so that the
 * enchanting table and anvil actually recognise them as enchantable.
 *
 * Without these tags, {@link nz.gks.wrenchmultitools.item.MultitoolWrenchItem#getEnchantmentValue()}
 * returning a positive number won't help — vanilla enchantments check their
 * {@code supported_items} tag to decide which items they can apply to.
 *
 * We include:
 *   - enchantable/mining        → Efficiency
 *   - enchantable/mining_loot   → Fortune, Silk Touch
 *   - enchantable/vanishing     → Curse of Vanishing
 *
 * We deliberately DON'T add:
 *   - enchantable/durability    → Unbreaking / Mending are meaningless on a durability-free item
 *   - enchantable/weapon, sharp_weapon → these are pickaxe-like tools, not swords
 *
 * NOTE: we build TagKeys from ResourceLocations directly rather than referencing
 * ItemTags.ENCHANTABLE_* constants, because those constants have different names
 * across Minecraft versions. This approach is version-agnostic.
 */
public class ModItemTagsProvider extends ItemTagsProvider {

    private static final TagKey<Item> ENCHANTABLE_MINING      = itemTag("enchantable/mining");
    private static final TagKey<Item> ENCHANTABLE_MINING_LOOT = itemTag("enchantable/mining_loot");
    private static final TagKey<Item> ENCHANTABLE_VANISHING   = itemTag("enchantable/vanishing");

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(path));
    }

    public ModItemTagsProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                               ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreateWrenchMultitools.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        Item[] wrenches = new Item[] {
                ModItems.COPPER_WRENCH.get(),
                ModItems.IRON_WRENCH.get(),
                ModItems.BRASS_WRENCH.get(),
                ModItems.DIAMOND_WRENCH.get(),
                ModItems.NETHERITE_WRENCH.get()
        };

        tag(ENCHANTABLE_MINING).add(wrenches);
        tag(ENCHANTABLE_MINING_LOOT).add(wrenches);
        tag(ENCHANTABLE_VANISHING).add(wrenches);
    }
}
