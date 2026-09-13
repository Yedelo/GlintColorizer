//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy;

import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import org.polyfrost.glintcolorizer.config.GlintColorizerConfig;
import org.polyfrost.glintcolorizer.config.category.BaseGlint;
import org.polyfrost.glintcolorizer.util.legacy.RenderItemHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashMap;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin_GlintCustomizer {

    @Unique private final HashMap<Integer, Integer> glintColorizer$cachedColors = new HashMap<>();

    @Inject(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "HEAD"
        )
    )
    private void glintColorizer$push(BakedModel model, CallbackInfo ci) {
        if (!RenderItemHook.INSTANCE.isPotionGlintEnabled()) { return; }
        if (glintColorizer$shouldApplyMatrix()) { return; }
        GlStateManager.pushMatrix();
    }

    @Inject(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "TAIL"
        )
    )
    private void glintColorizer$pop(BakedModel model, CallbackInfo ci) {
        if (!RenderItemHook.INSTANCE.isPotionGlintEnabled()) { return; }
        if (glintColorizer$shouldApplyMatrix()) { return; }
        GlStateManager.popMatrix();
    }

    @ModifyArgs(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;scalef(FFF)V"
        )
    )
    private void glintColorizer$modifyScale(Args args) {
        if (!GlintColorizerConfig.INSTANCE.enabled) { return; }
        args.set(0, glintColorizer$getModifiedScale(args.get(0)));
        args.set(1, glintColorizer$getModifiedScale(args.get(1)));
        args.set(2, glintColorizer$getModifiedScale(args.get(2)));
    }

    @ModifyArg(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;translatef(FFF)V"
        ),
        index = 0
    )
    private float glintColorizer$modifySpeed(float speed) {
        if (!GlintColorizerConfig.INSTANCE.enabled) { return speed; }
        return glintColorizer$getModifiedSpeed(speed);
    }

    @ModifyArg(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V",
            ordinal = 0
        ),
        index = 0
    )
    private float glintColorizer$modifyRotation(float angle) {
        if (!GlintColorizerConfig.INSTANCE.enabled) { return angle; }
        return glintColorizer$getModifiedRotation(angle , true);
    }

    @ModifyArg(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;rotatef(FFFF)V",
            ordinal = 1
        ),
        index = 0
    )
    private float glintColorizer$modifyRotation2(float angle) {
        if (!GlintColorizerConfig.INSTANCE.enabled) { return angle; }
        return glintColorizer$getModifiedRotation(angle, false);
    }

    @ModifyArg(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;I)V",
            ordinal = 0
        ),
        index = 1
    )
    private int glintColorizer$modifyColor1(int color) {
        if (!GlintColorizerConfig.INSTANCE.enabled) { return color; }
        return glintColorizer$getModifiedColor(color, true);
    }

    @ModifyArg(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/ItemRenderer;render(Lnet/minecraft/client/resource/model/BakedModel;I)V",
            ordinal = 1
        ),
        index = 1
    )
    private int glintColorizer$modifyColor2(int color) {
        if (!GlintColorizerConfig.INSTANCE.enabled) { return color; }
        return glintColorizer$getModifiedColor(color, false);
    }

    @Unique
    private int glintColorizer$getModifiedColor(int color, boolean isFirstStroke) {
        if (RenderItemHook.INSTANCE.isRenderingHeld()) {
            return glintColorizer$getColor(GlintColorizerConfig.INSTANCE.heldItemGlint, isFirstStroke);
        }

        if (RenderItemHook.INSTANCE.isRenderingInGUI()) {
            if (GlintColorizerConfig.INSTANCE.shinyPots.usePotionBasedColor && RenderItemHook.INSTANCE.isPotionItem()) {
                return glintColorizer$getPotionColor(RenderItemHook.INSTANCE.itemStack);
            }
            if (GlintColorizerConfig.INSTANCE.shinyPots.background && RenderItemHook.INSTANCE.isPotionItem()) {
                return glintColorizer$getColor(GlintColorizerConfig.INSTANCE.shinyPots, isFirstStroke);
            }
            return glintColorizer$getColor(GlintColorizerConfig.INSTANCE.guiItemGlint, isFirstStroke);
        }

        if (RenderItemHook.INSTANCE.isRenderingDropped()) {
            return glintColorizer$getColor(GlintColorizerConfig.INSTANCE.droppedItemGlint, isFirstStroke);
        }

        if (RenderItemHook.INSTANCE.isRenderingFramed()) {
            return glintColorizer$getColor(GlintColorizerConfig.INSTANCE.framedItemGlint, isFirstStroke);
        }

        return color;
    }

    @Unique
    private int glintColorizer$getColor(BaseGlint settings, boolean isFirstStroke) {
        return settings.individualStrokes ?
            (isFirstStroke ? settings.strokeOneColor.getArgb() : settings.strokeTwoColor.getArgb()) :
            settings.color.getArgb();
    }

    @Unique
    private float glintColorizer$getModifiedRotation(float defaultRot, boolean isFirstStroke) {
        if (RenderItemHook.INSTANCE.isRenderingHeld()) {
            return isFirstStroke ? GlintColorizerConfig.INSTANCE.heldItemGlint.strokeOneRotation : GlintColorizerConfig.INSTANCE.heldItemGlint.strokeTwoRotation;
        }

        if (RenderItemHook.INSTANCE.isRenderingInGUI()) {
            if (GlintColorizerConfig.INSTANCE.shinyPots.usePotionBasedColor && RenderItemHook.INSTANCE.isPotionItem()) {
                return isFirstStroke ? GlintColorizerConfig.INSTANCE.guiItemGlint.strokeOneRotation : GlintColorizerConfig.INSTANCE.guiItemGlint.strokeTwoRotation;
            }
            if (GlintColorizerConfig.INSTANCE.shinyPots.background && RenderItemHook.INSTANCE.isPotionItem()) {
                return isFirstStroke ? GlintColorizerConfig.INSTANCE.shinyPots.strokeOneRotation : GlintColorizerConfig.INSTANCE.shinyPots.strokeTwoRotation;
            }
            return isFirstStroke ? GlintColorizerConfig.INSTANCE.guiItemGlint.strokeOneRotation : GlintColorizerConfig.INSTANCE.guiItemGlint.strokeTwoRotation;
        }

        if (RenderItemHook.INSTANCE.isRenderingDropped()) {
            return isFirstStroke ? GlintColorizerConfig.INSTANCE.droppedItemGlint.strokeOneRotation : GlintColorizerConfig.INSTANCE.droppedItemGlint.strokeTwoRotation;
        }

        if (RenderItemHook.INSTANCE.isRenderingFramed()) {
            return isFirstStroke ? GlintColorizerConfig.INSTANCE.framedItemGlint.strokeOneRotation : GlintColorizerConfig.INSTANCE.framedItemGlint.strokeTwoRotation;
        }

        return defaultRot;
    }

    @Unique
    private float glintColorizer$getModifiedSpeed(float defaultSpeed) {
        if (RenderItemHook.INSTANCE.isRenderingHeld()) {
            return GlintColorizerConfig.INSTANCE.heldItemGlint.speed * defaultSpeed;
        }

        if (RenderItemHook.INSTANCE.isRenderingInGUI()) {
            if (GlintColorizerConfig.INSTANCE.shinyPots.usePotionBasedColor && RenderItemHook.INSTANCE.isPotionItem()) {
                return GlintColorizerConfig.INSTANCE.guiItemGlint.speed * defaultSpeed;
            }
            if (GlintColorizerConfig.INSTANCE.shinyPots.background && RenderItemHook.INSTANCE.isPotionItem()) {
                return GlintColorizerConfig.INSTANCE.shinyPots.scale * defaultSpeed;
            }
            return GlintColorizerConfig.INSTANCE.guiItemGlint.speed * defaultSpeed;
        }

        if (RenderItemHook.INSTANCE.isRenderingDropped()) {
            return GlintColorizerConfig.INSTANCE.droppedItemGlint.speed * defaultSpeed;
        }

        if (RenderItemHook.INSTANCE.isRenderingFramed()) {
            return GlintColorizerConfig.INSTANCE.framedItemGlint.speed * defaultSpeed;
        }

        return defaultSpeed;
    }

    @Unique
    private float glintColorizer$getModifiedScale(float originalScale) {
        if (RenderItemHook.INSTANCE.isRenderingHeld()) {
            return GlintColorizerConfig.INSTANCE.heldItemGlint.scale * originalScale;
        }

        if (RenderItemHook.INSTANCE.isRenderingInGUI()) {
            if (GlintColorizerConfig.INSTANCE.shinyPots.usePotionBasedColor && RenderItemHook.INSTANCE.isPotionItem()) {
                return GlintColorizerConfig.INSTANCE.guiItemGlint.scale * originalScale;
            }
            if (GlintColorizerConfig.INSTANCE.shinyPots.background && RenderItemHook.INSTANCE.isPotionItem()) {
                return GlintColorizerConfig.INSTANCE.shinyPots.scale * originalScale;
            }
            return GlintColorizerConfig.INSTANCE.guiItemGlint.scale * originalScale;
        }

        if (RenderItemHook.INSTANCE.isRenderingDropped()) {
            return GlintColorizerConfig.INSTANCE.droppedItemGlint.scale * originalScale;
        }

        if (RenderItemHook.INSTANCE.isRenderingFramed()) {
            return GlintColorizerConfig.INSTANCE.framedItemGlint.scale * originalScale;
        }

        return originalScale;
    }

    @Unique
    private boolean glintColorizer$shouldApplyMatrix() {
        return !GlintColorizerConfig.INSTANCE.shinyPots.fullSlotShine ||
            !RenderItemHook.INSTANCE.isRenderingInGUI() ||
            !RenderItemHook.INSTANCE.isPotionItem() ||
            (!GlintColorizerConfig.INSTANCE.shinyPots.foreground && !GlintColorizerConfig.INSTANCE.shinyPots.background);
    }

    /**
     * <a href="https://github.com/RoccoDev/ShinyPots-1.8">Adapted from ShinyPots by RoccoDev under the LGPL-3.0 license.</a>
     */
    @Unique
    private int glintColorizer$getPotionColor(ItemStack item) {
        int potionId = item.getMetadata();
        Integer cached = glintColorizer$cachedColors.get(potionId);
        if (cached != null) {
            return cached;
        } else {
            int color = Items.POTION.getDisplayColor(item, 0) | 0xFF000000;
            glintColorizer$cachedColors.put(potionId, color);
            return color;
        }
    }

}
//?}