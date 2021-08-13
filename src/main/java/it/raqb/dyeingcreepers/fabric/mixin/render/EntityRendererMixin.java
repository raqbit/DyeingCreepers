package it.raqb.dyeingcreepers.fabric.mixin.render;

import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "getBlockLight", at = @At("RETURN"), cancellable = true)
    public void setBlockLight(Entity entity, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        if(entity instanceof IDyeableCreeper creeper && creeper.getGlow()){
            cir.setReturnValue(15);
        }
    }
}
