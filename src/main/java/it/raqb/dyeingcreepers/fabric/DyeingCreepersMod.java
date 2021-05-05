package it.raqb.dyeingcreepers.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class DyeingCreepersMod implements ModInitializer {

    private static final String MOD_ID = "dyeingcreepers";

    @Override
    public void onInitialize() {
    }

    public static Identifier resource(String name) {
        return new Identifier(MOD_ID, name);
    }
}
