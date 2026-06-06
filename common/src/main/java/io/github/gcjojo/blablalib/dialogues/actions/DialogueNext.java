package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class DialogueNext extends DialogueAction {

    ResourceLocation nextDialogue;

    public DialogueNext(ResourceLocation nextSet) {
        this.nextDialogue = nextSet;
    }

    public DialogueNext(JsonObject object){
        if(object.has("set"))
            this.nextDialogue = ResourceLocation.tryParse(object.get("set").getAsString());
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        if(nextDialogue != null)
            screen.queueChangeSet(nextDialogue);
    }

    @Override
    public void step() { }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) { }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) { }

    @Override
    public boolean isBlocking() { return true; }

    @Override
    public boolean isSkippable() { return false; }
}
