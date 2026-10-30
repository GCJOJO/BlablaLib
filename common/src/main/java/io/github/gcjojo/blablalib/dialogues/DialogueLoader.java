package io.github.gcjojo.blablalib.dialogues;

import com.google.gson.*;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.actions.DialogueUnknownAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DialogueLoader extends SimpleJsonResourceReloadListener {
    public static final String SPEAKERS_KEY = "speakers";

    public static final Map<ResourceLocation, Dialogue> dialogues = new HashMap<>();
    public static final List<DialogueSpeaker> speakers = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().create();

    public DialogueLoader() {
        super(GSON, "dialogues");
    }

    public static Map<ResourceLocation, Dialogue> loadDialogueFile(ResourceLocation filePath, JsonObject json) {
        Map<ResourceLocation, Dialogue> dialogues = new LinkedHashMap<>();
        String namespace = filePath.getNamespace();

        loadSpeakers(json);

        json.keySet().forEach(dialogueName -> {
            ResourceLocation dialogueId = ResourceLocation.fromNamespaceAndPath(namespace, dialogueName);
            try{
                JsonElement dialogueJson = json.get(dialogueName);
                loadDialogue(dialogueId, dialogueJson.getAsJsonArray()).ifPresent(dialogue -> dialogues.put(dialogueId, dialogue));
            }
            catch (Exception e){
                BlablaLib.getLogger().warn("Error when trying to load dialogue {}\n{}", dialogueId, e.toString());
            }
        });

        return dialogues;
    }

    public static Optional<Dialogue> loadDialogue(ResourceLocation dialogueId, JsonArray jsonArray){
        try {
            String dialogueName = dialogueId.getPath();
            String dialogueNamespace = dialogueId.getNamespace();
            List<DialogueAction> actions = new ArrayList<>();
            List<ResourceLocation> referencedSpeakers = new ArrayList<>();

            jsonArray.forEach(element -> {
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
                            DialogueAction actionInstance = constructor.newInstance(obj);
                            actions.add(actionInstance);
                            actionInstance.getReferencedSpeaker().ifPresent(referencedSpeakers::add);
                            return;
                        } catch (NoSuchMethodException e) {
                            Constructor<? extends DialogueAction> constructor = actionClass.getConstructor();
                            DialogueAction actionInstance = constructor.newInstance();
                            actions.add(actionInstance);
                            actionInstance.getReferencedSpeaker().ifPresent(referencedSpeakers::add);
                            return;
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        BlablaLib.getLogger().error(e.getMessage());
                        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
                    }
                }
                actions.add(new DialogueUnknownAction(obj));
            });
            return Optional.of(new Dialogue(dialogueId, List.copyOf(actions), referencedSpeakers));

        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
            return Optional.empty();
        }
    }

    public static void loadSpeakers(JsonObject json) {
        json.getAsJsonArray(SPEAKERS_KEY).forEach(element -> {
            JsonObject obj = element.getAsJsonObject();
            DialogueSpeaker speaker = new DialogueSpeaker(obj);
            if(speaker.isValid())
                speakers.add(speaker);
        });
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
                        BlablaLib.getLogger().error("Cannot load dialogue file {}\n{}", entry.getKey().toString(), e.toString());
                    }
                });

       dialogues.putAll(newDialogues);
    }


}
