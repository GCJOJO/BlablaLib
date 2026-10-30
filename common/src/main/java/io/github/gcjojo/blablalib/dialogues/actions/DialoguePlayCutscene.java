package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.api.CutsceneLibAPI;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;


public class DialoguePlayCutscene extends DialogueAction {

    @Getter
    private ResourceLocation cutsceneId;
    @Getter
    private boolean waitingForEnd;

    public static final StreamCodec<RegistryFriendlyByteBuf, DialoguePlayCutscene> PLAY_CUTSCENE_STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialoguePlayCutscene::getCutsceneId,
            ByteBufCodecs.BOOL, DialoguePlayCutscene::isWaitingForEnd,
            DialoguePlayCutscene::new
    );

    private static final DialogueManager.DialogueActionType<DialoguePlayCutscene> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_play_cutscene"),
            PLAY_CUTSCENE_STREAM_CODEC
    );

    public DialoguePlayCutscene(ResourceLocation cutsceneId, boolean waitingForEnd) {
        this.cutsceneId = cutsceneId;
        this.waitingForEnd = waitingForEnd;
    }

    public DialoguePlayCutscene(JsonObject object) {
        if(object.has("cutscene"))
            cutsceneId = ResourceLocation.tryParse(object.get("cutscene").getAsString());
        if(object.has("wait_for_end"))
            waitingForEnd = object.get("wait_for_end").getAsBoolean();
    }

    @Override
    public void setup(DialogueScreen dialogueScreen) {
        super.setup(dialogueScreen);

        CutsceneLibAPI api = LibLib.getCutscenelibAPI();
        api.playCutsceneLocal(getCutsceneId());

        if(!isWaitingForEnd()) {
            dialogueScreen.queueAdvanceDialogue();
            return;
        }

        CutsceneLibAPI.CUTSCENE_COMPLETED.register(this::onCutsceneCompleted);
    }

    public void onCutsceneCompleted(ResourceLocation cutsceneId) {
        if(!cutsceneId.equals(getCutsceneId())) return;

        CutsceneLibAPI.CUTSCENE_COMPLETED.unregister(this::onCutsceneCompleted);
        screen.queueAdvanceDialogue();
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
