package org.polyfrost.glintcolorizer;

import com.mojang.brigadier.Command;
import org.polyfrost.glintcolorizer.config.GlintColorizerConfig;

import net.fabricmc.api.ClientModInitializer;
import org.polyfrost.oneconfig.api.commands.v1.CommandManager;
import org.polyfrost.oneconfig.utils.v1.dsl.ScreensKt;

public final class GlintColorizer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Config
		GlintColorizerConfig.INSTANCE.preload();

		// Commands
        CommandManager.INSTANCE.register(
            CommandManager.literal("glintcolorizer")
            .executes((source) -> {
                ScreensKt.openUI(GlintColorizerConfig.INSTANCE);
                return Command.SINGLE_SUCCESS;
            })
        );
    }
}
