package io.github.gcjojo.blablalib.forge.client;

import io.github.gcjojo.blablalib.client.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeSoundPlayer extends SoundPlayer {

    @Override
    public void playSound(String soundName) {

    }

    @Override
    public void playSound(String soundName, float pitch, float volume) {
        SoundEvent sound = loadSound(soundName);
        if(sound != null)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    @Override
    public SoundEvent loadSound(String soundName) {
        RegistryObject<SoundEvent> soundEvent = RegistryObject.create(ResourceLocation.tryParse(soundName), ForgeRegistries.SOUND_EVENTS);
        if(!soundEvent.isPresent()) return null;
        return soundEvent.get();
    }
}
