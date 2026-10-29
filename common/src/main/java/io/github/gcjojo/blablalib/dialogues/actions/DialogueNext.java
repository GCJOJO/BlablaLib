package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DialogueNext extends DialogueAction {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueNext> NEXT_STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueNext::getNextDialogue,
            DialogueNext::new
    );
    private static final DialogueManager.DialogueActionType<DialogueNext> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_next"),
            NEXT_STREAM_CODEC
    );
    @Getter
    ResourceLocation nextDialogue;

    public DialogueNext(ResourceLocation nextDialogue) {
        this.nextDialogue = nextDialogue;
    }

    public DialogueNext(JsonObject object) {
        // "set" field will be deprecated
        if(object.has("set"))
            this.nextDialogue = ResourceLocation.tryParse(object.get("set").getAsString());
        if(object.has("dialogue"))
            this.nextDialogue = ResourceLocation.tryParse(object.get("dialogue").getAsString());
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        //if(nextDialogue != null)
        //    screen.queueChange(nextDialogue);
    }

    @Override
    public void actionCompleted(ServerPlayer player) {
        BlablaLib.openDialogue(player, nextDialogue);
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

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
