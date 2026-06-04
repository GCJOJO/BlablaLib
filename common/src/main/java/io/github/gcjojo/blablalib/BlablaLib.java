package io.github.gcjojo.blablalib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.client.SoundPlayer;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public final class BlablaLib {
    public static final String MOD_ID = "blablalib";
    private static SoundPlayer SOUND_PLAYER;
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        BlablaLibNetwork.registerPackets();

        BlablalibEvents.DIALOGUE_COMPLETED.register((ServerPlayer player, String completedDialogue) -> {
            LOGGER.warn("Player {} has completed dialogue {}", player.getName().getString(), completedDialogue);
            return EventResult.pass();
        });
    }

    public static SoundPlayer getSoundPlayer() { return SOUND_PLAYER; }
    public static void setSoundPlayer(SoundPlayer newSoundPlayer) { SOUND_PLAYER = newSoundPlayer; }

    public static PlayerDataManager getPlayerDataManager() { return PLAYER_DATA_MANAGER; }
    public static void setPlayerDataManager(PlayerDataManager newPlayerDataManager) { PLAYER_DATA_MANAGER = newPlayerDataManager; }

    public static Logger getLogger() { return LOGGER; }

    public static void openDialogue(ServerPlayer player){
        String currentDialogue = PLAYER_DATA_MANAGER.getPlayerCurrentChapter(player);
        if(currentDialogue.isEmpty()) return;

        openDialogue(player, currentDialogue);
    }

    public static void openDialogue(ServerPlayer player, String dialogue) {
        if (PLAYER_DATA_MANAGER.getPlayerInDialogue(player)) return;

        PLAYER_DATA_MANAGER.setPlayerCurrentChapter(player, dialogue);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(dialogue);
        NetworkManager.sendToPlayer(player, BlablaLibNetwork.OPEN_DIALOGUE_PACKET_ID, buf);
    }

    public static void setPlayerDialogue(ServerPlayer player, String dialogue) {
        PLAYER_DATA_MANAGER.setPlayerCurrentChapter(player, dialogue);
    }

    public static boolean isPlayerInDialogue(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getPlayerInDialogue(player);
    }

    public static String getPlayerDialogue(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getPlayerCurrentChapter(player);
    }

    public static String getPlayerLastReadDialogue(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getPlayerLastReadChapter(player);
    }

    public static CompoundTag getPlayerAdditionalData(ServerPlayer player) {
        return PLAYER_DATA_MANAGER.getAdditionalData(player);
    }
}
