package io.github.gcjojo.blablalib.npc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record NPCVisualState(
        @SerializedName("model") String modelLocation,
        @SerializedName("texture") String textureLocation,
        @SerializedName("animation") String animation
) {
    public static Codec<NPCVisualState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("model").forGetter(NPCVisualState::modelLocation),
                    Codec.STRING.fieldOf("texture").forGetter(NPCVisualState::textureLocation),
                    Codec.STRING.fieldOf("animation").forGetter(NPCVisualState::animation))
                    .apply(instance, NPCVisualState::new));

    private static final Gson GSON = new GsonBuilder().create();
    public static final NPCVisualState EMPTY = new NPCVisualState("", "", "");
    public static final NPCVisualState DEFAULT = new NPCVisualState("blablalib:npc", "blablalib:npc/default.png", "");

    public static NPCVisualState fromCompoundTag(CompoundTag nbt){
        String model = "";
        String texture = "";
        String animation = "";

        if(nbt.contains("Model"))
            model = nbt.getString("Model");
        if(nbt.contains("Texture"))
            texture = nbt.getString("Texture");
        if(nbt.contains("Animation"))
            animation = nbt.getString("Animation");

        return new NPCVisualState(model, texture, animation);
    }

    public static NPCVisualState loadDefinition(String input){
        return GSON.fromJson(input, NPCVisualState.class);
    }

    public static NPCVisualState read(FriendlyByteBuf buf) {
        return new NPCVisualState(buf.readUtf(), buf.readUtf(), buf.readUtf());
    }

    public static NPCVisualState copy(NPCVisualState definition){
        return new NPCVisualState(definition.modelLocation, definition.textureLocation, definition.animation);
    }

    public NPCVisualState withModel(String model){
        return new NPCVisualState(model, this.textureLocation, this.animation);
    }

    public NPCVisualState withTexture(String texture){
        return new NPCVisualState(this.modelLocation, texture, this.animation);
    }

    public NPCVisualState withAnimation(String animation){
        return new NPCVisualState(this.modelLocation, this.textureLocation, animation);
    }

    public CompoundTag toCompoundTag(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Model", modelLocation);
        nbt.putString("Texture", textureLocation);
        nbt.putString("Animation", animation);
        return nbt;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modelLocation);
        buf.writeUtf(textureLocation);
        buf.writeUtf(animation);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, NPCVisualState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, NPCVisualState::modelLocation,
            ByteBufCodecs.STRING_UTF8, NPCVisualState::textureLocation,
            ByteBufCodecs.STRING_UTF8, NPCVisualState::animation,
            NPCVisualState::new
    );
}
