package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import net.minecraft.client.gui.GuiGraphics;

public class DialogueTimings extends DialogueAction {

    private static final int DEFAULT_DIALOGUE_SPEED = 1;
    private static final int DEFAULT_WAIT_MULTIPLIER = 8;

    private int dialogueSpeed = DEFAULT_DIALOGUE_SPEED;
    private int waitMultiplier = DEFAULT_WAIT_MULTIPLIER;

    public DialogueTimings(JsonObject json) {
        if(json.has("speed"))
            dialogueSpeed = json.get("speed").getAsInt();
        if(json.has("wait"))
            dialogueSpeed = json.get("wait").getAsInt();
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
}
