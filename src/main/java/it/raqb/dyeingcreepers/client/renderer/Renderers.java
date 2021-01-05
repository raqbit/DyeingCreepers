package it.raqb.dyeingcreepers.client.renderer;

import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class Renderers {
    public static void register() {
        RenderingRegistry.registerEntityRenderingHandler(EntityType.CREEPER, DyeableCreeperRenderer::new);
    }
}
