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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
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

    private static final Identifier GLOW_INK_SAC = new Identifier("minecraft", "glow_ink_sac");

    /**
     * Tracks data of the Creeper
     */
    private static final TrackedData<Byte> COLOR;
    private static final TrackedData<Boolean> GLOWING;

    /**
     * All colors except lime, the colors that can be picked at random for a natural Creeper
     */
    private static final List<DyeColor> RANDOM_COLORS;

    static {
        COLOR = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BYTE);
        GLOWING = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

        RANDOM_COLORS = Arrays.stream(DyeColor.values())
                .filter(dye -> dye != DyeColor.LIME)
                .collect(Collectors.toList());
    }

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World level) {
        super(entityType, level);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(COLOR, (byte) 0);
        this.dataTracker.startTracking(GLOWING, false);
    }

    /**
     * Get the Creeper's color
     *
     * @return The Creeper's color
     */
    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.get(COLOR));
    }

    public boolean getGlow() {
        return this.dataTracker.get(GLOWING);
    }

    /**
     * Set the Creeper's color
     *
     * @param color The color to give the Creeper
     */
    public void setColor(DyeColor color) {
        this.dataTracker.set(COLOR, (byte) color.getId());
    }

    public void setGlow(boolean glowing) {
        this.dataTracker.set(GLOWING, glowing);
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        var playerItem = player.getMainHandStack().getItem();
        if(!this.getGlow() && Registry.ITEM.getId(playerItem).equals(GLOW_INK_SAC)) {
            if (player.world.isClient) {
                return ActionResult.CONSUME;
            } else {
                this.setGlow(true);
                if(!player.getAbilities().creativeMode) {
                    player.getMainHandStack().decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.interactAt(player, hitPos, hand);
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
    public EntityData initialize(ServerWorldAccess serverLevelAccessor, LocalDifficulty difficultyInstance, SpawnReason mobSpawnType, @Nullable EntityData spawnGroupData, @Nullable NbtCompound compoundTag) {
        this.setColor(generateDefaultColor(world.getRandom(), mobSpawnType));
        return super.initialize(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("TAIL")
    )
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        tag.putByte("Color", (byte) this.getColor().getId());
        tag.putBoolean("Glow", this.getGlow());
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("TAIL")
    )
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        this.setColor(DyeColor.byId(tag.getByte("Color")));
        this.setGlow(tag.getBoolean("Glow"));
    }
}
