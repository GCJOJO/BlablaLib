package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DialogueExecuteCommand extends DialogueAction {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueExecuteCommand> COMMAND_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DialogueExecuteCommand::getCommand,
            DialogueExecuteCommand::new
    );
    private static final DialogueManager.DialogueActionType<DialogueExecuteCommand> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_execute_command"),
            COMMAND_STREAM_CODEC
    );
    @Getter
    private String command;

    public DialogueExecuteCommand(String command) {
        this.command = command;
    }

    public DialogueExecuteCommand(JsonObject object){
        if(object.has("command")) {
            this.command = object.get("command").getAsString();
        }
    }

    @Override
    public void setup(DialogueScreen screen){
        super.setup(screen);
        screen.queueAdvanceDialogue();
    }

    @Override
    public void actionCompleted(ServerPlayer player) {
        if(this.command != null) {
            if (this.command.contains("<PLAYER>"))
                this.command = this.command.replace("<PLAYER>", player.getName().getString());

            MinecraftServer server = player.getServer();

            if (server == null) return;

            String formattedCommand = "execute positioned as %s rotated as %s run %s".formatted(player.getName().getString(), player.getName().getString(), command);
            try {
                int success = server.getCommands().getDispatcher().execute(formattedCommand, server.createCommandSourceStack().withEntity(player).withLevel((ServerLevel) player.level()));
                BlablaLib.getLogger().warn(String.valueOf(success));
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
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

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
