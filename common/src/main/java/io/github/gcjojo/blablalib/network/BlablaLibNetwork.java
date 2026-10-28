package io.github.gcjojo.blablalib.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.payloads.*;
import io.github.gcjojo.liblib.LibLib;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class BlablaLibNetwork {

    public static void registerPackets() {

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueScreenOpenedPayload.TYPE, DialogueScreenOpenedPayload.STREAM_CODEC, (payload, context) -> {
            Player player = context.getPlayer();
            BlablaLib.setPlayerIsInDialogue((ServerPlayer) player, true);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueChoicePayload.TYPE, DialogueChoicePayload.STREAM_CODEC, (payload, context) -> {
            Player player = context.getPlayer();

            BlablaLib.setPlayerIsInDialogue((ServerPlayer) player, false);
            BlablaLib.setPlayerLastReadDialogue((ServerPlayer) player, BlablaLib.getPlayerDialogue((ServerPlayer) player));

            if (player instanceof ServerPlayer) {
                EventResult result = BlablalibEvents.DIALOGUE_CHOICE_MADE.invoker().dialogueChoiceMade((ServerPlayer) player, payload.nextDialogue(), payload.saveDialogue(), payload.action());
                if ((result.isPresent() && result.isTrue()) || result.isEmpty()) {
                    BlablaLib.openDialogue((ServerPlayer) player, payload.nextDialogue());
                    BlablaLib.setPlayerDialogue((ServerPlayer) player, payload.saveDialogue());
                }
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueCompletedPayload.TYPE, DialogueCompletedPayload.STREAM_CODEC, (payload, context) -> {
            Player player = context.getPlayer();

            if(!(player instanceof ServerPlayer serverPlayer)) return;

            DialogueManager.getPlayerStatus(serverPlayer).ifPresent(status -> {
                ResourceLocation dialogueId = status.getCurrentDialogue().getDialogueId();
                BlablaLib.setPlayerLastReadDialogue(serverPlayer, dialogueId);
                BlablaLib.setPlayerIsInDialogue(serverPlayer, false);

                BlablalibEvents.DIALOGUE_COMPLETED.invoker().dialogueCompleted(serverPlayer, dialogueId);
            });

            DialogueManager.resetPlayerStatus(serverPlayer);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueAdvancedPayload.TYPE, DialogueAdvancedPayload.STREAM_CODEC, (payload, context) -> {
            if(!(context.getPlayer() instanceof ServerPlayer player)) return;

            DialogueManager.getPlayerStatus(player).ifPresent(status -> {
                status.setActionIndex(payload.newActionIndex());
                DialogueManager.sendNextActions(player);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueCommandPayload.TYPE, DialogueCommandPayload.STREAM_CODEC, (payload, context) -> {
            Player player = context.getPlayer();
            if (!(player instanceof ServerPlayer)) return;

            String command = payload.command();
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

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueSetQuestStatePayload.TYPE, DialogueSetQuestStatePayload.STREAM_CODEC, (payload, context) -> {
            Player player = context.getPlayer();
            ResourceLocation questId = payload.questId();
            String newState = payload.newState();
            LibLib.getQuestsLibAPI().setQuestCompletionState(player, questId, newState);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueSetTaskStatePayload.TYPE, DialogueSetTaskStatePayload.STREAM_CODEC, (payload, context) -> {
            Player player = context.getPlayer();
            ResourceLocation questId = payload.questId();
            ResourceLocation taskId = payload.taskId();
            String newState = payload.newState();
            LibLib.getQuestsLibAPI().setTaskCompletionState(player, questId, taskId, newState);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DialogueSetNPCPayload.TYPE, DialogueSetNPCPayload.STREAM_CODEC, (payload, context) -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer();

            int npcId = payload.npcId();
            String newModel = payload.npcModel();
            String newTexture = payload.npcTexture();
            String newAnimation = payload.npcAnimation();
            boolean loopAnimation = payload.loopAnimation();

            if(!BlablaLib.playerHasNPC(player, npcId)) return;

            BlablaLib.getPlayerNPC(player, npcId).ifPresent(npc -> {
                if(!newModel.isEmpty())
                    npc.setCurrentModel(newModel);
                if(!newTexture.isEmpty())
                    npc.setCurrentTexture(newTexture);
                if(!newAnimation.isEmpty())
                    npc.setCurrentAnimation(newAnimation, loopAnimation);
            });
        });

        if (Platform.getEnv() == EnvType.CLIENT) return;

        NetworkManager.registerS2CPayloadType(OpenDialoguePayload.TYPE, OpenDialoguePayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(DialogueActionsPayload.TYPE, DialogueActionsPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(DialogueSpeakersPayload.TYPE, DialogueSpeakersPayload.STREAM_CODEC);
    }

    public static void registerClientPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, OpenDialoguePayload.TYPE, OpenDialoguePayload.STREAM_CODEC, (payload, context) -> {
            if (payload.dialogueId() != null)
                context.queue(() -> Minecraft.getInstance().setScreen(new DialogueScreen(payload.dialogueId())));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DialogueActionsPayload.TYPE, DialogueActionsPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                if(!(Minecraft.getInstance().screen instanceof DialogueScreen dialogueScreen)) return;
                dialogueScreen.receiveActions(payload.actions());
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DialogueSpeakersPayload.TYPE, DialogueSpeakersPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                if(!(Minecraft.getInstance().screen instanceof DialogueScreen dialogueScreen)) return;
                dialogueScreen.receiveSpeakers(payload.speakers());
            });
        });
    }
}
