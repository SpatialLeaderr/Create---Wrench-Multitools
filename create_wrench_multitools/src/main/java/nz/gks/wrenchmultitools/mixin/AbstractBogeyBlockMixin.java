package nz.gks.wrenchmultitools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nz.gks.wrenchmultitools.item.WrenchCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Bogey style cycling checks {@code stack.is(AllItems.WRENCH.get())}, which bypasses
 * {@code isIn}. Accept a multitool there too. See {@link WrenchCompat}.
 */
@Mixin(AbstractBogeyBlock.class)
public abstract class AbstractBogeyBlockMixin {

    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean wrenchmultitools$multitoolCyclesBogey(ItemStack stack, Item item, Operation<Boolean> original) {
        return original.call(stack, item) || (WrenchCompat.isMultitool(stack) && WrenchCompat.isCreateWrench(item));
    }
}
