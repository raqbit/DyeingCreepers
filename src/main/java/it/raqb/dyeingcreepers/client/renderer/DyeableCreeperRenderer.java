package it.raqb.dyeingcreepers.client.renderer;

import it.raqb.dyeingcreepers.DyeingCreepers;
import it.raqb.dyeingcreepers.common.capabilities.Capabilities;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class DyeableCreeperRenderer extends CreeperRenderer {
    private final Map<DyeColor, ResourceLocation> TEXTURE_LOOKUP = new EnumMap<>(DyeColor.class);

    {
        for (DyeColor value : DyeColor.values()) {

            // Skip lime, use default texture
            if (value == DyeColor.LIME) {
                continue;
            }

            TEXTURE_LOOKUP.put(value, DyeingCreepers.resource(String.format("textures/entity/creeper/creeper_%s.png", value.getTranslationKey())));
        }
    }

    public DyeableCreeperRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(CreeperEntity entity) {
        return entity
                .getCapability(Capabilities.DYEABLE)
                .resolve()
                .map(iDyeable -> TEXTURE_LOOKUP.get(iDyeable.getColor()))
                .orElse(super.getEntityTexture(entity));
    }
}
