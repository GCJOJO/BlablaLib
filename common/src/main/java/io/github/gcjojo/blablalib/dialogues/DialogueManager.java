package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.actions.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DialogueManager {
    private static Map<String, Class<? extends DialogueAction>> dialogueActionClasses = new HashMap<>();

    public static void registerDefaultActions(){
        dialogueActionClasses.put("clear",DialogueClear .class);
        dialogueActionClasses.put("wait",DialogueWait .class);
        dialogueActionClasses.put("choice",DialogueChoice .class);
        dialogueActionClasses.put("change_set",DialogueNext .class);
        dialogueActionClasses.put("fade",DialogueFading .class);
        dialogueActionClasses.put("message",DialogueMessage .class);
        dialogueActionClasses.put("image",DialogueImage .class);
        dialogueActionClasses.put("credit",DialogueCredit .class);
        dialogueActionClasses.put("image_move",DialogueMoveImage .class);
        dialogueActionClasses.put("command",DialogueExecuteCommand .class);
        dialogueActionClasses.put("sound",DialogueSound .class);
    }

    public static boolean registerCustomAction(String name, Class<? extends DialogueAction> action) {
        if(dialogueActionClasses.containsKey(name)) return false;
        var put = dialogueActionClasses.put(name, action);
        return put != null;
    }

    public static List<DialogueAction> loadDialogue(String dialoguePath) {
        try {
            String namespace = BlablaLib.MOD_ID;
            String setName = dialoguePath;
            if(dialoguePath.contains(":")) {
                namespace = dialoguePath.split(":")[0];
                setName = dialoguePath.split(":")[1];
            }

            ResourceLocation res = ResourceLocation.tryBuild(namespace, "dialogues.json");
            var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resourceOpt.isEmpty()) return null;

            JsonObject root = new Gson().fromJson(new InputStreamReader(resourceOpt.get().open()), JsonObject.class);

            if (!root.has(setName)) return null;
            List<DialogueAction> actions = new ArrayList<>();

            root.getAsJsonArray(setName).forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                if(!obj.has("action"))
                    return;

                String action = obj.get("action").getAsString();
                if(dialogueActionClasses.containsKey(action)) {
                    try {
                        Constructor<DialogueAction> constructor = (Constructor<DialogueAction>) dialogueActionClasses.get(action).getConstructor(JsonObject.class);
                        actions.add(constructor.newInstance(obj));
                        return;
                    } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                             InvocationTargetException e) {
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

    public static List<DialogueSpeaker> loadSpeakers(String setPath) {
        try {
            String namespace = BlablaLib.MOD_ID;
            if (setPath.contains(":"))
                namespace = setPath.split(":")[0];

            ResourceLocation res = ResourceLocation.tryBuild(namespace, "dialogues.json");
            var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resourceOpt.isEmpty()) return null;

            JsonObject root = new Gson().fromJson(new InputStreamReader(resourceOpt.get().open()), JsonObject.class);
            List<DialogueSpeaker> speakers = new ArrayList<>();
            if (!root.has("speakers")) return speakers;

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
}
