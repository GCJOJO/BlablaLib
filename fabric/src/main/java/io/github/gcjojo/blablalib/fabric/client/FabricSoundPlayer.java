package io.github.gcjojo.blablalib.fabric.client;

import io.github.gcjojo.blablalib.client.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class FabricSoundPlayer extends SoundPlayer {
    @Override
    public void playSound(String soundName, float pitch, float volume) {
        SoundEvent soundEvent = loadSound(soundName);
        if(soundEvent != null)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume));
    }

    @Override
    public SoundEvent loadSound(String soundName) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(soundName));
    }
}
