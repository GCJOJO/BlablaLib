package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.blablalib.entities.NPC;
import io.github.gcjojo.blablalib.npc.NPCState;
import io.github.gcjojo.blablalib.npc.VariableType;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;


// Gemini Slop
public class DialogueNPC extends DialogueAction {
    @Getter
    private final int targetNpcId;
    @Getter
    private final String variableName;
    @Getter
    private final VariableType variableType;
    @Getter
    private final Object newValue;

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueNPC> NPC_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull DialogueNPC decode(RegistryFriendlyByteBuf buf) {
            int npcId = buf.readVarInt();
            String varName = buf.readUtf();
            VariableType varType = VariableType.STREAM_CODEC.decode(buf);

            Object value = varType.getValueStreamCodec().decode(buf);

            return new DialogueNPC(npcId, varName, varType, value);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, DialogueNPC action) {
            buf.writeInt(action.targetNpcId);
            buf.writeUtf(action.variableName);
            VariableType.STREAM_CODEC.encode(buf, action.variableType);

            action.variableType.getValueStreamCodec().encode(buf, action.newValue);
        }
    };

    private static final DialogueManager.DialogueActionType<DialogueNPC> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_npc"),
            NPC_STREAM_CODEC
    );

    public DialogueNPC(JsonObject json) {
        targetNpcId = json.has("npc") ? json.get("npc").getAsInt() : -1;
        variableName = json.get("variable").getAsString();

        String typeStr = json.get("type").getAsString();
        variableType = VariableType.CODEC.decode(JsonOps.INSTANCE, JsonOps.INSTANCE.createString(typeStr))
                .getOrThrow().getFirst();

        newValue = variableType.getValueCodec()
                .decode(JsonOps.INSTANCE, json.get("value"))
                .getOrThrow().getFirst();
    }

    public DialogueNPC(int npcId, String variableName, VariableType variableType, Object newValue) {
        this.targetNpcId = npcId;
        this.variableName = variableName;
        this.variableType = variableType;
        this.newValue = newValue;
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        screen.queueAdvanceDialogue();
    }

    @Override
    public void actionCompleted(ServerPlayer player) {
        UUID playerId = player.getUUID();
        Optional<NPC> npcOptional = BlablaLib.getPlayerNPC(player, targetNpcId);

        npcOptional.ifPresent(npc -> {
            NPCState state = npc.getCurrentState(player);
            if(!state.hasVariable(variableName)) return;
            if(state.getVariableType(variableName) != variableType) return;

            state.setVariableValue(variableName, newValue);
        });
    }

    @Override
    public void step() {

    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public boolean isSkippable() {
        return false;
    }

    @Override
    public DialogueManager.DialogueActionType<?> getType() {
        return TYPE;
    }
}
