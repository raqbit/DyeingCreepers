package it.raqb.dyeingcreepers.common.capabilities.dyeablecreeper;

import net.minecraft.item.DyeColor;
import net.minecraft.nbt.IntNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDyeable extends INBTSerializable<IntNBT> {

    /**
     * Retrieves the color of the dyeable
     *
     * @return The color of the dyeable
     */
    DyeColor getColor();

    /**
     * Updates the color of the dyeable
     * @param color the new color of the dyeable
     */
    void setColor(DyeColor color);
}
