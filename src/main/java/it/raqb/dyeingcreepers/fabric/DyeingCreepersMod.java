package it.raqb.dyeingcreepers.fabric;

import net.minecraft.resources.ResourceLocation;

public class DyeingCreepersMod {
    private static final String MOD_ID = "dyeingcreepers";

    public static ResourceLocation resource(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
