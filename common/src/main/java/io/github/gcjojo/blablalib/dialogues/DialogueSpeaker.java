package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class DialogueSpeaker {
    public static final DialogueSpeaker DEFAULT_SPEAKER = new DialogueSpeaker(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "empty"),
            "",
            Color.WHITE,
            new ArrayList<>());
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSpeaker> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueSpeaker::getId,
            ByteBufCodecs.STRING_UTF8, DialogueSpeaker::getName,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogueSpeaker::getSoundsLocation,
            LibLibNetwork.COLOR_STREAM_CODEC, DialogueSpeaker::getColor,
            DialogueSpeaker::new);
    @Getter
    private final ResourceLocation id;
    @Getter
    private final String name;
    @Getter
    private final List<SoundEvent> sounds;
    @Getter
    private Color color = Color.WHITE;

    public DialogueSpeaker(JsonObject obj) {
        String colorStr = obj.get("color").getAsString();
        try {
            this.color = Color.fromString(colorStr);
            // TODO TEMP FIX
            if(this.color == null)
                this.color = Color.WHITE;
        } catch (Exception e) {
            BlablaLib.getLogger().warn("Unable to parse color {}", colorStr);
        }

        JsonArray soundsArray = obj.get("sounds").getAsJsonArray();
        String[] soundNames = new String[soundsArray.size()];
        for (int i = 0; i <= obj.get("sounds").getAsJsonArray().size() - 1; i++)
            soundNames[i] = soundsArray.get(i).getAsString();

        this.id = ResourceLocation.tryParse(obj.get("id").getAsString());
        this.name = obj.get("name").getAsString();
        this.sounds = loadSounds(soundNames);
    }

    public DialogueSpeaker(ResourceLocation id, String name, Color color, String[] soundNames) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sounds = loadSounds(soundNames);
    }

    public DialogueSpeaker(ResourceLocation id, String name, Color color, List<SoundEvent> sounds) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sounds = sounds;
    }

    public DialogueSpeaker(ResourceLocation id, String name, List<ResourceLocation> soundLocations, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sounds = loadSounds(soundLocations);
    }

    public static List<SoundEvent> loadSounds(String[] soundNames) {
        List<SoundEvent> loadedSounds = new ArrayList<>();
        for (int i = 0; i <= soundNames.length - 1; i++) {
            String soundName = soundNames[i];
            LibLib.getSoundPlayer().loadSound(soundName).ifPresent(loadedSounds::add);
        }
        return loadedSounds;
    }

    public static List<SoundEvent> loadSounds(List<ResourceLocation> soundLocations) {
        List<SoundEvent> loadedSounds = new ArrayList<>();
        for (var location : soundLocations) {
            LibLib.getSoundPlayer().loadSound(location).ifPresent(loadedSounds::add);
        }
        return loadedSounds;
    }

    public List<ResourceLocation> getSoundsLocation() {
        List<ResourceLocation> locations = new ArrayList<>();
        for(var sound : getSounds())
            locations.add(sound.getLocation());
        return locations;
    }

    public boolean isValid() { return id != null; }
}
