package it.raqb.dyeingcreepers.fabric;

import net.minecraft.util.Identifier;

public class DyeingCreepersMod {
    private static final String MOD_ID = "dyeingcreepers";

    public static Identifier resource(String name) {
        return new Identifier(MOD_ID, name);
    }
}
