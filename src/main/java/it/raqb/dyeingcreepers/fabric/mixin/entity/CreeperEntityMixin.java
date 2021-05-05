package it.raqb.dyeingcreepers.fabric.mixin.entity;

import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(Creeper.class)
public class CreeperEntityMixin extends Monster implements IDyeableCreeper {

    /**
     * Tracks data of the Creeper
     */
    private static final EntityDataAccessor<Byte> COLOR;

    /**
     * All colors except lime, the colors that can be picked at random for a natural Creeper
     */
    private static final List<DyeColor> RANDOM_COLORS;

    static {
        COLOR = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BYTE);

        RANDOM_COLORS = Arrays.stream(DyeColor.values())
                .filter(dye -> dye != DyeColor.LIME)
                .collect(Collectors.toList());
    }

    protected CreeperEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(COLOR, (byte) 0);
    }

    /**
     * Get the Creeper's color
     *
     * @return The Creeper's color
     */
    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(COLOR));
    }

    /**
     * Set the Creeper's color
     *
     * @param color The color to give the Creeper
     */
    public void setColor(DyeColor color) {
        this.entityData.set(COLOR, (byte) color.getId());
    }

    /**
     * Generate a random Creeper color
     *
     * @param random Random to use to generate the random Creeper color
     * @param reason Reason the mob spawned
     * @return The random Creeper color that was generated
     */
    private static DyeColor generateDefaultColor(Random random, MobSpawnType reason) {
        // 5% chance a natural spawning creeper has a random (non-lime) color
        if (reason == MobSpawnType.NATURAL && random.nextInt(100) < 5) {
            return RANDOM_COLORS.get(random.nextInt(RANDOM_COLORS.size()));
        } else {
            return DyeColor.LIME;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setColor(generateDefaultColor(level.getRandom(), mobSpawnType));
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putByte("Color", (byte) this.getColor().getId());
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        this.setColor(DyeColor.byId(tag.getByte("Color")));
    }
}
