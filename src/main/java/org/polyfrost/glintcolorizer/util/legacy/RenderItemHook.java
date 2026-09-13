//? if legacy {
package org.polyfrost.glintcolorizer.util.legacy;

import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ItemStack;
import org.polyfrost.glintcolorizer.config.GlintColorizerConfig;

import java.util.EnumSet;



public class RenderItemHook {
    public static final RenderItemHook INSTANCE = new RenderItemHook();

    public ItemStack itemStack;
    public ModelTransformations.Type transformType;
    public EnumSet<ModelTransformations.Type> playerTransforms = EnumSet.of(
        ModelTransformations.Type.FIRST_PERSON,
        ModelTransformations.Type.THIRD_PERSON
    );

    public boolean shouldSkipGlintRendering() {
        return !isPotionGlintEnabled() || !isRenderingInGUI() || !isPotionItem();
    }

    public boolean isPotionGlintEnabled() {
        return GlintColorizerConfig.enabled && GlintColorizerConfig.shinyPots.enabled;
    }

    public boolean isPotionItem() {
        return itemStack.getItem() instanceof PotionItem && itemStack.hasEnchantmentGlint();
    }

    public boolean isRenderingHeld() {
        return playerTransforms.contains(transformType);
    }

    public boolean isRenderingInGUI() {
        return transformType == ModelTransformations.Type.GUI;
    }

    public boolean isRenderingDropped() {
        return transformType == ModelTransformations.Type.GROUND;
    }

    public boolean isRenderingFramed() {
        return transformType == ModelTransformations.Type.FIXED;
    }
}
//?}