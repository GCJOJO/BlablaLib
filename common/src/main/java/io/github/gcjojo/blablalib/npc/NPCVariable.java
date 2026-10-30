package io.github.gcjojo.blablalib.npc;

import io.github.gcjojo.blablalib.npc.definitions.NPCVariableDefinition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NPCVariable<T> {
    private final String name;
    private T value;

    public NPCVariable(NPCVariableDefinition<T> definition) {
        this.name = definition.variableName();
        this.value = definition.defaultValue();
    }
}
