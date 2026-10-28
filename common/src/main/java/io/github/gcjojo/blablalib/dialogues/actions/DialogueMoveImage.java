package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Optional;

public class DialogueMoveImage extends DialogueAction {

    private int imageId;
    private float animTime;
    private Optional<Float> xPercentageStart = Optional.empty();
    private Optional<Float> yPercentageStart = Optional.empty();
    private Optional<Float> xPercentageEnd = Optional.empty();
    private Optional<Float> yPercentageEnd = Optional.empty();
    private Optional<Float> scaleStart = Optional.empty();
    private Optional<Float> scaleEnd = Optional.empty();
    private Optional<Integer> alphaStart = Optional.empty();
    private Optional<Integer> alphaEnd = Optional.empty();

    // TODO
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueMoveImage> MOVE_IMAGE_STREAM_CODEC = StreamCodec.unit(new DialogueMoveImage());

    private static final DialogueManager.DialogueActionType<DialogueMoveImage> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_move_image"),
            MOVE_IMAGE_STREAM_CODEC
    );

    private boolean animateX = false;
    private boolean animateY = false;
    private boolean animateScale = false;
    private boolean animateAlpha = false;

    private float currentTime;

    public DialogueMoveImage() {
        this.imageId = -1;
        this.animTime = 0;
        this.currentTime = 1;
    }

    public DialogueMoveImage(JsonObject object){
        if(object.has("id"))
            this.imageId = object.get("id").getAsInt();
        if(object.has("anim_time"))
            this.animTime = object.get("anim_time").getAsFloat();
        if(object.has("x_start"))
            this.xPercentageStart = Optional.of(object.get("x_start").getAsFloat());
        if(object.has("y_start"))
            this.yPercentageStart = Optional.of(object.get("y_start").getAsFloat());
        if(object.has("x_end"))
            this.xPercentageEnd = Optional.of(object.get("x_end").getAsFloat());
        if(object.has("y_end"))
            this.yPercentageEnd = Optional.of(object.get("y_end").getAsFloat());
        if(object.has("scale_start"))
            this.scaleStart = Optional.of(object.get("scale_start").getAsFloat());
        if(object.has("scale_end"))
            this.scaleEnd = Optional.of(object.get("scale_end").getAsFloat());
        if(object.has("alpha_start"))
            this.alphaStart = Optional.of(object.get("alpha_start").getAsInt());
        if(object.has("alpha_end"))
            this.alphaEnd = Optional.of(object.get("alpha_end").getAsInt());

        this.animateX = this.xPercentageStart.isPresent() && this.xPercentageEnd.isPresent();
        this.animateY = this.yPercentageStart.isPresent() && this.yPercentageEnd.isPresent();
        this.animateScale = this.scaleStart.isPresent() && this.scaleEnd.isPresent();
        this.animateAlpha = this.alphaStart.isPresent() && this.alphaEnd.isPresent();
    }

    @Override
    public void step() { }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(currentTime >= animTime)
            return;

        Optional<DialogueAction> imageAction = screen.getCurrentActions().stream()
                .filter(action -> action instanceof DialogueImage && ((DialogueImage)action)
                        .getImageId() == this.imageId).findFirst();

        if(imageAction.isEmpty() || !(imageAction.get() instanceof DialogueImage image))
            return;

        float animPercentage = currentTime / animTime;

        float xPercentage = animateX ? Mth.lerp(animPercentage, xPercentageStart.get(), xPercentageEnd.get()) : image.getX();
        float yPercentage = animateY ? Mth.lerp(animPercentage, yPercentageStart.get(), yPercentageEnd.get()) : image.getY();
        float scale = animateScale ? Mth.lerp(animPercentage, scaleStart.get(), scaleEnd.get()) : image.getScale();
        int alpha = animateAlpha ? (int) Mth.lerp(animPercentage, (float) alphaStart.get(), (float) alphaEnd.get()) : image.getAlpha();

        image.setX(xPercentage);
        image.setY(yPercentage);
        image.setScale(scale);
        image.setAlpha(alpha);

        currentTime += partialTick;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) { }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) { }

    @Override
    public boolean isBlocking() { return false; }

    @Override
    public boolean isSkippable() { return true; }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
