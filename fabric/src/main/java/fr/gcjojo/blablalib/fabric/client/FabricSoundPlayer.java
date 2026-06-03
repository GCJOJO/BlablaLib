package fr.gcjojo.blablalib.fabric.client;

import fr.gcjojo.blablalib.client.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class FabricSoundPlayer extends SoundPlayer {
    @Override
    public void playSound(String soundName) {
        SoundEvent soundEvent = loadSound(soundName);
        if(soundEvent != null)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 1.0F));
    }

    @Override
    public SoundEvent loadSound(String soundName) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(soundName));
    }
}
