package io.github.gcjojo.blablalib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.client.ClientModEvents;
import io.github.gcjojo.blablalib.client.SoundPlayer;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public final class BlablaLib {
    public static final String MOD_ID = "blablalib";
    private static SoundPlayer SOUND_PLAYER;
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        BlablaLibNetwork.registerPackets();
        DialogueManager.registerDefaultActions();

        BlablalibEvents.DIALOGUE_COMPLETED.register((ServerPlayer player, ResourceLocation completedDialogue) -> {
            LOGGER.warn("Player {} has completed dialogue {}", player.getName().getString(), completedDialogue.toString());
            return EventResult.pass();
        });

        PlayerEvent.PLAYER_CLONE.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) -> {
            PLAYER_DATA_MANAGER.copyPlayer(oldPlayer, newPlayer);
        });

        PlayerEvent.PLAYER_QUIT.register(DialogueCommand::disconnectPlayer);
    }

    public static void initClient() {
        BlablaLibNetwork.registerClientPackets();
    }

    public static SoundPlayer getSoundPlayer() { return SOUND_PLAYER; }
    public static void setSoundPlayer(SoundPlayer newSoundPlayer) { SOUND_PLAYER = newSoundPlayer; }

    public static PlayerDataManager getPlayerDataManager() { return PLAYER_DATA_MANAGER; }
    public static void setPlayerDataManager(PlayerDataManager newPlayerDataManager) { PLAYER_DATA_MANAGER = newPlayerDataManager; }

    public static Logger getLogger() { return LOGGER; }

    public static void openDialogue(ServerPlayer player){
        ResourceLocation currentDialogue = PLAYER_DATA_MANAGER.getPlayerCurrentDialogue(player);
        if(currentDialogue == null) return;

        openDialogue(player, currentDialogue);
    }

    public static void openDialogue(ServerPlayer player, ResourceLocation dialogue) {
        if (PLAYER_DATA_MANAGER.getPlayerInDialogue(player)) return;

        PLAYER_DATA_MANAGER.setPlayerCurrentDialogue(player, dialogue);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(dialogue.toString());
        NetworkManager.sendToPlayer(player, BlablaLibNetwork.OPEN_DIALOGUE_PACKET_ID, buf);
    }

    public static void setPlayerDialogue(ServerPlayer player, ResourceLocation dialogue) {
        PLAYER_DATA_MANAGER.setPlayerCurrentDialogue(player, dialogue);
    }

    public static boolean isPlayerInDialogue(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getPlayerInDialogue(player);
    }

    public static void setPlayerIsInDialogue(ServerPlayer player, boolean isInDialogue) {
        PLAYER_DATA_MANAGER.setPlayerInDialogue(player, isInDialogue);
    }

    public static ResourceLocation getPlayerDialogue(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getPlayerCurrentDialogue(player);
    }

    public static ResourceLocation getPlayerLastReadDialogue(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getPlayerLastReadDialogue(player);
    }

    public static void resetPlayerLastReadDialogue(ServerPlayer player){
        PLAYER_DATA_MANAGER.setPlayerLastReadDialogue(player, null);
    }

    public static CompoundTag getPlayerAdditionalData(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getAdditionalData(player);
    }

    public static void setPlayerLastReadDialogue(ServerPlayer player, ResourceLocation playerDialogue) {
        PLAYER_DATA_MANAGER.setPlayerLastReadDialogue(player, playerDialogue);
    }
}
