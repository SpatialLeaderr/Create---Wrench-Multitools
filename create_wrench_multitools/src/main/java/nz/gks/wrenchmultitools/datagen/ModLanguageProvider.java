package nz.gks.wrenchmultitools.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;
import nz.gks.wrenchmultitools.item.ModItems;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output) {
        super(output, CreateWrenchMultitools.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Wrenches
        add(ModItems.COPPER_WRENCH.get(),    "Copper Multitool Wrench");
        add(ModItems.IRON_WRENCH.get(),      "Iron Multitool Wrench");
        add(ModItems.BRASS_WRENCH.get(),     "Brass Multitool Wrench");
        add(ModItems.DIAMOND_WRENCH.get(),   "Diamond Multitool Wrench");
        add(ModItems.NETHERITE_WRENCH.get(), "Netherite Multitool Wrench");

        // Smithing template + its tooltip lines (the 4 extra keys come from SmithingTemplateItem)
        add(ModItems.WRENCH_UPGRADE_TEMPLATE.get(), "Smithing Template");
        add("upgrade.createwrenchmultitools.wrench_upgrade", "Wrench Multitool Upgrade");
        add("item.createwrenchmultitools.wrench_upgrade_template.applies_to", "Wrench");
        add("item.createwrenchmultitools.wrench_upgrade_template.ingredients", "Copper Sheet, Iron, Brass, or Diamond");
        add("item.createwrenchmultitools.wrench_upgrade_template.base_slot_description", "Add a wrench");
        add("item.createwrenchmultitools.wrench_upgrade_template.additions_slot_description", "Add upgrade material");
    }
}
