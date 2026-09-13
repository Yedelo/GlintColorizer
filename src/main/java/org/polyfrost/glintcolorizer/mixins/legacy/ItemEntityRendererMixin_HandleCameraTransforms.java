//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy;



import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.ItemEntity;
import org.polyfrost.glintcolorizer.util.legacy.RenderItemHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin_HandleCameraTransforms {
    @Inject(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", ordinal = 0))
    private void glintcolorizer$setCameraTransform(ItemEntity itemEntity, double d, double e, double f, float g, float h, CallbackInfo ci) {
        RenderItemHook.INSTANCE.transformType = ModelTransformations.Type.GROUND;
    }
}
//?}