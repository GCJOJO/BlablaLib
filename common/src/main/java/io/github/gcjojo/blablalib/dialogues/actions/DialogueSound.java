package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.liblib.LibLib;
import net.minecraft.client.gui.GuiGraphics;

public class DialogueSound extends DialogueAction {

    private String soundName;
    private float pitch = 1.0f;
    private float volume = 1.0f;

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
        LibLib.getSoundPlayer().playSound(this.soundName, this.pitch, this.volume);
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
}
