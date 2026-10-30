package io.github.gcjojo.blablalib.npc;

import com.mojang.serialization.MapCodec;
import io.github.gcjojo.blablalib.npc.conditions.TransitionCondition;
import net.minecraft.resources.ResourceLocation;

public record ConditionType<T extends TransitionCondition>(ResourceLocation id, MapCodec<T> codec) {
}
