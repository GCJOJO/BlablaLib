package io.github.gcjojo.blablalib.npc.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.npc.NPCVisualState;
import io.github.gcjojo.blablalib.npc.StateTransition;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record NPCStateDefinition(String stateName, ResourceLocation dialogueId, NPCVisualState visualState, List<StateTransition> transitions) {
    public static Codec<NPCStateDefinition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("state_name").forGetter(NPCStateDefinition::stateName),
                    ResourceLocation.CODEC.fieldOf("dialogue").forGetter(NPCStateDefinition::dialogueId),
                    NPCVisualState.CODEC.fieldOf("visual_state").forGetter(NPCStateDefinition::visualState),
                    StateTransition.CODEC.listOf().fieldOf("transitions").forGetter(NPCStateDefinition::transitions))
                    .apply(instance, NPCStateDefinition::new));

    public static final NPCStateDefinition DEFAULT = new NPCStateDefinition("default", null, NPCVisualState.EMPTY, List.of());
}
