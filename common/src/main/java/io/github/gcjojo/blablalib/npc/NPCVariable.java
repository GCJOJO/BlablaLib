package io.github.gcjojo.blablalib.npc;

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
