package it.raqb.dyeingcreepers;

import it.raqb.dyeingcreepers.common.capabilities.Capabilities;
import it.raqb.dyeingcreepers.common.network.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(DyeingCreepers.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DyeingCreepers {
    public static final String MODID = "dyeingcreepers";

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        PacketHandler.register();
        Capabilities.register();
    }

    public static ResourceLocation resource(String name) {
        return new ResourceLocation(MODID, name);
    }
}