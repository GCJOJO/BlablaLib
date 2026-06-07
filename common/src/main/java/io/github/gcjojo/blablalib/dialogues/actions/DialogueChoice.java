package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DialogueChoice extends DialogueAction {
    private String choice1;
    private ResourceLocation nextSet1;
    private ResourceLocation saveSet1;
    private String action1;

    private String choice2;
    private ResourceLocation nextSet2;
    private ResourceLocation saveSet2;
    private String action2;

    public DialogueChoice(String choice1, ResourceLocation nextSet1, ResourceLocation saveSet1, String action1,
                          String choice2, ResourceLocation nextSet2, ResourceLocation saveSet2, String action2)
    {
        this.choice1 = choice1;
        this.nextSet1 = nextSet1;
        this.saveSet1 = saveSet1;
        this.action1 = action1;

        this.choice2 = choice2;
        this.nextSet2 = nextSet2;
        this.saveSet2 = saveSet2;
        this.action2 = action2;
    }

    public DialogueChoice(JsonObject object){
        this.choice1  = object.get("option1").getAsString();
        this.nextSet1 = ResourceLocation.tryParse(object.get("next1").getAsString());
        this.saveSet1 = ResourceLocation.tryParse(object.get("save1").getAsString());
        this.action1  = object.get("action1").getAsString();
        this.choice2  = object.get("option2").getAsString();
        this.nextSet2 = ResourceLocation.tryParse(object.get("next2").getAsString());
        this.saveSet2 = ResourceLocation.tryParse(object.get("save2").getAsString());
        this.action2  = object.get("action2").getAsString();
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);

        int btnWidth = 140;
        int btnHeight = 20;
        int yPos = screen.height / 2 + 50;

        screen.drawButton(Button.builder(Component.translatable(choice1), b -> {
            if(nextSet1 != null && saveSet1 != null)
                screen.handleChoiceSelection(nextSet1, saveSet1, action1);
            else
                screen.endDialogue();
        }).bounds(screen.width / 4 - btnWidth / 2, yPos, btnWidth, btnHeight).build());

        screen.drawButton(Button.builder(Component.translatable(choice2), b -> {
            if(nextSet2 != null && saveSet2 != null)
                screen.handleChoiceSelection(nextSet2, saveSet2, action2);
            else
                screen.endDialogue();
        }).bounds(3 * screen.width / 4 - btnWidth / 2, yPos, btnWidth, btnHeight).build());
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
    public boolean isBlocking() { return true; }

    @Override
    public boolean isSkippable() { return false; }
}
