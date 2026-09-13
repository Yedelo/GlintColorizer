package org.polyfrost.glintcolorizer.config.category;

import org.polyfrost.compose.render.PolyColor;
import org.polyfrost.oneconfig.api.config.v1.annotations.Color;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class BaseGlint {
	@Switch(title = "Enabled")
	public boolean enabled = true;

	@Switch(title = "Individual Strokes")
	public boolean individualStrokes = false;

	@Color(title = "Color")
	public PolyColor color = new PolyColor(0xFF8040CC);

	@Color(title = "Stroke One Color")
	public PolyColor strokeOneColor = new PolyColor( 0xFFFF0000);

	@Slider(title = "Stroke Two Rotation", min = -50, max = 50, step = 1)
	public int strokeOneRotation = -50;

	@Color(title = "Stroke Two Color")
	public PolyColor strokeTwoColor = new PolyColor(0xFF0AEAFF);

	@Slider(title = "Stroke Two Rotation", min = -50, max = 50, step = 1)
	public int strokeTwoRotation = 10;

	@Slider(title = "Speed", max = 2.0F, step = 0.1F)
	public float speed = 1.0F;

	@Slider(title = "Scale", max = 2.0F)
	public float scale = 1.0F;
}
