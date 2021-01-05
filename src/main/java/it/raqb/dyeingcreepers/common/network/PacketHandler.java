package it.raqb.dyeingcreepers.common.network;

import it.raqb.dyeingcreepers.DyeingCreepers;
import it.raqb.dyeingcreepers.common.network.packets.SyncDyeablePacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
            DyeingCreepers.resource("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        HANDLER.registerMessage(0x00, SyncDyeablePacket.class, SyncDyeablePacket::encode, SyncDyeablePacket::decode, SyncDyeablePacket::onMessage);
    }

    private static void send(PacketDistributor.PacketTarget target, Object message) {
        HANDLER.send(target, message);
    }

    public static void sendToPlayer(PlayerEntity player, Object message) {
        send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }

    public static void sendToPlayersTrackingEntity(Entity entity, Object message) {
        send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
}