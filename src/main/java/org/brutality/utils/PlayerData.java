package org.brutality.utils;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerData {
    public int d; // Autoblock counter
    public int i; // NoSlow counter
    public double a; // Some value for NoSlow check
    public int c; // Scaffold counter
    public int f; // Some counter for Scaffold
    public int b; // Some counter for Scaffold

    public void update(EntityPlayer player) {
        // Your logic to update the counters
    }

    public void updateSneak(EntityPlayer player) {
        // Your logic to update sneaking status
    }

    public int getD() {
        return d;
    }

    public int getI() {
        return i;
    }

    public double getA() {
        return a;
    }

    public int getC() {
        return c;
    }

    public int getF() {
        return f;
    }

    public int getB() {
        return b;
    }
}
