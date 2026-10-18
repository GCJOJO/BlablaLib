package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueTimings extends DialogueAction {

    public static final int DEFAULT_DIALOGUE_SPEED = 1;
    public static final int DEFAULT_WAIT_MULTIPLIER = 8;
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueTimings> TIMINGS_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueTimings::getDialogueSpeed,
            ByteBufCodecs.INT, DialogueTimings::getWaitMultiplier,
            DialogueTimings::new
    );
    private static final DialogueManager.DialogueActionType<DialogueTimings> TYPE = DialogueManager.register(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_timings"),
            TIMINGS_STREAM_CODEC
    );
    @Getter
    private int dialogueSpeed = DEFAULT_DIALOGUE_SPEED;
    @Getter
    private int waitMultiplier = DEFAULT_WAIT_MULTIPLIER;

    public DialogueTimings(int dialogueSpeed, int waitMultiplier) {
        this.dialogueSpeed = dialogueSpeed;
        this.waitMultiplier = waitMultiplier;
    }

    public DialogueTimings(JsonObject json) {
        if(json.has("speed"))
            dialogueSpeed = json.get("speed").getAsInt();
        if(json.has("wait"))
            waitMultiplier = json.get("wait").getAsInt();
    }

    @Override
    public void setup(DialogueScreen screen) {
        screen.setDialogueSpeed(dialogueSpeed);
        screen.setWaitMultiplier(waitMultiplier);
        screen.queueAdvanceDialogue();
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
        return true;
    }

    @Override
    public boolean isSkippable() {
        return false;
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
