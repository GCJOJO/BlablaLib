package io.github.gcjojo.blablalib.npc.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.npc.VariableType;

// Maybe change Object -> T
public record NPCVariableDefinition<T>(String variableName, VariableType type, T defaultValue) {
    public static <T> MapCodec<NPCVariableDefinition<T>> createCodec(Codec<T> valueCodec) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("name").forGetter(NPCVariableDefinition::variableName),
                        VariableType.CODEC.fieldOf("type").forGetter(NPCVariableDefinition::type),
                        valueCodec.fieldOf("defaultValue").forGetter(NPCVariableDefinition::defaultValue)
                ).apply(instance, NPCVariableDefinition::new));
    }

    public static final Codec<NPCVariableDefinition<?>> CODEC = VariableType.CODEC.dispatch(
            NPCVariableDefinition::type,
            type -> createCodec(type.getValueCodec())
    );
}
