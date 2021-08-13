package it.raqb.dyeingcreepers.fabric;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VersionCheck implements DedicatedServerModInitializer, ClientModInitializer {
    public static final int PROTOCOL_VERSION = 1;
    public static final Identifier VERSION_CHECK = DyeingCreepersMod.resource("version_check");
    private static final LiteralText INCORRECT_VERSION = new LiteralText(String.format("Please install DyeingCreepers %d.x.x to play on this server.", PROTOCOL_VERSION));

    @Override
    public void onInitializeServer() {
        ServerLoginNetworking.registerGlobalReceiver(VERSION_CHECK, this::onClientResponse);
        ServerLoginConnectionEvents.QUERY_START.register(this::onLoginStart);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ClientLoginNetworking.registerGlobalReceiver(VERSION_CHECK, this::onServerRequest);
    }

    /**
     * On login start, send VERSION_CHECK request
     */
    private void onLoginStart(ServerLoginNetworkHandler serverLoginPacketListener, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer sync) {
        sender.sendPacket(VERSION_CHECK, PacketByteBufs.empty());
    }

    /**
     * On VERSION_CHECK request from a dedicated server, send response with current PROTOCOL_VERSION
     */
    @Environment(EnvType.CLIENT)
    private CompletableFuture<PacketByteBuf> onServerRequest(MinecraftClient minecraft, ClientLoginNetworkHandler listener, PacketByteBuf inBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> consumer) {
        PacketByteBuf outBuf = new PacketByteBuf(Unpooled.buffer());
        outBuf.writeInt(PROTOCOL_VERSION);
        return CompletableFuture.completedFuture(outBuf);
    }

    /**
     * Handle the VERSION_CHECK response from the client.
     * If the client did not respond (in time) or if the version is incorrect, disconnect
     */
    private void onClientResponse(MinecraftServer server, ServerLoginNetworkHandler listener, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
        if (!understood) {
            // Client did not respond in time, disconnect client.
            listener.disconnect(INCORRECT_VERSION);
            return;
        }

        int clientVersion = buf.readInt();

        if (clientVersion != PROTOCOL_VERSION) {
            // Client is using incorrect version, disconnect client.
            listener.disconnect(INCORRECT_VERSION);
        }
    }
}
