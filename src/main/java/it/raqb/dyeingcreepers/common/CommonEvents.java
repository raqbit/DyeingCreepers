package it.raqb.dyeingcreepers.common;

import it.raqb.dyeingcreepers.common.capabilities.dyeablecreeper.Dyeable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        // Attach dyeable capability on all creepers
        if (event.getObject() instanceof CreeperEntity) {
            event.addCapability(Dyeable.Provider.NAME, new Dyeable.Provider());
        }
    }
}
