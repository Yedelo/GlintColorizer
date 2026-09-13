//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy;

import net.minecraft.client.render.entity.ItemRenderer;

import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import org.polyfrost.glintcolorizer.config.GlintColorizerConfig;
import org.polyfrost.glintcolorizer.util.legacy.RenderItemHook;
import org.polyfrost.glintcolorizer.util.legacy.SecondGlintHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin_ShinyEffect {

    @Shadow protected abstract void renderEnchantmentGlint(BakedModel model);
    @Shadow @Final private static Identifier ENCHANTMENT_GLINT_LOCATION;
    @Shadow @Final private TextureManager textureManager;

    @Inject(
        method = "renderItem",
        at = @At(
            "HEAD"
        )
    )
    private void glintColorizer$setItemRendering(ItemStack stack, BakedModel model, CallbackInfo ci) {
        RenderItemHook.INSTANCE.itemStack = stack;
    }

    @Redirect(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;depthFunc(I)V"
        )
    )
    private void glintColorizer$disableDepthFunc(int factor) {
        if (RenderItemHook.INSTANCE.shouldSkipGlintRendering()) {
            GlStateManager.depthFunc(factor);
        }
    }

    @Inject(
        method = "renderItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private void glintColorizer$onRenderModel(ItemStack stack, BakedModel model, CallbackInfo ci) {
        if (!RenderItemHook.INSTANCE.isPotionGlintEnabled()) { return; }
        if ((GlintColorizerConfig.INSTANCE.shinyPots.foreground || GlintColorizerConfig.INSTANCE.shinyPots.background) && glintColorizer$isValidItem(stack)) {
            renderEnchantmentGlint(model);
        }
    }

    @Inject(
        method = "renderItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;Lnet/minecraft/item/ItemStack;)V",
            shift = At.Shift.AFTER
        )
    )
    private void glintColorizer$onRenderModel2(ItemStack stack, BakedModel model, CallbackInfo ci) {
        if (!RenderItemHook.INSTANCE.isPotionGlintEnabled()) { return; }
        if (GlintColorizerConfig.INSTANCE.shinyPots.background && !GlintColorizerConfig.INSTANCE.shinyPots.foreground && glintColorizer$isValidItem(stack)) {
            ItemRenderer instance = (ItemRenderer) (Object) this;
            SecondGlintHandler.renderEffect(instance, model, textureManager, ENCHANTMENT_GLINT_LOCATION);
        }
    }

    @Redirect(
        method = "renderItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;hasEnchantmentGlint()Z"
        )
    )
    private boolean glintColorizer$disableRenderEffect(ItemStack instance) {
        if (RenderItemHook.INSTANCE.isPotionGlintEnabled() && RenderItemHook.INSTANCE.isRenderingInGUI() && RenderItemHook.INSTANCE.isPotionItem()) {
            return !GlintColorizerConfig.INSTANCE.shinyPots.foreground && !GlintColorizerConfig.INSTANCE.shinyPots.background;
        }
        return instance.getItem() != null && instance.hasEnchantmentGlint();
    }

    @Inject(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/texture/TextureManager;bind(Lnet/minecraft/resource/Identifier;)V",
            ordinal = 0
        )
    )
    private void glintColorizer$fullSlotSize(BakedModel model, CallbackInfo ci) {
        if (!RenderItemHook.INSTANCE.isPotionGlintEnabled()) { return; }
        if (GlintColorizerConfig.INSTANCE.shinyPots.fullSlotShine && RenderItemHook.INSTANCE.isRenderingInGUI() && RenderItemHook.INSTANCE.isPotionItem()) {
            GlStateManager.scaled(1.25, 1.25, 1.25);
            GlStateManager.translated(-0.1, -0.1, 0.0);
        }
    }

    @Unique
    private boolean glintColorizer$isValidItem(ItemStack stack) {
        return RenderItemHook.INSTANCE.isRenderingInGUI() && RenderItemHook.INSTANCE.isPotionItem() && stack.hasEnchantmentGlint();
    }

}
//?}