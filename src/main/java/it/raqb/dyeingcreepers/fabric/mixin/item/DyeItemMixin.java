package it.raqb.dyeingcreepers.fabric.mixin.item;

import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeItem.class)
public class DyeItemMixin {
    @Inject(
            method = "useOnEntity",
            at = @At(value = "TAIL"),
            cancellable = true
    )
    public void useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand arm, CallbackInfoReturnable<ActionResult> cir) {
        if (entity instanceof IDyeableCreeper dCreeper) {
            CreeperEntity creeper = (CreeperEntity) entity;
            if (creeper.isAlive() && dCreeper.getColor() != ((DyeItem) (Object) this).getColor()) {
                if (!player.world.isClient) {
                    dCreeper.setColor(((DyeItem) (Object) this).getColor());
                    stack.decrement(1);
                }

                cir.setReturnValue(ActionResult.success(player.world.isClient));
            }
        }
    }
}
