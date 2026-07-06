package io.github.gcjojo.blablalib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.api.BlablaLibAPIImpl;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.github.gcjojo.liblib.LibLib;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class BlablaLib {
    public static final String MOD_ID = "blablalib";
    public static final ResourceLocation BLABLALIB_DIALOGUE_DATA_ID = ResourceLocation.tryBuild(MOD_ID, "dialogue_data");
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    public static void init() {
        BlablaLibNetwork.registerPackets();
        DialogueManager.registerDefaultActions();

        LibLib.setBlablaLibAPI(new BlablaLibAPIImpl());

        BlablalibEvents.DIALOGUE_COMPLETED.register((ServerPlayer player, ResourceLocation completedDialogue) -> {
            LOGGER.warn("Player {} has completed dialogue {}", player.getName().getString(), completedDialogue.toString());
            BlablaLibAPIImpl.DIALOGUE_COMPLETED.invoker().onDialogueCompleted(player, completedDialogue);
            return EventResult.pass();
        });

        PlayerEvent.PLAYER_JOIN.register((ServerPlayer player) -> {
            LibLib.getPlayerDataManager().setAdditionalData(player, BlablaLib.MOD_ID, getPlayerDataManager().getAllData(player));
        });

        PlayerEvent.PLAYER_QUIT.register(DialogueCommand::disconnectPlayer);
    }

    public static void initClient() {
        BlablaLibNetwork.registerClientPackets();
    }

    @Deprecated
    public static PlayerDataManager getPlayerDataManager() {
        return PLAYER_DATA_MANAGER;
    }

    @Deprecated
    public static void setPlayerDataManager(PlayerDataManager newPlayerDataManager) {
        PLAYER_DATA_MANAGER = newPlayerDataManager;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void openDialogue(ServerPlayer player) {
        ResourceLocation currentDialogue = LibLib.getPlayerDataManager().deserializePlayerData(player, BLABLALIB_DIALOGUE_DATA_ID, BlablaLibPlayerSaveData.class).getCurrentDialogue();
        if (currentDialogue == null) return;

        openDialogue(player, currentDialogue);
    }

    public static void openDialogue(ServerPlayer player, ResourceLocation dialogue) {
        if (isPlayerInDialogue(player)) return;

        setPlayerDialogue(player, dialogue);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(dialogue.toString());
        NetworkManager.sendToPlayer(player, BlablaLibNetwork.OPEN_DIALOGUE_PACKET_ID, buf);
    }

    public static BlablaLibPlayerSaveData getPlayerData(ServerPlayer player) {
        return LibLib.getPlayerDataManager().deserializePlayerData(player, BLABLALIB_DIALOGUE_DATA_ID, BlablaLibPlayerSaveData.class);
    }

    public static void setPlayerDialogue(ServerPlayer player, ResourceLocation dialogue) {
        getPlayerData(player).setCurrentDialogue(dialogue);
    }

    public static boolean isPlayerInDialogue(ServerPlayer player) {
        return getPlayerData(player).isInDialogue();
    }

    public static void setPlayerIsInDialogue(ServerPlayer player, boolean isInDialogue) {
        getPlayerData(player).setInDialogue(isInDialogue);
    }

    public static ResourceLocation getPlayerDialogue(ServerPlayer player) {
        return getPlayerData(player).getCurrentDialogue();
    }

    public static ResourceLocation getPlayerLastReadDialogue(ServerPlayer player) {
        return getPlayerData(player).getLastReadDialogue();
    }

    public static void setPlayerLastReadDialogue(ServerPlayer player, @Nullable ResourceLocation playerDialogue) {
        getPlayerData(player).setLastReadDialogue(playerDialogue);
    }

    public static void resetPlayerLastReadDialogue(ServerPlayer player) {
        setPlayerLastReadDialogue(player, null);
    }

}
