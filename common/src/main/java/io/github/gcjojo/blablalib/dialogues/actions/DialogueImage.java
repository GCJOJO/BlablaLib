package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class DialogueImage extends DialogueAction {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueImage> IMAGE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueImage::getImageId,
            ResourceLocation.STREAM_CODEC, DialogueImage::getImage,
            ByteBufCodecs.INT, DialogueImage::getInitialWidth,
            ByteBufCodecs.INT, DialogueImage::getInitialHeight,
            ImageTransforms.STREAM_CODEC, DialogueImage::getTransforms,
            DialogueImage::new
    );
    private static final DialogueManager.DialogueActionType<DialogueImage> TYPE = DialogueManager.registerActionType(
          ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_image"),
          IMAGE_STREAM_CODEC
    );
    @Getter
    @Setter
    ImageTransforms transforms = ImageTransforms.DEFAULT;
    @Getter
    private int imageId;
    @Getter
    private ResourceLocation image;
    @Getter
    private int initialWidth;
    @Getter
    private int initialHeight;
    @Setter
    @Getter
    private int alpha = 255;

    public DialogueImage(int imageId, String imagePath, int initialWidth, int initialHeight, ImageTransforms transforms) {
        this.imageId = imageId;
        this.image = ResourceLocation.tryParse(imagePath);
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        this.transforms = transforms;
    }

    public DialogueImage(int imageId, ResourceLocation imagePath, int initialWidth, int initialHeight, ImageTransforms transforms) {
        this.imageId = imageId;
        this.image = imagePath;
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        this.transforms = transforms;
    }

    public DialogueImage(JsonObject object){
        if(object.has("id"))
            this.imageId = object.get("id").getAsInt();
        if(object.has("image"))
            this.image = ResourceLocation.tryParse(object.get("image").getAsString());
        if(object.has("width"))
            this.initialWidth = object.get("width").getAsInt();
        if(object.has("height"))
            this.initialHeight = object.get("height").getAsInt();
        if(object.has("x"))
            transforms = transforms.withX(object.get("x").getAsFloat());
        if(object.has("y"))
            transforms = transforms.withY(object.get("y").getAsFloat());
        if(object.has("rotation"))
            transforms = transforms.withRotation(object.get("rotation").getAsFloat());
        if(object.has("scale"))
            transforms = transforms.withScale(object.get("scale").getAsFloat());
        if(object.has("alpha"))
            this.alpha = object.get("alpha").getAsInt();
    }

    public float getX() { return transforms.position().x; }

    public void setX(float newX) { transforms = transforms.withX(newX); }

    public float getY() { return transforms.position().y; }

    public void setY(float newY) { transforms = transforms.withY(newY); }

    public float getRotation() { return transforms.rotation(); }

    public void setRotation(float newRot) { transforms = transforms.withRotation(newRot); }

    public float getScale() { return transforms.scale(); }

    public void setScale(float newScale) { transforms = transforms.withScale(newScale); }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(alpha <= 8)
            return;

        if(this.alpha >= 255) this.alpha = 255;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        //pose.translate(screen.width / 2f,screen.height / 2f, 0);
        float xPos = screen.width * transforms.position().x * 0.01f;
        float yPos = screen.height * transforms.position().y * 0.01f;
        pose.translate(xPos, yPos, 1);

        double imageHeight = ((double) screen.height * 0.5d) / initialHeight;
        double imageWidth = (double)initialWidth * (imageHeight / (double)initialHeight);

        //image, topPos, leftPos, uvX, uvY, width, height
        pose.scale((float) imageHeight * transforms.scale(), (float) imageWidth * transforms.scale(), 1);

        pose.rotateAround(Axis.ZP.rotationDegrees(transforms.rotation()), (int)(imageWidth * 0.5f), (int)(imageHeight * 0.5f), 0.0f);

        // Merci copislop AKA copiflop >:(
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, (float) this.alpha / 255);
        graphics.blit(image, -initialWidth / 2, -initialHeight / 2, 0, 0, initialWidth, initialHeight, initialWidth, initialHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

        pose.popPose();
    }

    @Override
    public void step() {

    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isSkippable() { return true; }

    public record ImageTransforms(Vec2 position, float rotation, float scale) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ImageTransforms> STREAM_CODEC = StreamCodec.composite(
                LibLibNetwork.VEC2_STREAM_CODEC, ImageTransforms::position,
                ByteBufCodecs.FLOAT, ImageTransforms::rotation,
                ByteBufCodecs.FLOAT, ImageTransforms::scale,
                ImageTransforms::new
        );
        static final ImageTransforms DEFAULT = new ImageTransforms(Vec2.ZERO, 0f, 1f);

        public ImageTransforms withX(float newX) { return new ImageTransforms(new Vec2(newX, position().y), rotation(), scale()); }

        public ImageTransforms withY(float newY) { return new ImageTransforms(new Vec2(position().x, newY), rotation(), scale()); }

        public ImageTransforms withPosition(Vec2 newPos) { return new ImageTransforms(newPos, rotation(), scale()); }

        public ImageTransforms withRotation(float newRotation) { return new ImageTransforms(position(), newRotation, scale()); }

        public ImageTransforms withScale(float newScale) { return new ImageTransforms(position(), rotation(), newScale); }
    }

}
