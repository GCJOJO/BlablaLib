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

public class DialogueUnknownAction extends DialogueAction {

    private final String action;

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueUnknownAction> UNKNOWN_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DialogueUnknownAction::getRawAction,
            DialogueUnknownAction::new
    );

    private static final DialogueManager.DialogueActionType<DialogueUnknownAction> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_unknown"),
            UNKNOWN_STREAM_CODEC
    );

    public DialogueUnknownAction(String action) { this.action = action; }

    public DialogueUnknownAction(JsonObject object){
        this.action = object.get("action").getAsString();
    }

    public String getRawAction() { return this.action; }

    @Override
    public void step() { }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) { }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) { }

    @Override
    public boolean isBlocking() { return false; }

    @Override
    public boolean isSkippable() { return true; }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
