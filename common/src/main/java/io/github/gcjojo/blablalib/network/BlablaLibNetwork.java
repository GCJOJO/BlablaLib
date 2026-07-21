package io.github.gcjojo.blablalib.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.liblib.LibLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BlablaLibNetwork {
    public static final ResourceLocation SEND_DIALOGUE_LIST_ID = new ResourceLocation(BlablaLib.MOD_ID, "send_dialogue_list");
    public static final ResourceLocation OPEN_DIALOGUE_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "open_dialogue");
    public static final ResourceLocation DIALOGUE_SCREEN_OPENED_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_screen_opened");
    public static final ResourceLocation CHOICE_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "choice");
    public static final ResourceLocation DIALOGUE_COMPLETED_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_completed");
    public static final ResourceLocation DIALOGUE_COMMAND_PACKET_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_command");
    public static final ResourceLocation DIALOGUE_QUEST_SET_STATE_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_set_quest_state");
    public static final ResourceLocation DIALOGUE_TASK_SET_STATE_ID = new ResourceLocation(BlablaLib.MOD_ID, "dialogue_set_task_state");

    public static void registerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SEND_DIALOGUE_LIST_ID, (buf, context) -> {
            if (!(context.getPlayer() instanceof ServerPlayer)) return;

            List<ResourceLocation> dialoguePaths = new ArrayList<>();

            int dialogueCount = buf.readInt();
            for (int i = 0; i <= dialogueCount - 1; i++) {
                dialoguePaths.add(buf.readResourceLocation());
            }

            DialogueCommand.registerPlayerDialogueList((ServerPlayer) context.getPlayer(), dialoguePaths);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_SCREEN_OPENED_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            BlablaLib.setPlayerIsInDialogue((ServerPlayer) player, true);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHOICE_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            CompoundTag choiceNbt = buf.readNbt();
            ResourceLocation nextSet = ResourceLocation.tryParse(choiceNbt.getString("NextSet"));
            ResourceLocation saveSet = ResourceLocation.tryParse(choiceNbt.getString("SaveSet"));
            String action = choiceNbt.getString("Action");

            BlablaLib.setPlayerIsInDialogue((ServerPlayer) player, false);
            BlablaLib.setPlayerLastReadDialogue((ServerPlayer) player, BlablaLib.getPlayerDialogue((ServerPlayer) player));

            if (player instanceof ServerPlayer) {
                EventResult result = BlablalibEvents.DIALOGUE_CHOICE_MADE.invoker().dialogueChoiceMade((ServerPlayer) player, nextSet, saveSet, action);
                if ((result.isPresent() && result.isTrue()) || result.isEmpty()) {
                    BlablaLib.openDialogue((ServerPlayer) player, nextSet);
                    BlablaLib.setPlayerDialogue((ServerPlayer) player, saveSet);
                }
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_COMPLETED_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            ResourceLocation completedSet = ResourceLocation.tryParse(buf.readUtf());

            BlablaLib.setPlayerLastReadDialogue((ServerPlayer) player, completedSet);
            BlablaLib.setPlayerIsInDialogue((ServerPlayer) player, false);

            if (player instanceof ServerPlayer)
                BlablalibEvents.DIALOGUE_COMPLETED.invoker().dialogueCompleted((ServerPlayer) player, completedSet);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_COMMAND_PACKET_ID, (buf, context) -> {
            Player player = context.getPlayer();
            if (!(player instanceof ServerPlayer)) return;

            String command = buf.readUtf();
            context.queue(() -> {
                MinecraftServer server = player.getServer();
                if (server == null) return;

                String formattedCommand = "execute positioned as %s rotated as %s run %s".formatted(player.getName().getString(), player.getName().getString(), command);
                try {
                    int success = server.getCommands().getDispatcher().execute(formattedCommand, server.createCommandSourceStack().withEntity(player).withLevel((ServerLevel) player.level()));
                    BlablaLib.getLogger().warn(String.valueOf(success));
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_QUEST_SET_STATE_ID, (buf, context) -> {
            Player player = context.getPlayer();
            ResourceLocation questId = buf.readResourceLocation();
            String newState = buf.readUtf();
            LibLib.getQuestsLibAPI().setQuestCompletionState(player, questId, newState);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DIALOGUE_TASK_SET_STATE_ID, (buf, context) -> {
            Player player = context.getPlayer();
            ResourceLocation questId = buf.readResourceLocation();
            ResourceLocation taskId = buf.readResourceLocation();
            String newState = buf.readUtf();
            LibLib.getQuestsLibAPI().setTaskCompletionState(player, questId, taskId, newState);
        });
    }

    public static void registerClientPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, OPEN_DIALOGUE_PACKET_ID, (buf, context) -> {
            ResourceLocation dialogue = ResourceLocation.tryParse(buf.readUtf());
            if (dialogue != null)
                context.queue(() -> DialogueScreen.openDialogueScreen(dialogue));
        });
    }
}
