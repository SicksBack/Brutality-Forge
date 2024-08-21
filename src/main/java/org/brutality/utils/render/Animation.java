package org.brutality.utils.render;

public class Animation {
    private final Easing easing;
    private final long duration;
    private float value;

    public Animation(Easing easing, long duration) {
        this.easing = easing;
        this.duration = duration;
    }

    public void run(float targetValue) {
        // Dummy implementation for running the animation
        this.value = targetValue;
    }

    public float getValue() {
        return value;
    }
}

