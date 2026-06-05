package io.github.gcjojo.blablalib.fabric.client;

import io.github.gcjojo.blablalib.BlablaLib;
import net.fabricmc.api.ClientModInitializer;

public final class ModEntryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlablaLib.initClient();
        BlablaLib.setSoundPlayer(new FabricSoundPlayer());
    }
}
