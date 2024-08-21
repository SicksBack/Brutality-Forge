package org.brutality.settings.impl;

import lombok.Getter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

@Getter
public class NumberSetting extends Setting {
    private double value;
    private final double minValue;
    private final double maxValue;
    private final int decimalPlaces;

    public NumberSetting(String name, Module parent, double value, double minValue, double maxValue, int decimalPlaces) {
        super(name, parent);
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.decimalPlaces = decimalPlaces;
    }

    public void setValue(double value) {
        if (value < minValue) {
            value = minValue;
        }
        if (value > maxValue) {
            value = maxValue;
        }
        if (decimalPlaces == 0) {
            this.value = Math.round(value); // Ensure no decimals
        } else {
            // Round to the specified number of decimal places
            double scale = Math.pow(10, decimalPlaces);
            this.value = Math.round(value * scale) / scale;
        }
        mm.updateSettings(this);
    }

    @Override
    public String toString() {
        if (decimalPlaces == 0) {
            return String.format("%.0f", value); // Format without decimals
        } else {
            return String.format("%." + decimalPlaces + "f", value); // Format with specified decimal places
        }
    }
}
