package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class DialogueQuestStateAction extends DialogueAction {
    private Optional<ResourceLocation> questId = Optional.empty();
    // If present will target the task instead of the quest
    private Optional<ResourceLocation> taskId = Optional.empty();
    private String newState;

    public DialogueQuestStateAction(JsonObject json) {
        if (json.has("quest"))
            questId = Optional.ofNullable(ResourceLocation.tryParse(json.get("quest").getAsString()));
        if (json.has("task"))
            taskId = Optional.ofNullable(ResourceLocation.tryParse(json.get("task").getAsString()));
        if (json.has("state"))
            newState = json.get("state").getAsString();
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        if (!newState.isEmpty()) {
            questId.ifPresent(questId -> {
                taskId.ifPresentOrElse(taskId -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeResourceLocation(questId);
                    buf.writeResourceLocation(taskId);
                    buf.writeUtf(newState);
                    NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_TASK_SET_STATE_ID, buf);
                }, () -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeResourceLocation(questId);
                    buf.writeUtf(newState);
                    NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_QUEST_SET_STATE_ID, buf);
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
}
