package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;



public class DialogueNPC extends DialogueAction {
    @Getter
    private final ResourceLocation targetNpcDefinition;
    @Getter
    private final String newModel;
    @Getter
    private final String newTexture;
    @Getter
    private final String newAnimation;
    @Getter
    private final boolean loopAnimation;

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueNPC> NPC_STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueNPC::getTargetNpcDefinition,
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
            targetNpcDefinition = ResourceLocation.tryParse(json.get("npc").getAsString());
        else
            throw new IllegalStateException("NPC Action need to target a definition");

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

    public DialogueNPC(ResourceLocation targetNpcDefinition, String newModel, String newTexture, String newAnimation, boolean loopAnimation) {
        this.targetNpcDefinition = targetNpcDefinition;
        this.newModel = newModel;
        this.newTexture = newTexture;
        this.newAnimation = newAnimation;
        this.loopAnimation = loopAnimation;
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
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
