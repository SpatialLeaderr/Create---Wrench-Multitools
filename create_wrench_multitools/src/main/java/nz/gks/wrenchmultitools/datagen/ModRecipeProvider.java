package nz.gks.wrenchmultitools.datagen;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;
import nz.gks.wrenchmultitools.item.ModItems;

import java.util.concurrent.CompletableFuture;

/**
 * Generates recipes:
 *   - Smithing upgrade chain: Create Wrench -> Copper -> Iron -> Brass -> Diamond -> Netherite
 *   - Crafting recipe for the Wrench Upgrade Template (netherite-template style)
 *
 * Ingredient chain (using Create mod items):
 *   COPPER   = template + create:wrench    + copper sheet
 *   IRON     = template + copper_wrench    + iron ingot
 *   BRASS    = template + iron_wrench      + brass ingot (via create:brass_ingot)
 *   DIAMOND  = template + brass_wrench     + diamond
 *   NETHERITE= vanilla netherite template + diamond_wrench + netherite ingot
 *
 * NOTE: If Create changes its item registry paths, adjust the ResourceLocations below.
 */
public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        // Resolve Create-provided items by ResourceLocation.
        // These are pulled from the live registry at datagen time.
        Item createWrench    = getItem("create", "wrench");
        Item copperSheet     = getItem("create", "copper_sheet");
        Item brassIngot      = getItem("create", "brass_ingot");

        // ===== Smithing template crafting recipe =====
        // Standard "netherite-template style" — diamonds + netherrack around a Create Wrench.
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WRENCH_UPGRADE_TEMPLATE.get())
                .pattern("DTD")
                .pattern("DND")
                .pattern("DDD")
                .define('D', net.minecraft.world.item.Items.DIAMOND)
                .define('T', createWrench)
                .define('N', net.minecraft.world.item.Items.NETHERRACK)
                .unlockedBy("has_create_wrench", has(createWrench))
                .save(recipeOutput, CreateWrenchMultitools.rl("wrench_upgrade_template"));

        // ===== Upgrade chain (smithing table) =====
        smithingUpgrade(recipeOutput,
                ModItems.WRENCH_UPGRADE_TEMPLATE.get(),
                createWrench,
                copperSheet,
                ModItems.COPPER_WRENCH.get(),
                "copper_wrench_smithing");

        smithingUpgrade(recipeOutput,
                ModItems.WRENCH_UPGRADE_TEMPLATE.get(),
                ModItems.COPPER_WRENCH.get(),
                Items.IRON_INGOT,
                ModItems.IRON_WRENCH.get(),
                "iron_wrench_smithing");

        smithingUpgrade(recipeOutput,
                ModItems.WRENCH_UPGRADE_TEMPLATE.get(),
                ModItems.IRON_WRENCH.get(),
                brassIngot,
                ModItems.BRASS_WRENCH.get(),
                "brass_wrench_smithing");

        smithingUpgrade(recipeOutput,
                ModItems.WRENCH_UPGRADE_TEMPLATE.get(),
                ModItems.BRASS_WRENCH.get(),
                Items.DIAMOND,
                ModItems.DIAMOND_WRENCH.get(),
                "diamond_wrench_smithing");

        // Netherite wrench uses vanilla's netherite upgrade template, matching convention.
        smithingUpgrade(recipeOutput,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                ModItems.DIAMOND_WRENCH.get(),
                Items.NETHERITE_INGOT,
                ModItems.NETHERITE_WRENCH.get(),
                "netherite_wrench_smithing");
    }

    /** Build a smithing transform recipe: template + base + addition -> result. */
    private void smithingUpgrade(RecipeOutput recipeOutput,
                                 Item template, Item base, Item addition,
                                 Item result, String name) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(template),
                        Ingredient.of(base),
                        Ingredient.of(addition),
                        RecipeCategory.TOOLS,
                        result
                )
                .unlocks("has_" + BuiltInRegistries.ITEM.getKey(base).getPath(), has(base))
                .save(recipeOutput, CreateWrenchMultitools.rl(name));
    }

    private static Item getItem(String namespace, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            throw new IllegalStateException("Expected item " + id + " not found — is Create mod on the classpath?");
        }
        return item;
    }
}
