package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.network.payloads.DialogueChoicePayload;
import io.github.gcjojo.liblib.client.gui.elements.GuiBoxContainer;
import io.github.gcjojo.liblib.client.gui.elements.GuiButton;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class DialogueChoice extends DialogueAction {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoice> CHOICE_STREAM_CODEC = StreamCodec.composite(
            Option.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogueChoice::getOptions,
            DialogueChoice::new
    );
    private static final DialogueManager.DialogueActionType<DialogueChoice> TYPE = DialogueManager.registerActionType(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_choice"), CHOICE_STREAM_CODEC);

    @Getter
    private final List<Option> options;

    private final List<GuiButton> buttons = new ArrayList<>();
    private GuiBoxContainer buttonContainer;

    /*public DialogueChoice(String choice1, ResourceLocation nextSet1, ResourceLocation saveSet1, String action1,
                          String choice2, ResourceLocation nextSet2, ResourceLocation saveSet2, String action2) {
        option1 = new Option(choice1, nextSet1, saveSet1, action1);
        option2 = new Option(choice2, nextSet2, saveSet2, action2);
    }*/

    public DialogueChoice(List<Option> options) {
        this.options = options;
    }

    public DialogueChoice(JsonObject object) {
        this.options = new ArrayList<>();

        // TODO Load a list instead of just 2 specific elements + also calculate button position
        String choice1              = object.get("option1").getAsString();
        ResourceLocation nextSet1   = ResourceLocation.tryParse(object.get("next1").getAsString());
        ResourceLocation saveSet1   = ResourceLocation.tryParse(object.get("save1").getAsString());
        String action1              = object.get("action1").getAsString();

        String choice2              = object.get("option2").getAsString();
        ResourceLocation nextSet2   = ResourceLocation.tryParse(object.get("next2").getAsString());
        ResourceLocation saveSet2   = ResourceLocation.tryParse(object.get("save2").getAsString());
        String action2              = object.get("action2").getAsString();

        options.add(new Option(choice1, nextSet1, saveSet1, action1));
        options.add(new Option(choice2, nextSet2, saveSet2, action2));
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);

        int btnWidth = 140;
        int btnHeight = 20;
        int yPos = (int)(screen.height * 0.5f) - (btnHeight * options.size()) + 50;

        buttonContainer = new GuiBoxContainer(screen, btnWidth + 2, (btnHeight + 2) * options.size(), GuiBoxContainer.BoxDirection.Vertical);
        buttonContainer.setChildrenMargin(2);
        buttonContainer.setPosition(new Vec2(screen.width * 0.5f, yPos));

        buttons.clear();

        for(Option option : options) {
            GuiButton newButton = new GuiButton(screen, Component.translatable(option.name()), () -> handleButtonClick(option));
            buttonContainer.addChild(newButton);
        }
    }

    public void handleButtonClick(Option option) {
        if(option.nextDialogue() != null && option.saveDialogue() != null)
            //screen.handleChoiceSelection(option.nextDialogue(), option.saveDialogue(), option.action());
            NetworkManager.sendToServer(new DialogueChoicePayload(option.nextDialogue(), option.saveDialogue(), option.action()));
        screen.queueEndDialogue();
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }

    @Override
    public void step() {

    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        buttonContainer.draw(graphics, mouseX, mouseY, partialTick, Vec2.ZERO);
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
