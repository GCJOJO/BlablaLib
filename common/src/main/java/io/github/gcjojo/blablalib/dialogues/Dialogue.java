package io.github.gcjojo.blablalib.dialogues;

import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class Dialogue {
    @Getter
    protected final List<DialogueAction> dialogueActions;

    public Dialogue(List<DialogueAction> actions){
        dialogueActions = actions;
    }

    public void play(ServerPlayer player){

    }
}
