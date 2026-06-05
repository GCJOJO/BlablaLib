package io.github.gcjojo.blablalib.client;

import net.minecraft.sounds.SoundEvent;

public abstract class SoundPlayer {

    public void playSound(String soundName){
        playSound(soundName, 1.0f, 1.0f);
    }

    public abstract void playSound(String soundName, float pitch, float volume);
    public abstract SoundEvent loadSound(String soundName);
}
