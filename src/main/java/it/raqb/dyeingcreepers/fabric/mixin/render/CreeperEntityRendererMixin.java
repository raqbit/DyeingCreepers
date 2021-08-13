package it.raqb.dyeingcreepers.fabric.mixin.render;

import it.raqb.dyeingcreepers.fabric.DyeingCreepersMod;
import it.raqb.dyeingcreepers.fabric.IDyeableCreeper;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Map;

@Mixin(CreeperEntityRenderer.class)
public abstract class CreeperEntityRendererMixin extends MobEntityRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
    private static final Map<DyeColor, Identifier> TEXTURE_LOOKUP = new EnumMap<>(DyeColor.class);

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

    public CreeperEntityRendererMixin(EntityRendererFactory.Context context, CreeperEntityModel<CreeperEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Accessor("TEXTURE")
    private static Identifier getDefaultTexture() {
        throw new AssertionError();
    }

    @Inject(
            method = "getTexture",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getTexture(CreeperEntity creeperEntity, CallbackInfoReturnable<Identifier> cir) {
        if (creeperEntity instanceof IDyeableCreeper creeper) {
            cir.setReturnValue(TEXTURE_LOOKUP.get(creeper.getColor()));
        }
    }
}
