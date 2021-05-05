package it.raqb.dyeingcreepers.fabric;

import net.minecraft.util.DyeColor;

public interface IDyeableCreeper {
    DyeColor getColor();

    void setColor(DyeColor dyeColor);
}
