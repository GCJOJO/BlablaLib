package io.github.gcjojo.blablalib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import io.github.gcjojo.blablalib.api.BlablaLibAPIImpl;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.entities.BlablaLibEntityDataSerializers;
import io.github.gcjojo.blablalib.entities.BlablaLibEntityTypes;
import io.github.gcjojo.blablalib.entities.NPC;
import io.github.gcjojo.blablalib.events.BlablalibEvents;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.factory.PlayerDataRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BlablaLib {
    public static final String MOD_ID = "blablalib";
    public static final ResourceLocation BLABLALIB_DIALOGUE_DATA_ID = ResourceLocation.tryBuild(MOD_ID, "dialogue_data");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<UUID, Map<Integer, UUID>> playerNPCs = new HashMap<>();
    @Deprecated
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    public static void init() {
        BlablaLibNetwork.registerPackets();
        BlablaLibEntityDataSerializers.register();

        DialogueManager.registerDefaultActions();

        PlayerDataRegistry.register(BlablaLibPlayerSaveData.class, BlablaLibPlayerSaveData::new);
        LibLib.setBlablaLibAPI(new BlablaLibAPIImpl());



        BlablaLibEntityTypes.registerEntityTypes();
        EntityAttributeRegistry.register(() -> BlablaLibEntityTypes.NPC_TYPE.get(), NPC::createAttributes);

        BlablalibEvents.DIALOGUE_COMPLETED.register((ServerPlayer player, ResourceLocation completedDialogue) -> {
            LOGGER.warn("Player {} has completed dialogue {}", player.getName().getString(), completedDialogue.toString());
            BlablaLibAPIImpl.DIALOGUE_COMPLETED.invoker().onDialogueCompleted(player, completedDialogue);
            return EventResult.pass();
        });

        PlayerEvent.PLAYER_JOIN.register((ServerPlayer player) -> {
            LibLib.getPlayerDataManager().setAdditionalData(player, BlablaLib.MOD_ID, getPlayerDataManager().getAllData(player));
        });

        PlayerEvent.PLAYER_QUIT.register(DialogueCommand::disconnectPlayer);

        InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) -> {
            if(entity instanceof NPC npc)
                return EventResult.interrupt(npc.playerInteraction(player, hand));
            return EventResult.pass();
        });
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
        BlablaLibPlayerSaveData saveData = getPlayerData(player);
        saveData.setCurrentDialogue(dialogue);
        LibLib.getPlayerDataManager().serializePlayerData(player, saveData, BLABLALIB_DIALOGUE_DATA_ID);
    }

    public static boolean isPlayerInDialogue(ServerPlayer player) {
        return getPlayerData(player).isInDialogue();
    }

    public static void setPlayerIsInDialogue(ServerPlayer player, boolean isInDialogue) {
        BlablaLibPlayerSaveData saveData = getPlayerData(player);
        saveData.setInDialogue(isInDialogue);
        LibLib.getPlayerDataManager().serializePlayerData(player, saveData, BLABLALIB_DIALOGUE_DATA_ID);
    }

    public static ResourceLocation getPlayerDialogue(ServerPlayer player) {
        return getPlayerData(player).getCurrentDialogue();
    }

    public static ResourceLocation getPlayerLastReadDialogue(ServerPlayer player) {
        return getPlayerData(player).getLastReadDialogue();
    }

    public static void setPlayerLastReadDialogue(ServerPlayer player, @Nullable ResourceLocation playerDialogue) {
        BlablaLibPlayerSaveData saveData = getPlayerData(player);
        saveData.setLastReadDialogue(playerDialogue);
        LibLib.getPlayerDataManager().serializePlayerData(player, saveData, BLABLALIB_DIALOGUE_DATA_ID);
    }

    public static void resetPlayerLastReadDialogue(ServerPlayer player) {
        setPlayerLastReadDialogue(player, null);
    }

    public static boolean playerHasNPC(ServerPlayer player, int npcId){
        if(!playerNPCs.containsKey(player.getUUID()))
            return false;
        return playerNPCs.get(player.getUUID()).containsKey(npcId);
    }

    public static Optional<NPC> getPlayerNPC(ServerPlayer player, int npcId) {
        if(!playerHasNPC(player, npcId))
            return Optional.empty();

        ServerLevel level = (ServerLevel) player.level();
        UUID npcUUID = playerNPCs.get(player.getUUID()).get(npcId);
        Entity npcEntity = level.getEntity(npcUUID);

        if(!(npcEntity instanceof NPC npc))
            return Optional.empty();

        return Optional.of(npc);
    }

    public static void setPlayerNPC(ServerPlayer player, NPC npc, int npcId) {
        playerNPCs.computeIfAbsent(player.getUUID(), playerUUID -> new HashMap<>())
                .put(npcId, npc.getUUID());
    }
}
