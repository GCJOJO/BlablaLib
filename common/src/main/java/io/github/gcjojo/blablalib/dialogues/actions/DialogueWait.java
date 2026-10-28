package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueWait extends DialogueAction {

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueWait> WAIT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, DialogueWait::getWaitTime,
            DialogueWait::new
    );
    private static final DialogueManager.DialogueActionType<DialogueWait> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_wait"),
            WAIT_STREAM_CODEC
    );
    @Getter
    private final float waitTime;
    private float currentWaitTime;

    public DialogueWait(float waitTime){
        this.waitTime = waitTime;
    }

    public DialogueWait(JsonObject object){
        if(object.has("time"))
            this.waitTime = object.get("time").getAsFloat();
        else
            this.waitTime = 0.0f;
    }

    @Override
    public void step() {
        currentWaitTime++;
        if(currentWaitTime >= waitTime)
            screen.queueAdvanceDialogue();
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) { }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) { }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public boolean isSkippable() { return true; }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
