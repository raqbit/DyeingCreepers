package it.raqb.dyeingcreepers.fabric.mixin.entity;

import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends HostileEntity implements IDyeableCreeper {

    /**
     * Tracks data of the Creeper
     */
    private static final TrackedData<Byte> COLOR;

    /**
     * All colors except lime, the colors that can be picked at random for a natural Creeper
     */
    private static final List<DyeColor> RANDOM_COLORS;

    static {
        COLOR = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BYTE);

        RANDOM_COLORS = Arrays.stream(DyeColor.values())
                .filter(dye -> dye != DyeColor.LIME)
                .collect(Collectors.toList());
    }

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker()V", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(COLOR, (byte) 0);
    }

    /**
     * Get the Creeper's color
     *
     * @return The Creeper's color
     */
    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.get(COLOR));
    }

    /**
     * Set the Creeper's color
     *
     * @param color The color to give the Creeper
     */
    public void setColor(DyeColor color) {
        this.dataTracker.set(COLOR, (byte) color.getId());
    }

    /**
     * Generate a random Creeper color
     *
     * @param random Random to use to generate the random Creeper color
     * @param reason Reason the mob spawned
     * @return The random Creeper color that was generated
     */
    private static DyeColor generateDefaultColor(Random random, SpawnReason reason) {
        // 5% chance a natural spawning creeper has a random (non-lime) color
        if (reason == SpawnReason.NATURAL && random.nextInt(100) < 5) {
            return RANDOM_COLORS.get(random.nextInt(RANDOM_COLORS.size()));
        } else {
            return DyeColor.LIME;
        }
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.setColor(generateDefaultColor(world.getRandom(), spawnReason));
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    @Inject(
            method = "writeCustomDataToTag",
            at = @At("TAIL")
    )
    private void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        tag.putByte("Color", (byte) this.getColor().getId());
    }

    @Inject(
            method = "readCustomDataFromTag",
            at = @At("TAIL")
    )
    private void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        this.setColor(DyeColor.byId(tag.getByte("Color")));
    }
}
