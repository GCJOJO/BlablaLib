package io.github.gcjojo.blablalib.entities.neoforge;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.NPCVisualState;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BlablaLibEntityDataSerializersImpl {

    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, BlablaLib.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<NPCVisualState>> NPC_SERIALIZER = SERIALIZERS.register(
      "npc", () -> EntityDataSerializer.forValueType(NPCVisualState.STREAM_CODEC)
    );

    public static EntityDataSerializer<NPCVisualState> npcDefinition(){
        return NPC_SERIALIZER.get();
    }

}
