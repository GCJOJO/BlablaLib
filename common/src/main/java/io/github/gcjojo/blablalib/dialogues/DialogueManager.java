package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.actions.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DialogueManager {
    private static Map<String, Class<? extends DialogueAction>> dialogueActionClasses = new HashMap<>();

    public static void registerDefaultActions(){
        dialogueActionClasses.put("clear",          DialogueClear.class);
        dialogueActionClasses.put("wait",           DialogueWait.class);
        dialogueActionClasses.put("choice",         DialogueChoice.class);
        dialogueActionClasses.put("change_set",     DialogueNext.class);
        dialogueActionClasses.put("fade",           DialogueFading.class);
        dialogueActionClasses.put("message",        DialogueMessage.class);
        dialogueActionClasses.put("image",          DialogueImage.class);
        dialogueActionClasses.put("credit",         DialogueCredit.class);
        dialogueActionClasses.put("image_move",     DialogueMoveImage.class);
        dialogueActionClasses.put("command",        DialogueExecuteCommand.class);
        dialogueActionClasses.put("sound",          DialogueSound.class);
    }

    public static Class<? extends DialogueAction> getActionClass(String action) {
        if(dialogueActionClasses.containsKey(action))
            return dialogueActionClasses.get(action);
        return null;
    }

    public static boolean registerCustomAction(String name, Class<? extends DialogueAction> action) {
        if(dialogueActionClasses.containsKey(name)) return false;
        var put = dialogueActionClasses.put(name, action);
        return put != null;
    }

    public static List<DialogueAction> loadDialogue(ResourceLocation dialoguePath) {
        try {
            String dialogueName = dialoguePath.getPath();
            JsonObject root = getDialogueFile(dialoguePath.getNamespace());
            if (root == null || !root.has(dialogueName)) return null;
            List<DialogueAction> actions = new ArrayList<>();

            root.getAsJsonArray(dialogueName).forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                if(!obj.has("action"))
                    return;
                String action = obj.get("action").getAsString();
                if(dialogueActionClasses.containsKey(action)) {
                    try {
                        try {
                            Constructor<DialogueAction> constructor = (Constructor<DialogueAction>) dialogueActionClasses.get(action).getConstructor(JsonObject.class);
                            actions.add(constructor.newInstance(obj));
                            return;
                        } catch (NoSuchMethodException e) {
                            Constructor<DialogueAction> constructor = (Constructor<DialogueAction>) dialogueActionClasses.get(action).getConstructor();
                            actions.add(constructor.newInstance(obj));
                            return;
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        BlablaLib.getLogger().error(e.getMessage());
                        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
                    }
                }
                actions.add(new DialogueRawAction(obj));
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
