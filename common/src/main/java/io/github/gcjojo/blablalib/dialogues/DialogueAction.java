package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public abstract class DialogueAction
{
    protected DialogueScreen screen;

    public DialogueAction() { }
    public DialogueAction(JsonObject object) { }

    public void setup(DialogueScreen screen) { this.screen = screen; }

    public abstract void step();
    public abstract void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public abstract void mouseClicked(double mouseX, double mouseY, int button);
    public abstract void keyPressed(int keyCode, int scanCode, int modifiers);

    public abstract boolean isBlocking();
    public abstract boolean isSkippable();

    public void actionCompleted(ServerPlayer player) { }

    public Optional<ResourceLocation> getReferencedSpeaker() { return Optional.empty(); }

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueAction> STREAM_CODEC =
            DialogueManager.TYPE_CODEC.dispatch(DialogueAction::getType, DialogueManager.DialogueActionType::codec);

    public abstract DialogueManager.DialogueActionType<?> getType();
}
