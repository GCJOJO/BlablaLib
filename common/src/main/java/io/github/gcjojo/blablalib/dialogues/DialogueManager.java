package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.actions.*;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DialogueManager {
    public record ActionOverload<T extends DialogueAction>(Class<? extends T> overloadClass){ }

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

    public static List<DialogueAction> loadDialogue(ResourceLocation dialoguePath) {
        try {
            String dialogueName = dialoguePath.getPath();
            String dialogueNamespace = dialoguePath.getNamespace();
            JsonObject root = getDialogueFile(dialoguePath.getNamespace());
            if (root == null || !root.has(dialogueName)) return null;
            List<DialogueAction> actions = new ArrayList<>();

            root.getAsJsonArray(dialogueName).forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                if(!obj.has("action"))
                    return;
                ResourceLocation action = ResourceLocation.tryParse(obj.get("action").getAsString());
                if(dialogueActionClasses.containsKey(action)) {

                    Class<? extends DialogueAction> actionClass = dialogueActionClasses.get(action);
                    if(dialogueActionOverloads.containsKey(dialogueNamespace) && dialogueActionOverloads.get(dialogueNamespace).containsKey(action))
                        actionClass = dialogueActionOverloads.get(dialogueNamespace).get(action).overloadClass();

                    try {
                        try {
                            Constructor<? extends DialogueAction> constructor = actionClass.getConstructor(JsonObject.class);
                            actions.add(constructor.newInstance(obj));
                            return;
                        } catch (NoSuchMethodException e) {
                            Constructor<? extends DialogueAction> constructor = actionClass.getConstructor();
                            actions.add(constructor.newInstance());
                            return;
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        BlablaLib.getLogger().error(e.getMessage());
                        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
                    }
                }
                actions.add(new DialogueInvalidAction(obj));
            });
            return actions;

        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
            return null;
        }
    }

    public static List<DialogueSpeaker> loadSpeakers(ResourceLocation dialoguePath) {
        try {
            List<DialogueSpeaker> speakers = new ArrayList<>();
            JsonObject root = getDialogueFile(dialoguePath.getNamespace());
            if (root == null || !root.has("speakers")) return speakers;

            root.getAsJsonArray("speakers").forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                speakers.add(new DialogueSpeaker(obj));
            });

            return speakers;

        } catch (Exception e) {
            BlablaLib.getLogger().warn("Oopsie cannot load speakers !");
            return null;
        }
    }

    public static JsonObject getDialogueFile(String modNamespace){
        try {
            ResourceLocation res = ResourceLocation.tryBuild(modNamespace, "dialogues.json");
            var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resourceOpt.isEmpty()) return null;

            return new Gson().fromJson(new InputStreamReader(resourceOpt.get().open()), JsonObject.class);
        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
        }
        return null;
    }

    public static List<ResourceLocation> getDialogueList() {
        List<ResourceLocation> dialoguePaths = new ArrayList<>();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        List<String> namespaces = new ArrayList<>(Platform.getModIds());
        namespaces.addAll(resourceManager.getNamespaces());

        namespaces.forEach(packId -> {
            BlablaLib.getLogger().info("Selected pack {}", packId);
            JsonObject root = DialogueManager.getDialogueFile(packId);

            if(root == null || !root.isJsonObject()) return;
            root.getAsJsonObject().asMap().forEach((elementName, jsonElement) -> {
                if(elementName.equals("speakers") || !jsonElement.isJsonArray()) return;

                ResourceLocation resourceLocation = ResourceLocation.tryBuild(packId, elementName);
                if(resourceLocation != null)
                    dialoguePaths.add(resourceLocation);
            });
        });

        return dialoguePaths;
    }
}
