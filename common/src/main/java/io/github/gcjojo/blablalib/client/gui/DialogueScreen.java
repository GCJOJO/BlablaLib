package io.github.gcjojo.blablalib.client.gui;

import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.dialogues.DialogueSpeaker;
import io.github.gcjojo.blablalib.dialogues.actions.DialogueUnknownAction;
import io.github.gcjojo.blablalib.network.payloads.DialogueAdvancedPayload;
import io.github.gcjojo.blablalib.network.payloads.DialogueCompletedPayload;
import io.github.gcjojo.liblib.client.gui.GuiScreen;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends GuiScreen {

    private static final float endFade = 7.5f;

    private boolean advanceDialogueAtTickEnd = false;
    private final List<DialogueSpeaker> speakers = new ArrayList<>(List.of(DialogueSpeaker.DEFAULT_SPEAKER));
    private float currentFadingTime = -1.0f;
    private boolean guiVisible = true;
    @Getter
    private final List<DialogueAction> dialogueActions = new ArrayList<>();

    /**
     *  The number of ticks to wait before a new character is drawn
     */
    @Setter
    @Getter
    private int dialogueSpeed = 1;
    @Setter
    @Getter
    private int waitMultiplier = 8;
    @Getter
    private final List<DialogueAction> currentActions = new ArrayList<>();
    private ResourceLocation dialogueId;
    private boolean queueEndDialogue = false;
    private int actionIndex = 0;

    public DialogueScreen(ResourceLocation dialogueId) {
        super(Component.literal("Dialogue"));
        this.dialogueId = dialogueId;
    }

    public void receiveSpeakers(List<DialogueSpeaker> newSpeakers) {
        speakers.addAll(newSpeakers);
    }

    public void receiveActions(List<DialogueAction> newActions) {
        dialogueActions.addAll(newActions);
        refreshActions();
    }

    @Override
    public void tick() {
        if(advanceDialogueAtTickEnd) {
            this.advanceDialogueAtTickEnd = false;
            this.advanceDialogue();
        }

        if(queueEndDialogue) {
            this.queueEndDialogue = false;
            this.endDialogue();
            return;
        }

        if(actionIndex >= dialogueActions.size())
            return;

        currentActions.forEach(DialogueAction::step);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Color gradientTopColor = new Color(0, 0, 0, 0x11);
        Color gradientBottomColor = new Color(0, 0, 0, 0xDD);

        if(this.currentFadingTime >= 0.0){
            if(this.currentFadingTime >= endFade || !this.guiVisible){
                this.onClose();
                return;
            }

            float fadePercentage = currentFadingTime / endFade;

            int topPoint = Mth.lerpInt(fadePercentage, 0, (int)(this.height * 0.5));

            graphics.fillGradient(0, topPoint, this.width, this.height,
                    MathUtils.lerp(gradientTopColor, Color.TRANSPARENT, fadePercentage).getColorInt(),
                    MathUtils.lerp(gradientBottomColor, Color.TRANSPARENT, fadePercentage).getColorInt());

            currentFadingTime += partialTick;
            return;
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        if(actionIndex >= dialogueActions.size())
            return;
        if(guiVisible)
            graphics.fillGradient(0, 0, this.width, this.height, gradientTopColor.getColorInt(), gradientBottomColor.getColorInt());

        currentActions.forEach(action -> action.draw(graphics, mouseX, mouseY, partialTick));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(actionIndex < dialogueActions.size()) {
            currentActions.forEach(dialogueAction -> dialogueAction.mouseClicked(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 256) {
            skipDialogue();
            return true;
        }

        if(actionIndex < dialogueActions.size()) {
            currentActions.forEach(dialogueAction -> dialogueAction.keyPressed(keyCode, scanCode, modifiers));
        }

        return false;
    }

    public void queueAdvanceDialogue() { this.advanceDialogueAtTickEnd = true; }

    public void advanceDialogue() {
        actionIndex++;
        if (dialogueActions.isEmpty() || actionIndex >= dialogueActions.size()) {
            endDialogue();
            return;
        }

        currentActions.removeIf(DialogueAction::isBlocking);

        refreshActions();

        NetworkManager.sendToServer(new DialogueAdvancedPayload(actionIndex));
    }

    public void refreshActions() {
        for(int i = actionIndex; i <= dialogueActions.size() - 1; i++)
        {
            DialogueAction currentAction = dialogueActions.get(i);

            if(currentAction instanceof DialogueUnknownAction){
                switch(((DialogueUnknownAction) currentAction).getRawAction()){
                    case "hide_ui" -> hideGui();
                    case "show_ui" -> showGui();
                }
                continue;
            }
            currentActions.add(currentAction);

            currentAction.setup(this);
            if(currentAction.isBlocking())
            {
                actionIndex = i;
                break;
            }
        }
    }

    public DialogueSpeaker getDialogueSpeaker(ResourceLocation id) {
        for(var speaker : speakers)
            if(speaker.getId() == id) return speaker;
        return DialogueSpeaker.DEFAULT_SPEAKER;
    }

    public void clearActions() { currentActions.clear(); }

    public void clearActions(ResourceLocation clearedClass){
        Class<? extends DialogueAction> classToRemove = DialogueManager.getActionClass(clearedClass);
        if(classToRemove != null)
            currentActions.removeIf(classToRemove::isInstance);
    }

    public void showGui() { this.guiVisible = true; }
    public void hideGui() { this.guiVisible = false; }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public boolean isPauseScreen() { return false; }

    public void skipDialogue(){
        while(actionIndex < dialogueActions.size() && dialogueActions.get(actionIndex).isSkippable())
            advanceDialogue();
    }

    public void queueEndDialogue() { queueEndDialogue = true; }

    public void endDialogue(){
        clearActions();
        currentFadingTime = 0.0f;

        NetworkManager.sendToServer(new DialogueCompletedPayload());
    }
}
