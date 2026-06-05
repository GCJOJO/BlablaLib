package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import net.minecraft.client.gui.GuiGraphics;

public class DialogueNext extends DialogueAction {

    String nextSet;

    public DialogueNext(String nextSet) {
        this.nextSet = nextSet;
    }

    public DialogueNext(JsonObject object){
        if(object.has("set"))
            this.nextSet = object.get("set").getAsString();
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        if(!nextSet.isEmpty())
            screen.queueChangeSet(nextSet);
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
