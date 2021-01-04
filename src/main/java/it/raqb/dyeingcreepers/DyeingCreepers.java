package it.raqb.dyeingcreepers;

import it.raqb.dyeingcreepers.capabilities.Capabilities;
import it.raqb.dyeingcreepers.capabilities.dyeablecreeper.Dyeable;
import it.raqb.dyeingcreepers.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Customize renderer for creeper to change color based on capability

@Mod(DyeingCreepers.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DyeingCreepers {
    public static final String MODID = "dyeingcreepers";

    static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        PacketHandler.register();
        Capabilities.register();
    }

    public static ResourceLocation resource(String name) {
        return new ResourceLocation(MODID, name);
    }
}