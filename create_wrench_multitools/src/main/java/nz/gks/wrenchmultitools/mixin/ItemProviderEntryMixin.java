package nz.gks.wrenchmultitools.mixin;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.world.item.ItemStack;
import nz.gks.wrenchmultitools.item.WrenchCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@code AllItems.WRENCH.isIn(stack)} is true for our multitools too. This is the check Create
 * uses almost everywhere (block useItemOn, value boxes, overlays, minecart pickup, ...).
 * See {@link WrenchCompat}.
 */
@Mixin(value = ItemProviderEntry.class, remap = false)
public abstract class ItemProviderEntryMixin {

    @Inject(method = "isIn", at = @At("HEAD"), cancellable = true)
    private void wrenchmultitools$multitoolIsCreateWrench(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (WrenchCompat.isMultitool(stack)
                && WrenchCompat.CREATE_WRENCH.equals(((ItemProviderEntry<?, ?>) (Object) this).getId())) {
            cir.setReturnValue(true);
        }
    }
}
