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
 * Adds our wrenches to vanilla {@code minecraft:enchantable/*} tags (so they can be
 * enchanted at the enchanting table) and to the NeoForge common {@code c:tools/wrench}
 * tag (so Create recognises them as wrenches).
 *
 * The wrench tag is critical: Create's own {@code WrenchEventHandler} listens for
 * right-clicks on IWrenchable blocks and intercepts them for any item in this tag
 * (when the item isn't itself the vanilla Create wrench). Minecart-contraption pickup
 * uses the same pattern on entity interacts. Without this tag our items are invisible
 * to Create's third-party-wrench support.
 *
 * Enchanting tags:
 *   - enchantable/mining        → Efficiency
 *   - enchantable/mining_loot   → Fortune, Silk Touch
 *   - enchantable/vanishing     → Curse of Vanishing
 *
 * Deliberately NOT added:
 *   - enchantable/durability    → Unbreaking / Mending are meaningless on a durability-free item
 *   - enchantable/weapon, sharp_weapon → these are pickaxe-like tools, not swords
 *
 * NOTE: we build TagKeys from ResourceLocations directly rather than referencing
 * ItemTags.ENCHANTABLE_* or Tags.Items.TOOLS_WRENCH constants, because those constants
 * have different names across Minecraft/NeoForge versions. This approach is version-agnostic.
 */
public class ModItemTagsProvider extends ItemTagsProvider {

    private static final TagKey<Item> ENCHANTABLE_MINING      = minecraftItemTag("enchantable/mining");
    private static final TagKey<Item> ENCHANTABLE_MINING_LOOT = minecraftItemTag("enchantable/mining_loot");
    private static final TagKey<Item> ENCHANTABLE_VANISHING   = minecraftItemTag("enchantable/vanishing");

    /** The {@code c:tools/wrench} tag Create listens for. */
    private static final TagKey<Item> C_TOOLS_WRENCH = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));

    private static TagKey<Item> minecraftItemTag(String path) {
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

        // Identify as a wrench for Create + other mods
        tag(C_TOOLS_WRENCH).add(wrenches);

        // Enchanting eligibility
        tag(ENCHANTABLE_MINING).add(wrenches);
        tag(ENCHANTABLE_MINING_LOOT).add(wrenches);
        tag(ENCHANTABLE_VANISHING).add(wrenches);
    }
}
