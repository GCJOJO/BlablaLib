package io.github.gcjojo.blablalib.npc;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.definitions.NPCDefinition;
import io.github.gcjojo.blablalib.npc.definitions.NPCStateDefinition;
import io.github.gcjojo.blablalib.npc.definitions.NPCVariableDefinition;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NPCState {
    private static final int MAX_TRANSITION_STEPS = 100;

    public final NPCDefinition definition;

    public NPCStateDefinition currentStateDefinition = null;

    public final Map<String, NPCVariable<?>> variables = new HashMap<>();
    public final Map<String, NPCStateDefinition> stateDefinitionByName = new HashMap<>();

    public NPCState(NPCDefinition definition) {
        if(definition.stateDefinitions().isEmpty())
            throw new IllegalArgumentException("NPC State Definitions cannot be empty !");
        this.definition = definition;

        processStatesByName();

        for(NPCVariableDefinition<?> variableDefinition : definition.variableDefinitions()) {
            NPCVariable<?> variable = new NPCVariable<>(variableDefinition);
            variables.put(variableDefinition.variableName(), variable);
        }
    }

    public void processStatesByName() {
        for(NPCStateDefinition stateDefinition : definition.stateDefinitions()) {
            String stateName = stateDefinition.stateName();
            if(stateDefinitionByName.containsKey(stateName))
                throw new RuntimeException(String.format("State %s already exists !", stateName));
            stateDefinitionByName.put(stateName, stateDefinition);
        }

        String rootState = definition.rootState();
        if(!stateDefinitionByName.containsKey(rootState))
            throw new RuntimeException(String.format("Root State %s not found !", rootState));

        currentStateDefinition = stateDefinitionByName.get(rootState);
    }

    public boolean updateState(ServerPlayer player) {
        NPCStateDefinition newState = null;

        int steps = MAX_TRANSITION_STEPS;
        while(steps-- > 0) {
            newState = findNextValidState(player);
            if(newState == null)
                return false;
        }

        if(steps <= 0) {
            BlablaLib.getLogger().warn("Infinite Loop in NPC Transition !");
        }

        boolean stateChanged = !newState.stateName().equals(currentStateDefinition.stateName());
        currentStateDefinition = newState;

        return stateChanged;
    }

    public NPCStateDefinition findNextValidState(ServerPlayer player) {
        for(StateTransition transition : currentStateDefinition.transitions()) {
            if(transition.condition().canTransition(player, this)) {
                String newState = transition.toState();
                if(stateDefinitionByName.containsKey(newState))
                    return stateDefinitionByName.get(newState);
            }
        }
        return null;
    }

    public Optional<Object> getVariableValue(String variable) {
        if(!variables.containsKey(variable))
            return Optional.empty();

        return Optional.ofNullable(variables.get(variable).getValue());
    }
}
