package it.raqb.dyeingcreepers;

import it.raqb.dyeingcreepers.capabilities.Capabilities;
import it.raqb.dyeingcreepers.capabilities.dyeablecreeper.Dyeable;
import it.raqb.dyeingcreepers.capabilities.dyeablecreeper.IDyeable;
import it.raqb.dyeingcreepers.network.PacketHandler;
import it.raqb.dyeingcreepers.network.packets.SyncDyeablePacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof CreeperEntity) {
            event.addCapability(Dyeable.Provider.NAME, new Dyeable.Provider());
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
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

        // TODO: somehow integrate this into the capability instead of separate
        PacketHandler.sendToPlayersTrackingEntity(creeper, new SyncDyeablePacket(creeper.getEntityId(), dye.getDyeColor().getId()));

        // TODO: Should not do this in creative mode, but DyeItem gets that for free?
        heldItem.shrink(1);

        // Doing this to get "apply" animation like with sheep
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.SUCCESS);
    }

    @SubscribeEvent
    public static void onPlayerTrackEntity(PlayerEvent.StartTracking event) {
        if(event.getPlayer().getEntityWorld().isRemote) {
           return;
        }

        event.getTarget().getCapability(Capabilities.DYEABLE).ifPresent(capability -> {
            PacketHandler.sendToPlayer(
                    event.getPlayer(),
                    new SyncDyeablePacket(event.getTarget().getEntityId(), capability.getColor().getId())
            );
        });
    }
}
