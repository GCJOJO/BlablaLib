package io.github.gcjojo.blablalib.dialogues;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PlayerDialogueStatus {
    @Getter
    private final UUID playerId;
    @Getter
    private Dialogue currentDialogue = null;
    @Getter
    @Setter
    private int actionIndex = 0;
    @Getter
    @Setter
    private int lastReceivedActionIndex = 0;

    public PlayerDialogueStatus(UUID newPlayerId) {
        playerId = newPlayerId;
    }

    public void setCurrentDialogue(Dialogue newDialogue) {
        currentDialogue = newDialogue;
        this.actionIndex = 0;
    }

    public void advanceDialogue() { advanceDialogue(1); }
    public void advanceDialogue(int amount) { this.actionIndex += amount; }

    public boolean isInDialogue() { return currentDialogue != null; }
}
