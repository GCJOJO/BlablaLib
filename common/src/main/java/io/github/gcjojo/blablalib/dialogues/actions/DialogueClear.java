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

import java.util.Optional;

public class DialogueClear extends DialogueAction {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueClear> CLEAR_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), DialogueClear::getClearedClass,
            DialogueClear::new
    );
    private static final DialogueManager.DialogueActionType<DialogueClear> TYPE = DialogueManager.registerActionType(
            ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_clear"),
            CLEAR_STREAM_CODEC
    );
    @Getter
    Optional<ResourceLocation> clearedClass = Optional.empty();

    public DialogueClear(ResourceLocation clearedClass) {
        this.clearedClass = Optional.ofNullable(clearedClass);
    }

    public DialogueClear(Optional<ResourceLocation> clearedClass) { this.clearedClass = clearedClass; }

    public DialogueClear(JsonObject object){
        if(object.has("cleared_actions"))
            this.clearedClass = Optional.ofNullable(ResourceLocation.tryParse(object.get("cleared_actions").getAsString()));
    }

    @Override
    public void setup(DialogueScreenOld screen) {
        super.setup(screen);
        clearedClass.ifPresentOrElse(screen::clearActions, screen::clearActions);
        screen.advanceDialogue();
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
    public boolean isSkippable() { return true; }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
