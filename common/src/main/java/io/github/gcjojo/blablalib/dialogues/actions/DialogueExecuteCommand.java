package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;

public class DialogueExecuteCommand extends DialogueAction {
    private String command;

    public DialogueExecuteCommand(JsonObject object){
        if(object.has("command")) {
            this.command = object.get("command").getAsString();
        }
    }

    @Override
    public void setup(DialogueScreen screen){
        super.setup(screen);
        if(this.command != null) {
            if (this.command.contains("<PLAYER>"))
                this.command = this.command.replace("<PLAYER>", Minecraft.getInstance().player.getName().getString());

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUtf(command);
            NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_COMMAND_PACKET_ID, buf);
        }
        screen.queueAdvanceDialogue();
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
    public boolean isBlocking() {
        return true;
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
