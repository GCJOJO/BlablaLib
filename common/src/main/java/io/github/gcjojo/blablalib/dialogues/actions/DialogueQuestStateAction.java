package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.network.payloads.DialogueSetQuestStatePayload;
import io.github.gcjojo.blablalib.network.payloads.DialogueSetTaskStatePayload;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class DialogueQuestStateAction extends DialogueAction {
    public static StreamCodec<RegistryFriendlyByteBuf, DialogueQuestStateAction> QUEST_STATE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), DialogueQuestStateAction::getQuestId,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), DialogueQuestStateAction::getTaskId,
            ByteBufCodecs.STRING_UTF8, DialogueQuestStateAction::getNewState,
            DialogueQuestStateAction::new
    );
    private static DialogueManager.DialogueActionType<DialogueQuestStateAction> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_quest_state"),
            QUEST_STATE_STREAM_CODEC
    );
    @Getter
    private Optional<ResourceLocation> questId = Optional.empty();
    // If present will target the task instead of the quest
    @Getter
    private Optional<ResourceLocation> taskId = Optional.empty();
    @Getter
    private String newState;

    public DialogueQuestStateAction(Optional<ResourceLocation> questId, Optional<ResourceLocation> taskId, String newState) {
        this.questId = questId;
        this.taskId = taskId;
        this.newState = newState;
    }

    public DialogueQuestStateAction(JsonObject json) {
        if (json.has("quest"))
            questId = Optional.ofNullable(ResourceLocation.tryParse(json.get("quest").getAsString()));
        if (json.has("task"))
            taskId = Optional.ofNullable(ResourceLocation.tryParse(json.get("task").getAsString()));
        if (json.has("state"))
            newState = json.get("state").getAsString();
    }

    @Override
    public void setup(DialogueScreenOld screen) {
        super.setup(screen);
        if (!newState.isEmpty()) {
            questId.ifPresent(questId -> {
                taskId.ifPresentOrElse(taskId -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeResourceLocation(questId);
                    buf.writeResourceLocation(taskId);
                    buf.writeUtf(newState);
                    NetworkManager.sendToServer(new DialogueSetTaskStatePayload(questId, taskId, newState));
                }, () -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeResourceLocation(questId);
                    buf.writeUtf(newState);
                    NetworkManager.sendToServer(new DialogueSetQuestStatePayload(questId, newState));
                });
            });
        }
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
        return true;
    }

    @Override
    public boolean isSkippable() {
        return false;
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
