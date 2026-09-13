//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy.accessor;

import net.minecraft.client.render.entity.ItemRenderer;

import net.minecraft.client.resource.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    @Invoker void invokeRender(BakedModel model, int color);
}
//?}