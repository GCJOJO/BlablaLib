package io.github.gcjojo.blablalib.fabric.entities;

import io.github.gcjojo.blablalib.entities.NPCDefinition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.jetbrains.annotations.NotNull;

public class BlablaLibEntityDataSerializersImpl {
    public static EntityDataSerializer<NPCDefinition> npcDefinition() {
        return new EntityDataSerializer<NPCDefinition>() {
            public static final StreamCodec<RegistryFriendlyByteBuf, NPCDefinition> STREAM_CODEC = StreamCodec.of(
                    (buf, def) -> def.write(buf),
                    NPCDefinition::read
            );

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, NPCDefinition> codec() {
                return STREAM_CODEC;
            }

            @Override
            public @NotNull NPCDefinition copy(NPCDefinition object) {
                return NPCDefinition.copy(object);
            }
        };
    }

    static {
        EntityDataSerializers.registerSerializer(npcDefinition());
    }
}
