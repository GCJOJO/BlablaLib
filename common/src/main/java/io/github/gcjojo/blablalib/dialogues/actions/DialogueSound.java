package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.liblib.client.SoundPlayer;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueSound extends DialogueAction {

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSound> SOUND_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DialogueSound::getSoundName,
            ByteBufCodecs.FLOAT, DialogueSound::getPitch,
            ByteBufCodecs.FLOAT, DialogueSound::getVolume,
            DialogueSound::new
    );
    private static final DialogueManager.DialogueActionType<DialogueSound> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_sound"),
            SOUND_STREAM_CODEC
    );
    @Getter
    private String soundName;
    @Getter
    private float pitch = 1.0f;
    @Getter
    private float volume = 1.0f;

    public DialogueSound(String soundName, float pitch, float volume) {
        this.soundName = soundName;
        this.pitch = pitch;
        this.volume = volume;
    }

    public DialogueSound(JsonObject object) {
        if (object.has("sound"))
            this.soundName = object.get("sound").getAsString();
        if (object.has("pitch"))
            this.pitch = object.get("pitch").getAsFloat();
        if (object.has("volume"))
            this.volume = object.get("volume").getAsFloat();
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        SoundPlayer.playSound(this.soundName, this.pitch, this.volume);
    }

    @Override
    public void step() {
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
