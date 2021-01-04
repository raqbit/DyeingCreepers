package it.raqb.dyeingcreepers.capabilities.dyeablecreeper;

import it.raqb.dyeingcreepers.DyeingCreepers;
import it.raqb.dyeingcreepers.capabilities.Capabilities;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Dyeable implements IDyeable {
    private DyeColor color;

    public Dyeable(DyeColor defaultColor) {
        color = defaultColor;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor newColor) {
        color = newColor;
    }

    @Override
    public IntNBT serializeNBT() {
        return IntNBT.valueOf(color.getId());
    }

    @Override
    public void deserializeNBT(IntNBT nbt) {
        color = DyeColor.byId(nbt.getInt());
    }

    public static class Provider implements ICapabilitySerializable<IntNBT> {
        public static final ResourceLocation NAME = DyeingCreepers.resource("dyeable");
        private final IDyeable impl = Objects.requireNonNull(Capabilities.DYEABLE.getDefaultInstance());
        private final LazyOptional<IDyeable> cap = LazyOptional.of(() -> impl);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
            if (capability == Capabilities.DYEABLE) {
                return cap.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public IntNBT serializeNBT() {
            return impl.serializeNBT();
        }

        @Override
        public void deserializeNBT(IntNBT nbt) {
            impl.deserializeNBT(nbt);
        }
    }
}
