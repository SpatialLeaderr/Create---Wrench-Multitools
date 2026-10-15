package nz.gks.wrenchmultitools.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Lets our multitools pass Create's "is this the Create wrench?" checks.
 *
 * Create checks for its wrench by exact item identity in about 30 gameplay places
 * ({@code AllItems.WRENCH.isIn(stack)}, plus three direct item comparisons), never by the
 * {@code c:tools/wrench} tag. For tagged third-party wrenches, Create instead runs
 * {@code WrenchEventHandler.useOwnWrenchLogicForCreateBlocks}: it intercepts a plain
 * right-click on any IWrenchable block BEFORE the block sees it and only calls the generic
 * {@code onWrenched}, and cancels the click. So for a multitool:
 *   - blocks with their own wrench handling in {@code useItemOn} (Belt, Deployer, Funnel,
 *     Sequenced Gearshift, Ejector, Packager, Toggle Latch, Threshold Switch, Elevator
 *     Contact, Girder, Valve Handle, train bogeys and stations, ...) never run it;
 *   - Create's other click handlers never see the click either, even the ones that do
 *     accept {@code c:tools/wrench} (for example merging Mechanical Crafter inputs by
 *     clicking the edge of their backs, via EdgeInteractionHandler).
 * Sneak + right-click was unaffected because sneaking skips the block for both.
 *
 * The mixins in {@code nz.gks.wrenchmultitools.mixin} make every one of those checks accept a
 * multitool, so it takes the exact same route as Create's own wrench: block first, then
 * {@code WrenchItem.useOn}. That also makes Create skip the third-party bypass for us.
 */
public final class WrenchCompat {

    /** Registry id of Create's wrench. Compared by id so we never touch AllItems during its init. */
    public static final ResourceLocation CREATE_WRENCH = ResourceLocation.fromNamespaceAndPath("create", "wrench");

    private WrenchCompat() {
    }

    public static boolean isMultitool(ItemStack stack) {
        return stack.getItem() instanceof MultitoolWrenchItem;
    }

    public static boolean isMultitool(Object item) {
        return item instanceof MultitoolWrenchItem;
    }

    public static boolean isCreateWrench(Object item) {
        return item instanceof Item i && CREATE_WRENCH.equals(BuiltInRegistries.ITEM.getKey(i));
    }
}
