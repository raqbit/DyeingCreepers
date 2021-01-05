package it.raqb.dyeingcreepers.server;

import it.raqb.dyeingcreepers.common.capabilities.Capabilities;
import it.raqb.dyeingcreepers.common.capabilities.dyeablecreeper.IDyeable;
import it.raqb.dyeingcreepers.common.network.PacketHandler;
import it.raqb.dyeingcreepers.common.network.packets.SyncDyeablePacket;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // Only handle interaction on server side, sync with SyncDyeablePacket
        if (event.getSide() != LogicalSide.SERVER) {
            return;
        }

        if (!(event.getTarget() instanceof CreeperEntity)) {
            return;
        }

        CreeperEntity creeper = (CreeperEntity) event.getTarget();

        ItemStack heldItem = event.getItemStack();

        if (!(heldItem.getItem() instanceof DyeItem)) {
            return;
        }

        DyeItem dye = (DyeItem) heldItem.getItem();

        Optional<IDyeable> op = creeper.getCapability(Capabilities.DYEABLE).resolve();

        if (!op.isPresent()) {
            event.setCanceled(true);
            return;
        }

        IDyeable dyeable = op.get();

        // Dot not allow dyeing same color it already has
        if (dyeable.getColor() == dye.getDyeColor()) {
            event.setCanceled(true);
            return;
        }

        // Transfer color
        dyeable.setColor(dye.getDyeColor());

        // TODO: somehow integrate this into the capability instead of a separate call
        PacketHandler.sendToPlayersTrackingEntity(creeper, new SyncDyeablePacket(creeper.getEntityId(), dye.getDyeColor().getId()));

        // Only reduce items if player is in survival or adventure mode
        if (!event.getPlayer().isCreative() && !event.getPlayer().isSpectator()) {
            heldItem.shrink(1);
        }

        // Doing this to get "apply" animation like with sheep
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.SUCCESS);
    }

    @SubscribeEvent
    public static void onPlayerTrackEntity(PlayerEvent.StartTracking event) {
        // Only handle on server side, as this is only used to sync state to client
        if (event.getPlayer().getEntityWorld().isRemote) {
            return;
        }

        // Send dyeable sync to player tracking this creeper
        event.getTarget().getCapability(Capabilities.DYEABLE).ifPresent(capability -> {
            PacketHandler.sendToPlayer(
                    event.getPlayer(),
                    new SyncDyeablePacket(event.getTarget().getEntityId(), capability.getColor().getId())
            );
        });
    }
}
