package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueChoice extends DialogueAction {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoice> CHOICE_STREAM_CODEC = StreamCodec.composite(
            Option.STREAM_CODEC, DialogueChoice::getOption1,
            Option.STREAM_CODEC, DialogueChoice::getOption2,
            DialogueChoice::new
    );
    private static final DialogueManager.DialogueActionType<DialogueChoice> TYPE = DialogueManager.register(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_choice"), CHOICE_STREAM_CODEC);
    // TODO Make it a list one day
    @Getter
    private final Option option1;
    @Getter
    private final Option option2;

    public DialogueChoice(String choice1, ResourceLocation nextSet1, ResourceLocation saveSet1, String action1,
                          String choice2, ResourceLocation nextSet2, ResourceLocation saveSet2, String action2) {
        option1 = new Option(choice1, nextSet1, saveSet1, action1);
        option2 = new Option(choice2, nextSet2, saveSet2, action2);
    }

    public DialogueChoice(Option option1, Option option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    public DialogueChoice(JsonObject object){
        String choice1              = object.get("option1").getAsString();
        ResourceLocation nextSet1   = ResourceLocation.tryParse(object.get("next1").getAsString());
        ResourceLocation saveSet1   = ResourceLocation.tryParse(object.get("save1").getAsString());
        String action1              = object.get("action1").getAsString();

        String choice2              = object.get("option2").getAsString();
        ResourceLocation nextSet2   = ResourceLocation.tryParse(object.get("next2").getAsString());
        ResourceLocation saveSet2   = ResourceLocation.tryParse(object.get("save2").getAsString());
        String action2              = object.get("action2").getAsString();

        option1 = new Option(choice1, nextSet1, saveSet1, action1);
        option2 = new Option(choice2, nextSet2, saveSet2, action2);
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);

        int btnWidth = 140;
        int btnHeight = 20;
        int yPos = screen.height / 2 + 50;

        screen.drawButton(Button.builder(Component.translatable(option1.name()), b -> {
            if(option1.nextDialogue() != null && option1.saveDialogue() != null)
                screen.handleChoiceSelection(option1.nextDialogue(), option1.saveDialogue(), option1.action());
            else
                screen.endDialogue();
        }).bounds(screen.width / 4 - btnWidth / 2, yPos, btnWidth, btnHeight).build());

        screen.drawButton(Button.builder(Component.translatable(option2.name()), b -> {
            if(option2.nextDialogue() != null && option2.saveDialogue() != null)
                screen.handleChoiceSelection(option2.nextDialogue(), option2.saveDialogue(), option2.action());
            else
                screen.endDialogue();
        }).bounds(3 * screen.width / 4 - btnWidth / 2, yPos, btnWidth, btnHeight).build());
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }

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

    public record Option(String name, ResourceLocation nextDialogue, ResourceLocation saveDialogue, String action) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Option> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Option::name,
                ResourceLocation.STREAM_CODEC, Option::nextDialogue,
                ResourceLocation.STREAM_CODEC, Option::saveDialogue,
                ByteBufCodecs.STRING_UTF8, Option::action,
                Option::new);
    }
}
