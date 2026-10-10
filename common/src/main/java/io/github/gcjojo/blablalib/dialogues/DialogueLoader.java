package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.actions.DialogueInvalidAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DialogueLoader extends SimpleJsonResourceReloadListener {
    public static final Map<ResourceLocation, Dialogue> dialogues = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();

    public DialogueLoader() {
        super(GSON, "dialogues");
    }

    public static Map<ResourceLocation, Dialogue> loadDialogueFile(ResourceLocation filePath, JsonObject json) {
        Map<ResourceLocation, Dialogue> dialogues = new LinkedHashMap<>();
        String namespace = filePath.getNamespace();

        json.keySet().forEach(dialogueName -> {
            ResourceLocation dialogueId = ResourceLocation.fromNamespaceAndPath(namespace, dialogueName);

            try{
                JsonObject dialogueJson = (JsonObject) json.get(dialogueName);
                loadDialogue(dialogueId, dialogueJson).ifPresent(dialogue -> dialogues.put(dialogueId, dialogue));
            }
            catch (Exception e){
                BlablaLib.getLogger().warn("Error when trying to load dialogue {}\n{}", dialogueId, e.toString());
            }
        });

        return dialogues;
    }

    public static Optional<Dialogue> loadDialogue(ResourceLocation dialogueId, JsonObject json){
        try {
            String dialogueName = dialogueId.getPath();
            String dialogueNamespace = dialogueId.getNamespace();
            if (json == null || !json.has(dialogueName)) return Optional.empty();
            List<DialogueAction> actions = new ArrayList<>();

            json.getAsJsonArray(dialogueName).forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                if(!obj.has("action"))
                    return;
                ResourceLocation action = ResourceLocation.tryParse(obj.get("action").getAsString());
                if(DialogueManager.getDialogueActionClasses().containsKey(action)) {

                    Class<? extends DialogueAction> actionClass = DialogueManager.getDialogueActionClasses().get(action);
                    if(DialogueManager.getDialogueActionOverloads().containsKey(dialogueNamespace) && DialogueManager.getDialogueActionOverloads().get(dialogueNamespace).containsKey(action))
                        actionClass = DialogueManager.getDialogueActionOverloads().get(dialogueNamespace).get(action).overloadClass();
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
            return Optional.of(new Dialogue(List.copyOf(actions)));

        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
            return Optional.empty();
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        dialogues.clear();
        Map<ResourceLocation, Dialogue> newDialogues = new LinkedHashMap<>();

        files.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    try {
                        ResourceLocation filePath = entry.getKey();
                        JsonObject json = (JsonObject) entry.getValue();
                        newDialogues.putAll(loadDialogueFile(filePath, json));
                    } catch (Exception e) {
                        BlablaLib.getLogger().error("Cannot load quest file {}\n{}", entry.getKey().toString(), e.toString());
                    }
                });

       dialogues.putAll(newDialogues);
    }
}
