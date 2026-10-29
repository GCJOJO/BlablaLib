package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.tween.Easing;
import io.github.gcjojo.liblib.tween.Easings;
import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class DialogueMoveImage extends DialogueAction {

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueMoveImage> MOVE_IMAGE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueMoveImage::getImageId,
            PositionAnimation.STREAM_CODEC, DialogueMoveImage::getPosition,
            RotationAnimation.STREAM_CODEC, DialogueMoveImage::getRotation,
            ScaleAnimation.STREAM_CODEC, DialogueMoveImage::getScale,
            ColorAnimation.STREAM_CODEC, DialogueMoveImage::getColor,
            DialogueMoveImage::new
    );
    private static final DialogueManager.DialogueActionType<DialogueMoveImage> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_move_image"),
            MOVE_IMAGE_STREAM_CODEC
    );
    @Getter
    private int imageId;
    @Getter
    private PositionAnimation position = PositionAnimation.DEFAULT;
    @Getter
    private RotationAnimation rotation = RotationAnimation.DEFAULT;
    @Getter
    private ScaleAnimation scale = ScaleAnimation.DEFAULT;
    @Getter
    private ColorAnimation color = ColorAnimation.DEFAULT;

    private float currentTime;

    public DialogueMoveImage(int newImageId, PositionAnimation positionAnimation, RotationAnimation rotationAnimation, ScaleAnimation scaleAnimation, ColorAnimation colorAnimation) {
        imageId = newImageId;
        position = positionAnimation;
        rotation = rotationAnimation;
        scale = scaleAnimation;
        color = colorAnimation;
    }

    public DialogueMoveImage(JsonObject object){
        if(object.has("id"))
            this.imageId = object.get("id").getAsInt();

        if(object.has("position")) {
            JsonObject positionObject = object.getAsJsonObject("position");
            float time = 0;
            Optional<Float> xStart = Optional.empty();
            Optional<Float> yStart = Optional.empty();
            Optional<Float> xEnd = Optional.empty();
            Optional<Float> yEnd = Optional.empty();
            Easing easing = Easing.LINEAR;

            if(positionObject.has("time"))
                time = positionObject.get("time").getAsFloat();

            if(positionObject.has("x_start"))
                xStart = Optional.of(positionObject.get("x_start").getAsFloat());
            if(positionObject.has("y_start"))
                yStart = Optional.of(positionObject.get("y_start").getAsFloat());
            if(positionObject.has("x_end"))
                xEnd = Optional.of(positionObject.get("x_end").getAsFloat());
            if(positionObject.has("y_end"))
                yEnd = Optional.of(positionObject.get("y_end").getAsFloat());
            if(positionObject.has("easing"))
                easing = Easings.get((positionObject.get("easing").getAsString()));

            position = new PositionAnimation(time, xStart, yStart, xEnd, yEnd, easing);
        }

        if(object.has("rotation")) {
            JsonObject rotationObject = object.getAsJsonObject("rotation");
            float time = 0;
            Optional<Float> angleStart = Optional.empty();
            Optional<Float> angleEnd = Optional.empty();
            Easing easing = Easing.LINEAR;

            if(rotationObject.has("time"))
                time = rotationObject.get("time").getAsFloat();

            if(rotationObject.has("from"))
                angleStart = Optional.of(rotationObject.get("from").getAsFloat());
            if(rotationObject.has("to"))
                angleEnd = Optional.of(rotationObject.get("to").getAsFloat());
            if(rotationObject.has("easing"))
                easing = Easings.get((rotationObject.get("easing").getAsString()));

            rotation = new RotationAnimation(time, angleStart, angleEnd, easing);
        }

        if(object.has("scale")) {
            JsonObject scaleObject = object.getAsJsonObject("scale");
            float time = 0;
            Optional<Float> scaleStart = Optional.empty();
            Optional<Float> scaleEnd = Optional.empty();
            Easing easing = Easing.LINEAR;

            if(scaleObject.has("time"))
                time = scaleObject.get("time").getAsFloat();

            if(scaleObject.has("from"))
                scaleStart = Optional.of(scaleObject.get("from").getAsFloat());
            if(scaleObject.has("to"))
                scaleEnd = Optional.of(scaleObject.get("to").getAsFloat());
            if(scaleObject.has("easing"))
                easing = Easings.get((scaleObject.get("easing").getAsString()));

            scale = new ScaleAnimation(time, scaleStart, scaleEnd, easing);
        }

        if(object.has("color")) {
            JsonObject colorObject = object.getAsJsonObject("color");
            float time = 0;
            Optional<Color> colorStart = Optional.empty();
            Optional<Color> colorEnd = Optional.empty();
            Easing easing = Easing.LINEAR;

            if(colorObject.has("time"))
                time = colorObject.get("time").getAsFloat();

            if(colorObject.has("from"))
                colorStart = Optional.ofNullable(Color.fromString(colorObject.get("from").getAsString()));
            if(colorObject.has("to"))
                colorEnd = Optional.ofNullable(Color.fromString(colorObject.get("to").getAsString()));
            if(colorObject.has("easing"))
                easing = Easings.get((colorObject.get("easing").getAsString()));

            color = new ColorAnimation(time, colorStart, colorEnd, easing);
        }
    }

    public DialogueMoveImage() {
        this.imageId = -1;
        this.currentTime = 1;
    }

    public float maxTime() {
        return Math.max(position.time(), Math.max(rotation.time(), Math.max(scale.time(), color.time())));
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(currentTime >= maxTime())
            return;

        Optional<DialogueAction> imageAction = screen.getCurrentActions().stream()
                .filter(action -> action instanceof DialogueImage && ((DialogueImage)action)
                        .getImageId() == this.imageId).findFirst();

        if(imageAction.isEmpty() || !(imageAction.get() instanceof DialogueImage image))
            return;

        float newX = position.lerpX(currentTime, image.getX());
        float newY = position.lerpY(currentTime, image.getY());
        float newRot = rotation.lerpRotation(currentTime, image.getRotation());
        float newScale = scale.lerpScale(currentTime, image.getScale());
        Color newColor = color.lerpColor(currentTime, Color.WHITE);

        image.setX(newX);
        image.setY(newY);
        image.setRotation(newRot);
        image.setScale(newScale);
        // TODO Change this to a full setColor()
        image.setAlpha(newColor.alpha);

        currentTime += partialTick;
    }

    @Override
    public void step() { }

    public record PositionAnimation(float time, Optional<Float> xStart, Optional<Float> yStart, Optional<Float> xEnd, Optional<Float> yEnd, Easing easing) {
        public static final StreamCodec<RegistryFriendlyByteBuf, PositionAnimation> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, PositionAnimation::time,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), PositionAnimation::xStart,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), PositionAnimation::yStart,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), PositionAnimation::xEnd,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), PositionAnimation::yEnd,
                LibLibNetwork.EasingStreamCodec.CODEC, PositionAnimation::easing,
                PositionAnimation::new
        );

        public static PositionAnimation DEFAULT = new PositionAnimation(0f, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Easing.LINEAR);

        public boolean animatesX() {
            return xStart.isPresent() && xEnd.isPresent();
        }

        public boolean animatesY() {
            return yStart.isPresent() && yEnd.isPresent();
        }

        public float lerpX(float currentTime, float imageX) {
            if(!animatesX()) return imageX;
            float progress = easing.apply(currentTime / time);
            return MathUtils.lerp(xStart.get(), xEnd.get(), progress);
        }

        public float lerpY(float currentTime, float imageY) {
            if(!animatesY()) return imageY;
            float progress = easing.apply(currentTime / time);
            return MathUtils.lerp(yStart.get(), yEnd.get(), progress);
        }
    }

    public record RotationAnimation(float time, Optional<Float> angleStart, Optional<Float> angleEnd, Easing easing) {
        public static final StreamCodec<RegistryFriendlyByteBuf, RotationAnimation> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, RotationAnimation::time,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), RotationAnimation::angleStart,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), RotationAnimation::angleEnd,
                LibLibNetwork.EasingStreamCodec.CODEC, RotationAnimation::easing,
                RotationAnimation::new
        );

        public static RotationAnimation DEFAULT = new RotationAnimation(0f, Optional.empty(), Optional.empty(), Easing.LINEAR);

        public boolean animatesRotation() { return angleStart.isPresent() && angleEnd.isPresent(); }

        public float lerpRotation(float currentTime, float imageRotation) {
            if(!animatesRotation()) return imageRotation;
            float progress = easing.apply(currentTime / time);
            return MathUtils.lerp(angleStart.get(), angleEnd.get(), progress);
        }
    }

    public record ScaleAnimation(float time, Optional<Float> scaleStart, Optional<Float> scaleEnd, Easing easing) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ScaleAnimation> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, ScaleAnimation::time,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ScaleAnimation::scaleStart,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ScaleAnimation::scaleEnd,
                LibLibNetwork.EasingStreamCodec.CODEC, ScaleAnimation::easing,
                ScaleAnimation::new
        );

        public static ScaleAnimation DEFAULT = new ScaleAnimation(0f, Optional.empty(), Optional.empty(), Easing.LINEAR);

        public boolean animatesScale() { return scaleStart.isPresent() && scaleEnd.isPresent(); }

        public float lerpScale(float currentTime, float imageScale) {
            if(!animatesScale()) return imageScale;
            float progress = easing.apply(currentTime / time);
            return MathUtils.lerp(scaleStart.get(), scaleEnd.get(), progress);
        }
    }

    public record ColorAnimation(float time, Optional<Color> colorStart, Optional<Color> colorEnd, Easing easing) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ColorAnimation> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, ColorAnimation::time,
                ByteBufCodecs.optional(LibLibNetwork.COLOR_STREAM_CODEC), ColorAnimation::colorStart,
                ByteBufCodecs.optional(LibLibNetwork.COLOR_STREAM_CODEC), ColorAnimation::colorEnd,
                LibLibNetwork.EasingStreamCodec.CODEC, ColorAnimation::easing,
                ColorAnimation::new
        );

        public static ColorAnimation DEFAULT = new ColorAnimation(0f, Optional.empty(), Optional.empty(), Easing.LINEAR);

        public boolean animatesColor() { return colorStart.isPresent() && colorEnd.isPresent(); }

        public Color lerpColor(float currentTime, Color imageColor) {
            if(!animatesColor()) return imageColor;
            float progress = easing.apply(currentTime / time);
            return MathUtils.lerp(colorStart.get(), colorEnd.get(), progress);
        }
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
