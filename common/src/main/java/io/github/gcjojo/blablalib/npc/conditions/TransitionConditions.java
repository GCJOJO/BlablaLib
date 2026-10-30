package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class TransitionConditions {
    private static final Map<ResourceLocation, ConditionType<?>> REGISTRY = new HashMap<>();

    public static <E extends TransitionCondition> ConditionType<?> register(ConditionType<E> type) {
        if (REGISTRY.putIfAbsent(type.id(), type) == null) {
            BlablaLib.getLogger().warn("Condition Type already registered : {}", type.id());
            return REGISTRY.get(type.id());
        }

        REGISTRY.put(type.id(), type);
        return type;
    }

    public static final Codec<ConditionType<?>> CODEC = ResourceLocation.CODEC.flatXmap(
            id -> Optional.ofNullable(REGISTRY.get(id))
                    .map(DataResult::<ConditionType<?>>success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown condition type : " + id)),
            type -> DataResult.success(type.id()));

    public static Collection<ConditionType<?>> all(){
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static final ConditionType<GreaterCondition> GREATER = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "greater"),
            GreaterCondition.CODEC);

    public static final ConditionType<GreaterThanOrEqualCondition> GREATER_THAN_OR_EQUAL = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "greater_than_or_equal"),
            GreaterThanOrEqualCondition.CODEC);

    public static final ConditionType<LessCondition> LESS = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "less"),
            LessCondition.CODEC);

    public static final ConditionType<LessThanOrEqualCondition> LESS_THAN_OR_EQUAL = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "less_than_or_equal"),
            LessThanOrEqualCondition.CODEC);

    static {
        register(EqualsCondition.TYPE);
        register(GREATER);
        register(GREATER_THAN_OR_EQUAL);
        register(LESS);
        register(LESS_THAN_OR_EQUAL);
        register(IsInDialogueCondition.TYPE);
        register(NotCondition.TYPE);
        register(AndCondition.TYPE);
        register(OrCondition.TYPE);
        register(XorCondition.TYPE);
        register(RandomCondition.TYPE);
        register(CountCondition.TYPE);
        register(AnyCondition.TYPE);
        register(AllCondition.TYPE);

        register(QuestCondition.TYPE);
    }
}
