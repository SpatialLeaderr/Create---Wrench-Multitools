package nz.gks.wrenchmultitools.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * All of our items are registered here using DeferredRegister.
 *
 * Each wrench tier:
 *   - stacksTo(1): can't stack
 *   - rarity set by tier (common/uncommon/rare/epic)
 *   - ENCHANTABLE component: enables enchanting at the enchanting table with tier value
 *   - NO MAX_DAMAGE component: means no durability
 *
 * The smithing template is a vanilla SmithingTemplateItem used for all upgrade recipes
 * (except netherite, which uses vanilla's netherite upgrade template).
 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreateWrenchMultitools.MODID);

    // ===== Wrenches =====
    public static final DeferredItem<MultitoolWrenchItem> COPPER_WRENCH =
            registerWrench(WrenchTier.COPPER);

    public static final DeferredItem<MultitoolWrenchItem> IRON_WRENCH =
            registerWrench(WrenchTier.IRON);

    public static final DeferredItem<MultitoolWrenchItem> BRASS_WRENCH =
            registerWrench(WrenchTier.BRASS);

    public static final DeferredItem<MultitoolWrenchItem> DIAMOND_WRENCH =
            registerWrench(WrenchTier.DIAMOND);

    public static final DeferredItem<MultitoolWrenchItem> NETHERITE_WRENCH =
            registerWrench(WrenchTier.NETHERITE);

    // ===== Smithing Template =====
    // Custom "Wrench Multitool Upgrade" template used for copper/iron/brass/diamond recipes.
    // For netherite we use vanilla's netherite upgrade template, matching Minecraft convention.
    public static final DeferredItem<SmithingTemplateItem> WRENCH_UPGRADE_TEMPLATE =
            ITEMS.register("wrench_upgrade_template", () -> new SmithingTemplateItem(
                    net.minecraft.network.chat.Component.translatable(
                            "item.createwrenchmultitools.wrench_upgrade_template.applies_to"),
                    net.minecraft.network.chat.Component.translatable(
                            "item.createwrenchmultitools.wrench_upgrade_template.ingredients"),
                    net.minecraft.network.chat.Component.translatable(
                            "upgrade.createwrenchmultitools.wrench_upgrade"),
                    net.minecraft.network.chat.Component.translatable(
                            "item.createwrenchmultitools.wrench_upgrade_template.base_slot_description"),
                    net.minecraft.network.chat.Component.translatable(
                            "item.createwrenchmultitools.wrench_upgrade_template.additions_slot_description"),
                    // Empty icon lists — the default slot backgrounds will show (gear + ingot).
                    java.util.List.of(),
                    java.util.List.of()
            ));

    private static DeferredItem<MultitoolWrenchItem> registerWrench(WrenchTier tier) {
        return ITEMS.register(tier.itemId(), () -> new MultitoolWrenchItem(
                new Item.Properties()
                        .stacksTo(1)
                        // NOTE: enchantability is provided by MultitoolWrenchItem#getEnchantmentValue()
                        // (1.21.1 doesn't have DataComponents.ENCHANTABLE — that was added in 1.21.2).
                        // NOTE: no .durability() set, so the wrench has no durability bar and no uses limit.
                        .rarity(rarityFor(tier))
                        .fireResistant(), // QoL — wrenches don't burn. Remove if you want.
                tier
        ));
    }

    private static net.minecraft.world.item.Rarity rarityFor(WrenchTier tier) {
        return switch (tier) {
            case COPPER -> net.minecraft.world.item.Rarity.COMMON;
            case IRON, BRASS -> net.minecraft.world.item.Rarity.UNCOMMON;
            case DIAMOND -> net.minecraft.world.item.Rarity.RARE;
            case NETHERITE -> net.minecraft.world.item.Rarity.EPIC;
        };
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
