package io.github.gcjojo.blablalib.dialogues;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class Dialogue {
    @Getter
    protected final ResourceLocation dialogueId;
    @Getter
    protected final List<DialogueAction> dialogueActions;
    @Getter
    protected final List<ResourceLocation> dialogueSpeakersId;

    public Dialogue(ResourceLocation id, List<DialogueAction> actions, List<ResourceLocation> speakersId) {
        dialogueId = id;
        dialogueActions = actions;
        dialogueSpeakersId = speakersId;
    }
}
