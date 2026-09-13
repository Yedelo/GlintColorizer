//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy;



import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import org.polyfrost.glintcolorizer.util.legacy.RenderItemHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;



@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin_HandleCameraTransforms {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", ordinal = 0))
    private void glintcolorizer$setCameraTransform() {
        RenderItemHook.INSTANCE.transformType = ModelTransformations.Type.GROUND;
    }
}
//?}