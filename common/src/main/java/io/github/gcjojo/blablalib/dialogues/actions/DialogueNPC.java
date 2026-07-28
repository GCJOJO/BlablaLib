package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;

public class DialogueNPC extends DialogueAction {
    private final int npcId;
    private final String newModel;
    private final String newTexture;
    private final String newAnimation;
    private final boolean loopAnimation;

    public DialogueNPC(JsonObject json) {
        if(json.has("npc"))
            npcId = json.get("npc").getAsInt();
        else
            npcId = -1;

        if(json.has("model"))
            newModel = json.get("model").getAsString();
        else
            newModel = "";

        if(json.has("texture"))
            newTexture = json.get("texture").getAsString();
        else
            newTexture = "";

        if(json.has("animation")) {
            newAnimation = json.get("animation").getAsString();
            if(json.has("loop"))
                loopAnimation = json.get("loop").getAsBoolean();
            else
                loopAnimation = false;
        }
        else {
            newAnimation = "";
            loopAnimation = false;
        }
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        if(npcId == -1)
        {
            screen.queueAdvanceDialogue();
            return;
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(npcId);
        buf.writeUtf(newModel);
        buf.writeUtf(newTexture);
        buf.writeUtf(newAnimation);
        buf.writeBoolean(loopAnimation);

        NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_NPC_ACTION, buf);

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
