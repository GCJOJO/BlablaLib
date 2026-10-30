package io.github.gcjojo.blablalib.network;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.entities.NPC;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.payloads.*;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

            DialogueManager.playerDialogueAdvanced(player, payload.newActionIndex());
        });


        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NPCVisualStatePayload.TYPE, NPCVisualStatePayload.STREAM_CODEC, (payload, context) -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer();

            Entity entity = player.serverLevel().getEntity(payload.entityId());
            if(!(entity instanceof NPC npc)) return;

            npc.setClientState(payload.newVisualState());
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
