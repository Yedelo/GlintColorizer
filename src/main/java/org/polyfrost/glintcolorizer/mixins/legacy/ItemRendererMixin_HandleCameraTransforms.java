//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy;



import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.polyfrost.glintcolorizer.util.legacy.RenderItemHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ItemRenderer.class)
public class ItemRendererMixin_HandleCameraTransforms {
    @Inject(method = "renderItemInHand(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/model/BakedModel;getTransformations()Lnet/minecraft/client/render/model/block/ModelTransformations;"))
    private void glintcolorizer$setCameraTransform$hand(ItemStack item, BakedModel model, ModelTransformations.Type transform, CallbackInfo ci) {
        RenderItemHook.INSTANCE.transformType = transform;
    }

    @Inject(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/block/ModelTransformations;apply(Lnet/minecraft/client/render/model/block/ModelTransformations$Type;)V"))
    private void glintcolorizer$setCameraTransform$gui(ItemStack item, int x, int y, CallbackInfo ci) {
        RenderItemHook.INSTANCE.transformType = ModelTransformations.Type.GUI;
    }
}
//?}
