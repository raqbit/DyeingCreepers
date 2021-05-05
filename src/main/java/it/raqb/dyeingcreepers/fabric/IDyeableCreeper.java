package it.raqb.dyeingcreepers.fabric;
import net.minecraft.world.item.DyeColor;

public interface IDyeableCreeper {
    DyeColor getColor();

    void setColor(DyeColor dyeColor);
}
