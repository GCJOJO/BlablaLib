package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueStartQuestAction extends DialogueAction {

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueStartQuestAction> QUEST_START_STREAM_CODEC = StreamCodec.unit(new DialogueStartQuestAction());

    private static final DialogueManager.DialogueActionType<DialogueStartQuestAction> TYPE = DialogueManager.register(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_start_quest"),
            QUEST_START_STREAM_CODEC
    );

    public DialogueStartQuestAction() {

    }

    public DialogueStartQuestAction(JsonObject json) {
        
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
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
        return false;
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
