package org.polyfrost.glintcolorizer.mixin.celeritas;



import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.taumc.celeritas.api.options.control.CyclingControl;



@Mixin(CyclingControl.class)
public interface AnotherOne<T> {
    @Accessor("allowedValues")
    T[] yes$getAllowedValues();
}
