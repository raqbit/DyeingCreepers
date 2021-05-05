package it.raqb.dyeingcreepers.fabric.mixin.render;

import it.raqb.dyeingcreepers.fabric.DyeingCreepersMod;
import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Map;

@Mixin(CreeperRenderer.class)
public class CreeperEntityRendererMixin {
    private static final Map<DyeColor, ResourceLocation> TEXTURE_LOOKUP = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor value : DyeColor.values()) {

            // Skip lime, use default texture
            if (value == DyeColor.LIME) {
                TEXTURE_LOOKUP.put(value, getDefaultTexture());
                continue;
            }

            TEXTURE_LOOKUP.put(value, DyeingCreepersMod.resource(String.format("textures/entity/creeper/creeper_%s.png", value.getName())));
        }
    }

    @Accessor("CREEPER_LOCATION")
    private static ResourceLocation getDefaultTexture() {
        throw new AssertionError();
    }

    @Inject(
            method = "getTextureLocation",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getTextureLocation(Creeper creeperEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (creeperEntity instanceof IDyeableCreeper) {
            IDyeableCreeper creeper = (IDyeableCreeper) creeperEntity;
            cir.setReturnValue(TEXTURE_LOOKUP.get(creeper.getColor()));
        }
    }
}
