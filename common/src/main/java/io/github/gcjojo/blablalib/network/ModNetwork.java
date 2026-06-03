package io.github.gcjojo.blablalib.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ModNetwork {
    public static final ResourceLocation OPEN_DIALOGUE_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "open_dialogue");
    public static final ResourceLocation DIALOGUE_SCREEN_OPENED_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_screen_opened");
    public static final ResourceLocation CHOICE_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "choice");
    public static final ResourceLocation DIALOGUE_COMPLETED_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_completed");
    public static final ResourceLocation DIALOGUE_COMMAND_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_command");

    public static void registerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, OPEN_DIALOGUE_PACKET_ID, (buf, context) -> {
            String dialogue = buf.readUtf();
            context.queue(() -> DialogueScreen.openForSet(dialogue));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_SCREEN_OPENED_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            BlablaLib.getPlayerDataManager().setPlayerInDialogue(player, true);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHOICE_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            CompoundTag choiceNbt = buf.readNbt();
            String nextSet = choiceNbt.getString("NextSet");
            String saveSet = choiceNbt.getString("SaveSet");
            String action = choiceNbt.getString("Action");

            BlablaLib.getPlayerDataManager().setPlayerCurrentChapter(player, nextSet);
            if(player instanceof ServerPlayer)

                BlablalibEvents.DIALOGUE_CHOICE_MADE.invoker().dialogueChoiceMade((ServerPlayer) player, nextSet, saveSet, action);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_COMPLETED_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            String completedSet = buf.readUtf();

            BlablaLib.getPlayerDataManager().setPlayerLastReadChapter(player, completedSet);
            BlablaLib.getPlayerDataManager().setPlayerInDialogue(player, false);

            if(player instanceof ServerPlayer)
                BlablalibEvents.DIALOGUE_COMPLETED.invoker().dialogueCompleted((ServerPlayer) player, completedSet);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_COMMAND_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            if(!(player instanceof ServerPlayer)) return;

            String command = buf.readUtf();
            context.queue(() -> {
                MinecraftServer server = player.getServer();
                if(server == null) return;

                String formattedCommand = "execute positioned as %s run %s".formatted(player.getName().getString(), command);
                try {
                    int success = server.getCommands().getDispatcher().execute(formattedCommand, server.createCommandSourceStack().withEntity(player).withLevel((ServerLevel) player.level()));
                    BlablaLib.getLogger().warn(String.valueOf(success));
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
