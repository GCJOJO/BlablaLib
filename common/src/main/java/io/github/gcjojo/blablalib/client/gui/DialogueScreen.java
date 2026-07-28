package io.github.gcjojo.blablalib.client.gui;

import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.dialogues.DialogueSpeaker;
import io.github.gcjojo.blablalib.dialogues.actions.DialogueRawAction;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.github.gcjojo.liblib.client.gui.GuiScreen;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends GuiScreen {

    private static final float endFade = 7.5f;
    private final List<DialogueAction> currentActions = new ArrayList<>();
    private final List<Button> buttons = new ArrayList<>();
    private List<DialogueAction> dialogueActions;
    private int actionIndex = -1;
    private List<DialogueSpeaker> dialogueSpeakers;
    private ResourceLocation currentDialogue;
    private boolean advanceDialogueAtTickEnd = false;
    private ResourceLocation queuedNextDialogue = null;
    private float currentFadingTime = -1.0f;
    private boolean guiVisible = true;

    /**
     *  The number of ticks to wait before a new character is drawn
     */
    @Setter
    @Getter
    private int dialogueSpeed = 1;
    @Setter
    @Getter
    private int waitMultiplier = 8;

    public DialogueScreen(ResourceLocation dialoguePath) {
        super(Component.literal("Dialogue"));
        this.currentDialogue = dialoguePath;
        var actions = DialogueManager.loadDialogue(dialoguePath);
        if(actions == null || actions.isEmpty())
        {
            this.onClose();
            return;
        }
        this.dialogueActions = actions;

        var speakers = DialogueManager.loadSpeakers(dialoguePath);
        if(speakers == null || speakers.isEmpty())
        {
            this.onClose();
            return;
        }
        this.dialogueSpeakers = speakers;
    }

    public DialogueScreen(ResourceLocation dialoguePath, List<DialogueAction> actions, List<DialogueSpeaker> speakers) {
        super(Component.literal("Dialogue"));
        this.currentDialogue = dialoguePath;
        this.dialogueActions = actions;
        this.dialogueSpeakers = speakers;
        if(!actions.isEmpty())
            NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_SCREEN_OPENED_PACKET_ID, new FriendlyByteBuf(Unpooled.buffer()));
    }

    public static void openDialogueScreen(ResourceLocation dialoguePath) {
        List<DialogueAction> actions = DialogueManager.loadDialogue(dialoguePath);
        List<DialogueSpeaker> speakers = DialogueManager.loadSpeakers(dialoguePath);
        if(actions != null && !actions.isEmpty() && speakers != null)
            Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new DialogueScreen(dialoguePath, actions, speakers)));
    }

    @Override
    protected void init() {
        super.init();
        if(actionIndex == -1)
            advanceDialogue();

        buttons.forEach(this::addRenderableWidget);
    }

    public void drawButton(Button button)
    {
        this.addRenderableWidget(button);
        buttons.add(button);
    }

    public void handleChoiceSelection(ResourceLocation nextSet, ResourceLocation saveSet, String action) {
        CompoundTag choiceNbt = new CompoundTag();
        choiceNbt.putString("NextSet", nextSet.toString());
        choiceNbt.putString("SaveSet", saveSet.toString());
        choiceNbt.putString("Action", action);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeNbt(choiceNbt);
        NetworkManager.sendToServer(BlablaLibNetwork.CHOICE_PACKET_ID, buf);
        queueAdvanceDialogue();
    }

    public void changeToDialogue(ResourceLocation dialoguePath) {
        List<DialogueAction> newActions = DialogueManager.loadDialogue(dialoguePath);
        if(newActions == null) {
            this.onClose();
            return;
        }
        List<DialogueSpeaker> newSpeakers = DialogueManager.loadSpeakers(dialoguePath);
        if(newSpeakers == null) {
            this.onClose();
            return;
        }

        currentDialogue = dialoguePath;
        actionIndex = -1;
        dialogueActions = newActions;
        dialogueSpeakers = newSpeakers;
        advanceDialogue();
    }

    @Override
    public void tick() {
        if(advanceDialogueAtTickEnd) {
            this.advanceDialogueAtTickEnd = false;
            this.advanceDialogue();
        }

        if(queuedNextDialogue != null){
            changeToDialogue(queuedNextDialogue);
            queuedNextDialogue = null;
        }

        if(actionIndex >= dialogueActions.size())
            return;

        currentActions.forEach(DialogueAction::step);

    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(this.currentFadingTime >= 0.0){
            if(this.currentFadingTime >= endFade || !this.guiVisible){
                this.onClose();
                return;
            }

            float fadePercentage = currentFadingTime / endFade;

            int topColor = Mth.lerpInt(fadePercentage, 0x11, 0x00);
            int bottomColor = Mth.lerpInt(fadePercentage, 0xDD, 0x00);
            int topPoint = Mth.lerpInt(fadePercentage, 0, (int)(this.height * 0.5));

            graphics.fillGradient(0, topPoint, this.width, this.height,
                    FastColor.ARGB32.color(topColor, 0, 0, 0), FastColor.ARGB32.color(bottomColor, 0, 0, 0));
            currentFadingTime += partialTick;
            return;
        }

        if(actionIndex >= dialogueActions.size())
            return;
        if(guiVisible)
            graphics.fillGradient(0, 0, this.width, this.height, 0x11000000, 0xDD000000);

        currentActions.forEach(action -> action.draw(graphics, mouseX, mouseY, partialTick));
        super.render(graphics, mouseX, mouseY, partialTick);
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

    public void queueChangeSet(ResourceLocation nextDialogue) { this.queuedNextDialogue = nextDialogue; }

    public void advanceDialogue() {
        clearElements();
        actionIndex++;
        buttons.forEach(button -> button.visible = false);
        buttons.clear();
        if (dialogueActions.isEmpty() || actionIndex >= dialogueActions.size()) {
            endDialogue();
            return;
        }

        currentActions.removeIf(DialogueAction::isBlocking);

        for(int i = actionIndex; i <= dialogueActions.size() - 1; i++)
        {
            DialogueAction currentAction = dialogueActions.get(i);

            if(currentAction instanceof DialogueRawAction){
                switch(((DialogueRawAction) currentAction).getRawAction()){
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

    public List<DialogueSpeaker> getDialogueSpeakers() { return this.dialogueSpeakers; }

    public DialogueSpeaker getDialogueSpeaker(int id){
        if (dialogueSpeakers.isEmpty())
            return DialogueSpeaker.DEFAULT_SPEAKER;
        for(var speaker : this.dialogueSpeakers)
            if(speaker.getId() == id) return speaker;
        return DialogueSpeaker.DEFAULT_SPEAKER;
    }

    public void clearActions() { currentActions.clear(); }

    public void clearActions(String clearedClass){
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

    public void endDialogue(){
        clearActions();
        currentFadingTime = 0.0f;

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(currentDialogue.toString());
        NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_COMPLETED_PACKET_ID, buf);
    }

    public List<DialogueAction> getCurrentActions() {
        return this.currentActions;
    }
}