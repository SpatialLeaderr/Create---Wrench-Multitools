package nz.gks.wrenchmultitools.event;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.data.ContraptionPickupLimiting;
import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;
import nz.gks.wrenchmultitools.item.MultitoolWrenchItem;

import java.util.List;

/**
 * Bridges our {@link MultitoolWrenchItem} into Create's wrench-specific behaviours
 * that are gated by strict item-identity checks against {@code AllItems.WRENCH}.
 *
 * Create's own code uses {@code AllItems.WRENCH.isIn(stack)} in several places that
 * ignore the {@code c:tools/wrench} tag, so subclasses alone aren't recognised. Each
 * handler here re-implements the equivalent Create behaviour with our subclass check.
 *
 * Current handlers:
 *   - {@link #onWrenchAttackMinecart(AttackEntityEvent)}: left-click-insta-kill minecart
 *     (mirrors Create's {@code WrenchItem.wrenchInstaKillsMinecarts}).
 *   - {@link #onWrenchPickupMinecartContraption(PlayerInteractEvent.EntityInteract)}:
 *     sneak-right-click-pickup minecart contraption (mirrors Create's
 *     {@code MinecartContraptionItem.wrenchCanBeUsedToPickUpMinecartContraptions}).
 *     This one replicates the full logic in Create's source so behaviour matches
 *     exactly — config gates, pickup-too-big message, PSI reset, advancement award, etc.
 *
 * Registered on the GAME bus because these are runtime gameplay events.
 */
@EventBusSubscriber(modid = CreateWrenchMultitools.MODID)
public class WrenchEventHandlers {

    /**
     * Left-click-attack mirror of Create's {@code wrenchInstaKillsMinecarts}.
     */
    @SubscribeEvent
    public static void onWrenchAttackMinecart(AttackEntityEvent event) {
        Entity target = event.getTarget();
        if (!(target instanceof AbstractMinecart minecart)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof MultitoolWrenchItem)) {
            return;
        }

        if (!player.isCreative()) {
            minecart.hurt(minecart.damageSources().playerAttack(player), 100.0F);
        } else {
            minecart.discard();
        }
    }

    /**
     * Sneak-right-click a minecart contraption to pick it up. Full reimplementation of
     * Create's {@code MinecartContraptionItem.wrenchCanBeUsedToPickUpMinecartContraptions}
     * for our wrench subclass. Kept as close to the original as possible — behaviour,
     * messages, advancements, config gating all preserved.
     *
     * Priority HIGH so we run before Create's version, and we cancel the event so
     * Create's version doesn't then fail its identity check and produce a no-op.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onWrenchPickupMinecartContraption(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if (player == null || entity == null) return;

        // Config gate — respect Create's "survival contraption pickup" setting
        if (!AllConfigs.server().kinetics.survivalContraptionPickup.get() && !player.isCreative()) return;

        // Our wrench check (Create's check is AllItems.WRENCH.isIn(wrench))
        ItemStack wrench = player.getItemInHand(event.getHand());
        if (!(wrench.getItem() instanceof MultitoolWrenchItem)) return;

        // If we right-clicked the contraption entity directly, unwrap to its minecart vehicle
        if (entity instanceof AbstractContraptionEntity) {
            entity = entity.getVehicle();
        }

        if (!(entity instanceof AbstractMinecart cart)) return;
        if (!entity.isAlive()) return;

        // Skip pickup when a deployer fake-player is already riding
        if (player instanceof DeployerFakePlayer dfp && dfp.onMinecartContraption) return;

        AbstractMinecart.Type type = cart.getMinecartType();
        if (type != Type.RIDEABLE && type != Type.FURNACE && type != Type.CHEST) return;

        List<Entity> passengers = cart.getPassengers();
        if (passengers.isEmpty()) return;

        if (!(passengers.get(0) instanceof OrientedContraptionEntity oce)) return;
        Contraption contraption = oce.getContraption();

        // Immovable-blocks check
        if (ContraptionMovementSetting.isNoPickup(contraption.getBlocks().values())) {
            player.displayClientMessage(
                    CreateLang.translateDirect("contraption.minecart_contraption_illegal_pickup")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return;
        }

        // Client side: just acknowledge — server does the actual work
        if (event.getLevel().isClientSide) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        contraption.stop(event.getLevel());

        // Reset portable storage interfaces so they don't leak transient state
        for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair : contraption.getActors()) {
            if (MovementBehaviour.REGISTRY.get(pair.left.state()) instanceof PortableStorageInterfaceMovement psim) {
                psim.reset(pair.right);
            }
        }

        ItemStack generatedStack = MinecartContraptionItem.create(type, oce);
        generatedStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());

        // Too-big check
        if (ContraptionPickupLimiting.isTooLargeForPickup(
                generatedStack.saveOptional(event.getLevel().registryAccess()))) {
            MutableComponent message = CreateLang.translateDirect("contraption.minecart_contraption_too_big")
                    .withStyle(ChatFormatting.RED);
            player.displayClientMessage(message, true);
            return;
        }

        // Advancement for picking up large carts
        if (contraption.getBlocks().size() > 200) {
            AllAdvancements.CART_PICKUP.awardTo(player);
        }

        player.getInventory().placeItemBackInInventory(generatedStack);
        oce.discard();
        entity.discard();

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
