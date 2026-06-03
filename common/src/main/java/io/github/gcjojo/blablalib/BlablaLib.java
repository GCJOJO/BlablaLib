package io.github.gcjojo.blablalib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.client.SoundPlayer;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.ModNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public final class BlablaLib {
    public static final String MOD_ID = "blablalib";
    private static SoundPlayer SOUND_PLAYER;
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.
        ModNetwork.registerPackets();

        BlablalibEvents.DIALOGUE_COMPLETED.register((ServerPlayer player, String completedDialogue) -> {
            LOGGER.warn("Player {} has completed dialogue {}", player.getName().getString(), completedDialogue);
            return EventResult.interruptDefault();
        });
    }

    public static SoundPlayer getSoundPlayer() { return SOUND_PLAYER; }
    public static void setSoundPlayer(SoundPlayer newSoundPlayer) { SOUND_PLAYER = newSoundPlayer; }

    public static PlayerDataManager getPlayerDataManager() { return PLAYER_DATA_MANAGER; }
    public static void setPlayerDataManager(PlayerDataManager newPlayerDataManager) { PLAYER_DATA_MANAGER = newPlayerDataManager; }

    public static Logger getLogger() { return LOGGER; }

    public static void OpenDialogue(ServerPlayer player, String dialogue){
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(dialogue);
        NetworkManager.sendToPlayer(player, ModNetwork.OPEN_DIALOGUE_PACKET_ID, buf);
    }
}
