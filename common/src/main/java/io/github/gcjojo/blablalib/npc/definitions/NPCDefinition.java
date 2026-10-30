package io.github.gcjojo.blablalib.npc.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record NPCDefinition(Component name, List<NPCVariableDefinition<?>> variableDefinitions, List<NPCStateDefinition> stateDefinitions) {
    public static Codec<NPCDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                ComponentSerialization.CODEC.fieldOf("name").forGetter(NPCDefinition::name),
                NPCVariableDefinition.CODEC.listOf().fieldOf("variables").forGetter(NPCDefinition::variableDefinitions),
                NPCStateDefinition.CODEC.listOf().fieldOf("states").forGetter(NPCDefinition::stateDefinitions))
                .apply(instance, NPCDefinition::new));
}
