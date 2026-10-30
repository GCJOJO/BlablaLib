package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import io.github.gcjojo.liblib.LibLib;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class QuestCondition extends TransitionCondition {

    public final ResourceLocation questId;
    public final Optional<ResourceLocation> taskId;
    public final Optional<String> completionState;

    public static final MapCodec<QuestCondition> QUEST_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("quest").forGetter(QuestCondition::getQuestId),
                    ResourceLocation.CODEC.optionalFieldOf("task").forGetter(QuestCondition::getTaskId),
                    CODEC.STRING.optionalFieldOf("task").forGetter(QuestCondition::getCompletionState)
            ).apply(instance, QuestCondition::new)
    );

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "quest"),
            QUEST_CODEC
    );

    public QuestCondition(ResourceLocation questId, Optional<ResourceLocation> taskId, Optional<String> completionState) {
        this.questId = questId;
        this.taskId = taskId;
        this.completionState = completionState;
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        String targetCompletionState = completionState.orElse("completed");
        AtomicReference<String> returnValue = new AtomicReference<>("");

        taskId.ifPresentOrElse(
                id -> returnValue.set(LibLib.getQuestsLibAPI().getTaskCompletionState(player, questId, id)),
                () -> returnValue.set(LibLib.getQuestsLibAPI().getQuestCompletionState(player, questId))
        );

        return returnValue.get().equals(targetCompletionState);
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
