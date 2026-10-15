package nz.gks.wrenchmultitools.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchHandler;
import net.minecraft.world.item.Item;
import nz.gks.wrenchmultitools.item.WrenchCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The Block Rotation Menu key compares the held item to {@code AllItems.WRENCH.get()} by
 * reference. Report a held multitool as Create's wrench there so the menu opens.
 * See {@link WrenchCompat}.
 */
@Mixin(value = RadialWrenchHandler.class, remap = false)
public abstract class RadialWrenchHandlerMixin {

    @ModifyExpressionValue(method = "onKeyInput", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private static Item wrenchmultitools$multitoolOpensMenu(Item held) {
        return WrenchCompat.isMultitool(held) ? AllItems.WRENCH.get() : held;
    }
}
