package io.github.gcjojo.blablalib.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.server.level.ServerPlayer;

public interface BlablalibEvents {
    Event<DialogueCompleted> DIALOGUE_COMPLETED = EventFactory.createEventResult();
    Event<DialogueChoiceMade> DIALOGUE_CHOICE_MADE = EventFactory.createEventResult();


    interface DialogueCompleted {
        EventResult dialogueCompleted(ServerPlayer player, String dialogue);
    }

    interface DialogueChoiceMade {
        EventResult dialogueChoiceMade(ServerPlayer player, String nextSet, String saveSet, String action);
    }
}
