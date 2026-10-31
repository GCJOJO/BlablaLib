package io.github.gcjojo.blablalib.npc;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

@Getter
public enum VariableType implements StringRepresentable {
    NONE("unknown", Codec.EMPTY.codec(), StreamCodec.unit(Unit.INSTANCE)),
    BOOL("bool", Codec.BOOL, ByteBufCodecs.BOOL.cast()),
    NUMBER("number", Codec.DOUBLE, ByteBufCodecs.DOUBLE.cast()),
    STRING("string", Codec.STRING, ByteBufCodecs.STRING_UTF8.cast()),
    RESOURCE_LOCATION("resource_location", ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC.cast());

    private final String serializedName;
    private final Codec<?> valueCodec;
    private final StreamCodec<RegistryFriendlyByteBuf, Object> valueStreamCodec;

    @SuppressWarnings("unchecked")
    <T> VariableType(String serializedName, Codec<?> valueCodec, StreamCodec<RegistryFriendlyByteBuf, T> valueStreamCodec) {
        this.serializedName = serializedName;
        this.valueCodec = valueCodec;
        this.valueStreamCodec = (StreamCodec<RegistryFriendlyByteBuf, Object>) valueStreamCodec;
    }

    public static final Codec<VariableType> CODEC = StringRepresentable.fromEnum(VariableType::values);

    public static final StreamCodec<RegistryFriendlyByteBuf, VariableType> STREAM_CODEC =
            ByteBufCodecs.idMapper(id -> values()[id], VariableType::ordinal).cast();

    @Override
    public @NotNull String getSerializedName() {
        return this.serializedName;
    }
}
