package org.polyfrost.glintcolorizer.mixin;



import org.embeddedt.embeddium.impl.gui.CeleritasVideoOptionsController;
import org.embeddedt.embeddium.impl.gui.framework.DrawContext;
import org.polyfrost.glintcolorizer.CeleritasCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.taumc.celeritas.api.options.structure.OptionPage;

import java.util.List;



@Mixin(CeleritasVideoOptionsController.class)
public abstract class ClassNameTooDamnLong {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void captureParentPages(Runnable onClose, List<OptionPage> pages, DrawContext font, CallbackInfo ci) {
        CeleritasCompat.INSTANCE.handleOptionPages(pages);
    }
}
