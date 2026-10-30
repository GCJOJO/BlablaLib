package io.github.gcjojo.blablalib.fabric.entities;

import io.github.gcjojo.blablalib.npc.NPCVisualState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.jetbrains.annotations.NotNull;

public class BlablaLibEntityDataSerializersImpl {
    public static EntityDataSerializer<NPCVisualState> npcDefinition() {
        return new EntityDataSerializer<NPCVisualState>() {
            public static final StreamCodec<RegistryFriendlyByteBuf, NPCVisualState> STREAM_CODEC = StreamCodec.of(
                    (buf, def) -> def.write(buf),
                    NPCVisualState::read
            );

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, NPCVisualState> codec() {
                return STREAM_CODEC;
            }

            @Override
            public @NotNull NPCVisualState copy(NPCVisualState object) {
                return NPCVisualState.copy(object);
            }
        };
    }

    static {
        EntityDataSerializers.registerSerializer(npcDefinition());
    }
}
