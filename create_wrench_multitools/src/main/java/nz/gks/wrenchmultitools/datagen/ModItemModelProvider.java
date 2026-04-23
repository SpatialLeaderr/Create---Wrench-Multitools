package nz.gks.wrenchmultitools.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;
import nz.gks.wrenchmultitools.item.ModItems;

/**
 * Generates item models.
 *
 * The five tier wrenches use HAND-WRITTEN 3D models (copied/modified from Create's
 * wrench.json) stored in src/main/resources/assets/createwrenchmultitools/models/item/.
 * We do NOT generate those here — the manual models take precedence.
 *
 * Only the flat Wrench Upgrade Template icon is generated automatically.
 *
 * Textures:
 *   src/main/resources/assets/createwrenchmultitools/textures/item/<tier>_wrench.png
 *   src/main/resources/assets/createwrenchmultitools/textures/item/wrench_upgrade_template.png
 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateWrenchMultitools.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // NOTE: tier wrenches intentionally skipped — see class javadoc.
        simple(ModItems.WRENCH_UPGRADE_TEMPLATE.getId().getPath());
    }

    private void simple(String name) {
        singleTexture(name,
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0",
                CreateWrenchMultitools.rl("item/" + name));
    }
}
