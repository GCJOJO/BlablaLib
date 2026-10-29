package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.network.payloads.DialogueSetNPCPayload;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueNPC extends DialogueAction {
    @Getter
    private final int npcId;
    @Getter
    private final String newModel;
    @Getter
    private final String newTexture;
    @Getter
    private final String newAnimation;
    @Getter
    private final boolean loopAnimation;

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueNPC> NPC_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueNPC::getNpcId,
            ByteBufCodecs.STRING_UTF8, DialogueNPC::getNewModel,
            ByteBufCodecs.STRING_UTF8, DialogueNPC::getNewTexture,
            ByteBufCodecs.STRING_UTF8, DialogueNPC::getNewAnimation,
            ByteBufCodecs.BOOL, DialogueNPC::isLoopAnimation,
            DialogueNPC::new
    );

    private static final DialogueManager.DialogueActionType<DialogueNPC> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_npc"),
            NPC_STREAM_CODEC
    );

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

    public DialogueNPC(int npcId, String newModel, String newTexture, String newAnimation, boolean loopAnimation) {
        this.npcId = npcId;
        this.newModel = newModel;
        this.newTexture = newTexture;
        this.newAnimation = newAnimation;
        this.loopAnimation = loopAnimation;
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        if(npcId == -1)
        {
            screen.queueAdvanceDialogue();
            return;
        }

        NetworkManager.sendToServer(new DialogueSetNPCPayload(npcId, newModel, newTexture, newAnimation, loopAnimation));

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
    public DialogueManager.DialogueActionType<?> getType() {
        return TYPE;
    }
}
