package it.raqb.dyeingcreepers.network.packets;

import it.raqb.dyeingcreepers.capabilities.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SyncDyeablePacket {

    private static final Logger LOGGER = LogManager.getLogger();
    private int entityId;
    private int color;

    public SyncDyeablePacket() {
    }

    public SyncDyeablePacket(int entityId, int color) {
        this.entityId = entityId;
        this.color = color;
    }

    private void fromBytes(PacketBuffer buffer) {
        entityId = buffer.readInt();
        color = buffer.readInt();
    }

    private void toBytes(PacketBuffer buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(color);
    }

    public static SyncDyeablePacket decode(PacketBuffer buffer) {
        SyncDyeablePacket message = new SyncDyeablePacket();
        message.fromBytes(buffer);
        return message;
    }

    public static void encode(SyncDyeablePacket message, PacketBuffer buffer) {
        message.toBytes(buffer);
    }

    public static void onMessage(SyncDyeablePacket message, Supplier<NetworkEvent.Context> ctx) {
        // TODO: somehow integrate this with the capability
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            if (world != null) {
                Entity entity = world.getEntityByID(message.entityId);
                if (entity != null) {
                    entity.getCapability(Capabilities.DYEABLE).ifPresent(capability -> {
                        capability.setColor(DyeColor.byId(message.color));
                    });
                }
            }
        });
        context.setPacketHandled(true);
    }
}
