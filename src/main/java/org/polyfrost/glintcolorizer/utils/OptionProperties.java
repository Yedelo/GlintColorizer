package org.polyfrost.glintcolorizer.utils;



import org.polyfrost.glintcolorizer.mixin.celeritas.AnotherOne;
import org.polyfrost.oneconfig.api.config.v1.Properties;
import org.polyfrost.oneconfig.api.config.v1.Property;
import org.taumc.celeritas.api.options.control.CyclingControl;
import org.taumc.celeritas.api.options.control.SliderControl;
import org.taumc.celeritas.api.options.control.TickBoxControl;
import org.taumc.celeritas.api.options.structure.Option;



public class OptionProperties {
    public static Property<?> getProperty(Option option, String id, String name, String description) {
        return switch (option.getControl()) {
            case SliderControl slider -> Properties.functional(() -> (Integer) option.getValue(), (newValue) -> option.setValue(newValue), id, name, description, Integer.class);
            case TickBoxControl tickBox -> Properties.functional(() -> (Boolean) option.getValue(), (newValue) -> option.setValue(newValue), id, name, description, Boolean.class);
            case CyclingControl cycling -> {
                Class<?> clazz = ((AnotherOne) cycling).yes$getAllowedValues()[0].getClass();
                yield Properties.<Object>functional(() -> clazz.cast(option.getValue()), (newValue) -> option.setValue(newValue), id, name, description, (Class<Object>) clazz);
            }
            default -> null;
        };
    }
}
