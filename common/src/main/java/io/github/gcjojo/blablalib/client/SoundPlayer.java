package io.github.gcjojo.blablalib.client;

import net.minecraft.sounds.SoundEvent;

public abstract class SoundPlayer {
    public abstract void playSound(String soundName);
    public abstract SoundEvent loadSound(String soundName);
}
