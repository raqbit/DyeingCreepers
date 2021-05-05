package it.raqb.dyeingcreepers.fabric.mixin.item;

import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeItem.class)
public class DyeItemMixin {
    @Inject(
            method = "interactLivingEntity",
            at = @At(value = "TAIL"),
            cancellable = true
    )
    public void interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand arm, CallbackInfoReturnable<InteractionResult> cir) {
        if (entity instanceof IDyeableCreeper) {
            Creeper creeper = (Creeper) entity;
            IDyeableCreeper dyeableCreeper = (IDyeableCreeper) creeper;
            if (creeper.isAlive() && dyeableCreeper.getColor() != ((DyeItem) (Object) this).getDyeColor()) {
                if (!user.level.isClientSide) {
                    dyeableCreeper.setColor(((DyeItem) (Object) this).getDyeColor());
                    stack.shrink(1);
                }

                cir.setReturnValue(InteractionResult.sidedSuccess(user.level.isClientSide));
            }
        }
    }
}
