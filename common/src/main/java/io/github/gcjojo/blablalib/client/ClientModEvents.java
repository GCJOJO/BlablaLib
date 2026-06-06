package io.github.gcjojo.blablalib.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientModEvents {
    public static void registerClientModEvents() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((player) -> {
            List<ResourceLocation> dialogueList = DialogueManager.getDialogueList();

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(dialogueList.size());
            for(int i = 0; i <= dialogueList.size() - 1; i++)
                buf.writeResourceLocation(dialogueList.get(i));

            NetworkManager.sendToServer(BlablaLibNetwork.SEND_DIALOGUE_LIST_ID, buf);
        });
    }
}
