package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialoguePressEnter extends DialogueAction {

    public static final StreamCodec<RegistryFriendlyByteBuf, DialoguePressEnter> PRESS_ENTER_STREAM_CODEC = StreamCodec.unit(new DialoguePressEnter());

    private static final DialogueManager.DialogueActionType<DialoguePressEnter> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "press_enter"),
            PRESS_ENTER_STREAM_CODEC
    );

    public DialoguePressEnter()
    {

    }

    public DialoguePressEnter(JsonObject json)
    {

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
        if(keyCode == 257)
            screen.queueAdvanceDialogue();
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
    public DialogueManager.DialogueActionType<?> getType() {
        return TYPE;
    }
}
