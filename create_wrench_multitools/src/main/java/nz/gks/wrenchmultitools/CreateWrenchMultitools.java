package nz.gks.wrenchmultitools;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import nz.gks.wrenchmultitools.item.ModItems;
import org.slf4j.Logger;

/**
 * Create: Wrench Multitools main class.
 *
 * The @Mod annotation must match the mod_id in gradle.properties.
 */
@Mod(CreateWrenchMultitools.MODID)
public class CreateWrenchMultitools {

    public static final String MODID = "createwrenchmultitools";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateWrenchMultitools(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Create: Wrench Multitools loading...");

        // Register all items (the wrenches and smithing template)
        ModItems.register(modEventBus);

        // Add our items to creative tabs
        modEventBus.addListener(this::addCreative);
    }

    /**
     * Add our items to creative tabs. We use the Tools tab as the primary home,
     * and also register them to Create's own tab if you want (see commented block below).
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        var tabKey = event.getTabKey();

        // Vanilla "Tools & Utilities" tab
        if (tabKey == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.COPPER_WRENCH.get());
            event.accept(ModItems.IRON_WRENCH.get());
            event.accept(ModItems.BRASS_WRENCH.get());
            event.accept(ModItems.DIAMOND_WRENCH.get());
            event.accept(ModItems.NETHERITE_WRENCH.get());
            event.accept(ModItems.WRENCH_UPGRADE_TEMPLATE.get());
        }

        // Optional: also surface in Create's tab. Uncomment if desired.
        // if (tabKey.location().equals(ResourceLocation.fromNamespaceAndPath("create", "base"))) {
        //     event.accept(ModItems.COPPER_WRENCH.get());
        //     ...
        // }
    }

    /** Helper for building our mod's ResourceLocations. */
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
