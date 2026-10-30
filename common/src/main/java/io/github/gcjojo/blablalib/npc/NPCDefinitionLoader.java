package io.github.gcjojo.blablalib.npc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.Dialogue;
import io.github.gcjojo.blablalib.npc.definitions.NPCDefinition;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class NPCDefinitionLoader extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new GsonBuilder().create();
    public static Map<ResourceLocation, NPCDefinition> DEFINITIONS = new HashMap<>();

    public NPCDefinitionLoader() {
        super(GSON, "npc_definitions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DEFINITIONS.clear();
        Map<ResourceLocation, Dialogue> newDialogues = new LinkedHashMap<>();

        files.forEach((id, json) -> {
                    NPCDefinition.CODEC.parse(JsonOps.INSTANCE, json)
                            .resultOrPartial(error -> BlablaLib.getLogger().error("Invalid NPC Definition File"))
                            .ifPresent(def -> DEFINITIONS.put(id, def));
                });
    }
}
