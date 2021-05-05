package it.raqb.dyeingcreepers.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

public class DyeingCreepersMod implements ModInitializer {

    private static final String MOD_ID = "dyeingcreepers";

    @Override
    public void onInitialize() {
    }

    public static ResourceLocation resource(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
