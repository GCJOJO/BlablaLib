package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.liblib.LibLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.List;

public class DialogueSpeaker {
    public static final DialogueSpeaker DEFAULT_SPEAKER = new DialogueSpeaker(-1, "Speaker", 0xFFFFFFFF, new ArrayList<SoundEvent>());
    private int id;
    private String name;
    private int color;
    private List<SoundEvent> sounds = new ArrayList<>();

    public DialogueSpeaker(JsonObject obj) {
        this.color = 0xFFFFFFFF;
        String colorStr = obj.get("color").getAsString();
        try {
            if (colorStr.length() == 8) {
                int alpha = Integer.parseInt(colorStr.substring(0, 2), 16);
                int red = Integer.parseInt(colorStr.substring(2, 4), 16);
                int green = Integer.parseInt(colorStr.substring(4, 6), 16);
                int blue = Integer.parseInt(colorStr.substring(6, 8), 16);
                this.color = FastColor.ARGB32.color(alpha, red, green, blue);
            }
        } catch (Exception e) {
            BlablaLib.getLogger().warn("Unable to parse color {}", colorStr);
        }

        JsonArray soundsArray = obj.get("sounds").getAsJsonArray();
        String[] soundNames = new String[soundsArray.size()];
        for (int i = 0; i <= obj.get("sounds").getAsJsonArray().size() - 1; i++)
            soundNames[i] = soundsArray.get(i).getAsString();

        this.id = obj.get("id").getAsInt();
        this.name = obj.get("name").getAsString();
        this.sounds = loadSounds(soundNames);
    }

    public DialogueSpeaker(int id, String name, int color, String[] soundNames) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sounds = loadSounds(soundNames);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSpeaker> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueSpeaker::getId,
            ByteBufCodecs.STRING_UTF8, DialogueSpeaker::getName,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogueSpeaker::getSoundsLocation,
            ByteBufCodecs.INT, DialogueSpeaker::getColor,
            DialogueSpeaker::new);

    public DialogueSpeaker(int id, String name, int color, List<SoundEvent> sounds) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sounds = sounds;
    }

    public DialogueSpeaker(int id, String name, List<ResourceLocation> soundLocations, int color) {
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

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public List<SoundEvent> getSounds() {
        return this.sounds;
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
}
