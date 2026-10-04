package io.github.gcjojo.blablalib.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.network.payloads.DialogueListPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientModEvents {
    public static void registerClientModEvents() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((player) -> {
            List<ResourceLocation> dialogueList = DialogueManager.getDialogueList();

            NetworkManager.sendToServer(new DialogueListPayload(dialogueList));
        });
    }
}
