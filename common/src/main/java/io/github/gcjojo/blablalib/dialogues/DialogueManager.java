package io.github.gcjojo.blablalib.dialogues;

import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.actions.*;
import io.github.gcjojo.blablalib.network.payloads.DialogueActionsPayload;
import io.github.gcjojo.blablalib.network.payloads.DialogueSpeakersPayload;
import io.github.gcjojo.blablalib.network.payloads.OpenDialoguePayload;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class DialogueManager {
    private static final int BLOCKING_ACTIONS_SEND_AMOUNT = 3;

    private static final Map<UUID, PlayerDialogueStatus> STATUSES = new HashMap<>();
    // Claude Slop
    private static final Map<ResourceLocation, DialogueActionType<?>> TYPES = new HashMap<>();

    public static <T extends DialogueAction> DialogueActionType<T> registerActionType(ResourceLocation id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        DialogueActionType<T> type = new DialogueActionType<>(id, codec);
        TYPES.put(id, type);
        return type;
    }

    public static void playDialogue(ServerPlayer player, Dialogue dialogue) {
        List<DialogueSpeaker> speakers =
                DialogueLoader.speakers.stream().filter(speaker -> dialogue.getDialogueSpeakersId().contains(speaker.getId())).toList();

        PlayerDialogueStatus status = new PlayerDialogueStatus(player.getUUID());
        status.setCurrentDialogue(dialogue);
        STATUSES.put(player.getUUID(), status);

        NetworkManager.sendToPlayer(player, new OpenDialoguePayload(dialogue.getDialogueId()));

        NetworkManager.sendToPlayer(player, new DialogueSpeakersPayload(speakers));
        sendNextActions(player);
    }

    public static Optional<PlayerDialogueStatus> getPlayerStatus(ServerPlayer player) {
        if(!STATUSES.containsKey(player.getUUID()))
            return Optional.empty();
        return Optional.ofNullable(STATUSES.get(player.getUUID()));
    }

    public static void resetPlayerStatus(ServerPlayer player) {
        STATUSES.remove(player.getUUID());
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueActionType<?>> TYPE_CODEC =
        ResourceLocation.STREAM_CODEC.<DialogueActionType<?>>map(TYPES::get, DialogueActionType::id).cast();

    public static void sendNextActions(ServerPlayer player) {
        if(!STATUSES.containsKey(player.getUUID()))
            return;

        PlayerDialogueStatus status = STATUSES.get(player.getUUID());
        Dialogue currentDialogue = status.getCurrentDialogue();

        int blockingActions = 0;
        int lastSendActionIndex = status.getLastReceivedActionIndex();

        List<DialogueAction> actionsToSend = currentDialogue.getDialogueActions().subList(lastSendActionIndex, currentDialogue.getDialogueActions().size());
        List<DialogueAction> sentActions = new ArrayList<>();

        for(DialogueAction action : actionsToSend) {
            if(blockingActions >= BLOCKING_ACTIONS_SEND_AMOUNT)
                break;

            sentActions.add(action);

            lastSendActionIndex++;

            if(action.isBlocking())
                blockingActions++;
        }
        status.setLastReceivedActionIndex(lastSendActionIndex);

        NetworkManager.sendToPlayer(player, new DialogueActionsPayload(blockingActions, sentActions));
    }

    public static void playerDialogueAdvanced(ServerPlayer player, int newActionIndex) {
        DialogueManager.getPlayerStatus(player).ifPresent(status -> {
            int oldActionIndex = status.getActionIndex();

            List<DialogueAction> actions = status.getCurrentDialogue().getDialogueActions();
            for(int i = oldActionIndex; i <= newActionIndex; i++) {
                DialogueAction action = actions.get(i);
                action.actionCompleted(player);
            }

            status.setActionIndex(newActionIndex);
            DialogueManager.sendNextActions(player);
        });
    }

    @Getter
    private static final Map<ResourceLocation, Class<? extends DialogueAction>> dialogueActionClasses = new HashMap<>();
    @Getter
    private static final Map<String, Map<ResourceLocation, ActionOverload<?>>> dialogueActionOverloads = new HashMap<>();

    public static void registerDefaultActions(){
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "clear"),          DialogueClear.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "wait"),           DialogueWait.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "choice"),         DialogueChoice.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "change_set"),     DialogueNext.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "next_dialogue"),  DialogueNext.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "fade"),           DialogueFading.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "message"),        DialogueMessage.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "image"),          DialogueImage.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "credit"),         DialogueText.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "text"),           DialogueText.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "image_move"),     DialogueMoveImage.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "command"),        DialogueExecuteCommand.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "sound"),          DialogueSound.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "timings"),        DialogueTimings.class);
        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "npc"),            DialogueNPC.class);

        dialogueActionClasses.put(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "quest_state"),    DialogueQuestStateAction.class);
    }

    public static Class<? extends DialogueAction> getActionClass(ResourceLocation action) {
        if(dialogueActionClasses.containsKey(action))
            return dialogueActionClasses.get(action);
        return null;
    }

    public static boolean registerCustomAction(ResourceLocation name, Class<? extends DialogueAction> action) {
        if(dialogueActionClasses.containsKey(name)) return false;
        var put = dialogueActionClasses.put(name, action);
        return put != null;
    }

    public static <T extends DialogueAction> boolean registerOverload(String overloadingNamespace, ResourceLocation actionName, Class<? extends T> overloadClass) {
        var overloadList = dialogueActionOverloads.computeIfAbsent(overloadingNamespace, key -> new HashMap<>());
        var put = overloadList.put(actionName, new ActionOverload<T>(overloadClass));
        return put != null;
    }

    public record ActionOverload<T extends DialogueAction>(Class<? extends T> overloadClass) { }

    public record DialogueActionType<T extends DialogueAction>(ResourceLocation id, StreamCodec<RegistryFriendlyByteBuf, T> codec) { }
}
