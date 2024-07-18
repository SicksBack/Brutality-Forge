package org.brutality.settings.impl;

import org.brutality.module.Module;
import org.brutality.settings.Setting;

public class NumberSettingForScale extends Setting {

    private double value, minimum, maximum, increment;

    public NumberSettingForScale(String name, Module parent, double value, double minimum, double maximum, double increment) {
        super(name, parent);
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = Math.round(Math.max(minimum, Math.min(maximum, value)) * (1.0 / increment)) / (1.0 / increment);
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }
}
