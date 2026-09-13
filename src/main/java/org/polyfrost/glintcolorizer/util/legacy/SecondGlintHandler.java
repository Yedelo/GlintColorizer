//? if legacy {
package org.polyfrost.glintcolorizer.util.legacy;



import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.texture.TextureAtlas;
import net.minecraft.client.render.texture.TextureManager;




import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.resource.Identifier;
import org.polyfrost.glintcolorizer.config.GlintColorizerConfig;
import org.polyfrost.glintcolorizer.mixins.legacy.accessor.ItemRendererAccessor;



public class SecondGlintHandler {
    public static void renderEffect(ItemRenderer itemRenderer, BakedModel model, TextureManager textureManager, Identifier glintResource) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(768, 1);
        textureManager.bind(glintResource);
        GlStateManager.matrixMode(5890);
        glintStroke1(itemRenderer, model);
        glintStroke2(itemRenderer, model);
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bind(TextureAtlas.BLOCKS_LOCATION);
        GlStateManager.popMatrix();
    }

    public static void glintStroke1(ItemRenderer itemRenderer, BakedModel model) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0f, 8.0f, 8.0f);
        float f = (Minecraft.getTime() % 3000L) / 3000.0f / 8.0f;
        GlStateManager.translatef(f, 0.0f, 0.0f);
        GlStateManager.rotatef(-50.0f, 0.0f, 0.0f, 1.0f);
//        (ItemRenderer as ItemRendererAccessor).invokeRenderModel(model,
//        if (GlintConfig.guiItem.individualStrokes) GlintConfig.guiItem.strokeOneColor.rgb
//        else GlintConfig.guiItem.glintColor.rgb
//        )
        int color = GlintColorizerConfig.INSTANCE.guiItemGlint.individualStrokes ? GlintColorizerConfig.INSTANCE.guiItemGlint.strokeOneColor.getArgb() : GlintColorizerConfig.INSTANCE.guiItemGlint.color.getArgb();
        ((ItemRendererAccessor) itemRenderer).invokeRender(model, color);
        GlStateManager.popMatrix();
    }

    public static void glintStroke2(ItemRenderer itemRenderer, BakedModel model) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0f, 8.0f, 8.0f);
        float f1 = (Minecraft.getTime() % 4873L) / 4873.0f / 8.0f;
        GlStateManager.translatef(-f1, 0.0f, 0.0f);
        GlStateManager.rotatef(10.0f, 0.0f, 0.0f, 1.0f);
        int color = GlintColorizerConfig.INSTANCE.guiItemGlint.individualStrokes ? GlintColorizerConfig.INSTANCE.guiItemGlint.strokeTwoColor.getArgb() : GlintColorizerConfig.INSTANCE.guiItemGlint.color.getArgb();
        ((ItemRendererAccessor) itemRenderer).invokeRender(model, color);
        GlStateManager.popMatrix();
    }
}
//?}