package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Random;

@Getter
public class RandomCondition extends TransitionCondition {
    public final float chance;

    public static final MapCodec<RandomCondition> RANDOM_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("chance").forGetter(RandomCondition::getChance)
            ).apply(instance, RandomCondition::new));

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "chance"),
            RANDOM_CODEC
    );

    public RandomCondition(float chance) {
        this.chance = chance;
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        Random rng = new Random();
        float randomNumber = rng.nextFloat();
        return randomNumber >= chance;
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
