//? if legacy {
package org.polyfrost.glintcolorizer.mixins.legacy;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.entity.layer.EntityRenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.render.entity.layer.AbstractArmorLayer;
import org.polyfrost.compose.render.PolyColor;
import org.polyfrost.glintcolorizer.config.GlintColorizerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(
    value = AbstractArmorLayer.class,
    priority = Integer.MIN_VALUE
)
public abstract class AbstractArmorLayerMixin<T extends Model> implements EntityRenderLayer<LivingEntity> {

    @Inject(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void glintColorizer$disableGlint(LivingEntity entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float partialTicks, float p_177183_6_, float p_177183_7_, float p_177183_8_, float scale, CallbackInfo ci) {
        if (GlintColorizerConfig.INSTANCE.enabled && !GlintColorizerConfig.INSTANCE.armorGlint.enabled) {
            ci.cancel();
        }
    }

    @ModifyArgs(
        method = "renderEnchantmentGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;color4f(FFFF)V"
        )
    )
    private void glintColorizer$modifyArmorColor(Args args) {
        PolyColor color = GlintColorizerConfig.INSTANCE.armorGlint.color;
        args.set(0, color.getRedF());
        args.set(1, color.getGreenF());
        args.set(2, color.getBlueF());
    }

}
//?}