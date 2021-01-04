package it.raqb.dyeingcreepers.capabilities;

import it.raqb.dyeingcreepers.capabilities.dyeablecreeper.Dyeable;
import it.raqb.dyeingcreepers.capabilities.dyeablecreeper.IDyeable;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {
    @CapabilityInject(IDyeable.class)
    public static Capability<IDyeable> DYEABLE = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDyeable.class, new Capability.IStorage<IDyeable>() {
            @Override
            public INBT writeNBT(Capability<IDyeable> capability, IDyeable instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IDyeable> capability, IDyeable instance, Direction side, INBT nbt) {
                instance.deserializeNBT((IntNBT) nbt);
            }
        }, () -> new Dyeable(DyeColor.GREEN));
    }
}
