package io.github.gcjojo.blablalib.npc;

import io.github.gcjojo.blablalib.npc.definitions.NPCDefinition;
import io.github.gcjojo.blablalib.npc.definitions.NPCVariableDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NPCState {
    public final NPCDefinition definition;

    public final Map<String, NPCVariable<?>> variables = new HashMap();

    public NPCState(NPCDefinition definition) {
        this.definition = definition;

        for(NPCVariableDefinition<?> variableDefinition : definition.variableDefinitions()) {
            NPCVariable<?> variable = new NPCVariable<>(variableDefinition);
            variables.put(variableDefinition.variableName(), variable);
        }
    }

    public Optional<Object> getVariableValue(String variable) {
        if(!variables.containsKey(variable))
            return Optional.empty();

        return Optional.ofNullable(variables.get(variable).getValue());
    }
}
