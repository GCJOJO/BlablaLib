package io.github.gcjojo.blablalib.entities;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class BlablaLibEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BlablaLib.MOD_ID, Registries.ENTITY_TYPE);

    public static RegistrySupplier<EntityType<NPC>> NPC_TYPE;

    public static void registerEntityTypes() {
        NPC_TYPE = registerEntityType("npc", () -> EntityType.Builder.of(NPC::new, MobCategory.MISC)
                .sized(0.9f, 1.95f)
                .fireImmune()
                .clientTrackingRange(60)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.tryBuild(BlablaLib.MOD_ID, "npc")).toString()));

        ENTITIES.register();
    }

    private static <T extends Entity> RegistrySupplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityType) {
        return ENTITIES.register(ResourceLocation.tryBuild(BlablaLib.MOD_ID, name), entityType);
    }
}
