package io.github.gcjojo.blablalib.npc;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

@Getter
public enum VariableType implements StringRepresentable {
    NONE("unknown", Codec.EMPTY.codec()),
    BOOL("bool", Codec.BOOL),
    NUMBER("number", Codec.DOUBLE),
    STRING("string", Codec.STRING),
    RESOURCE_LOCATION("resource_location", ResourceLocation.CODEC);

    private final String serializedName;
    private final Codec<?> valueCodec;

    VariableType(String serializedName, Codec<?> valueCodec) {
        this.serializedName = serializedName;
        this.valueCodec = valueCodec;
    }

    public static final Codec<VariableType> CODEC = StringRepresentable.fromEnum(VariableType::values);

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
}
