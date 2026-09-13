package org.polyfrost.glintcolorizer.config.category;

import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class ShinyPotsCategory extends BaseGlint {
	@Switch(title = "Full Slot Glint")
	public boolean fullSlotShine = false;

    //? if legacy {
    @Checkbox(title = "Render Shiny Effect Only")
    public boolean foreground = false;

    @Checkbox(title = "Custom Shiny Effect Color")
    public boolean background = false;
    //?}

	@Switch(title = "Use Custom Color")
	public boolean useCustomColor = false;

	@Switch(title = "Use Potion Color")
	public boolean usePotionBasedColor = false;

	public ShinyPotsCategory() {
		this.enabled = false;
	}
}
