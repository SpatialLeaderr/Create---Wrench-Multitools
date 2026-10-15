package nz.gks.wrenchmultitools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import nz.gks.wrenchmultitools.item.WrenchCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Switching a Deployer's mode on a moving contraption checks
 * {@code stack.getItem().equals(AllItems.WRENCH.get())}. Accept a multitool there too.
 * See {@link WrenchCompat}.
 */
@Mixin(value = DeployerMovingInteraction.class, remap = false)
public abstract class DeployerMovingInteractionMixin {

    @WrapOperation(method = "handlePlayerInteraction", at = @At(value = "INVOKE",
            target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z"))
    private boolean wrenchmultitools$multitoolSwitchesMode(Object held, Object wrench, Operation<Boolean> original) {
        return original.call(held, wrench) || (WrenchCompat.isMultitool(held) && WrenchCompat.isCreateWrench(wrench));
    }
}
