package io.github.gcjojo.blablalib.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


public class BlablaLibNeoForgeConfig {

    public static final BlablaLibNeoForgeConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<BlablaLibNeoForgeConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(BlablaLibNeoForgeConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<Boolean> ENABLE_COMMAND;/* = BUILDER
            .comment("Whether or not the Blabla Lib mod should register it's default dialogue command.")
            .define("enable_command", false);*/

    private BlablaLibNeoForgeConfig(ModConfigSpec.Builder builder) {
        ENABLE_COMMAND = builder.comment("Whether or not the Blabla Lib mod should register it's default dialogue command.")
                .translation("blablalib.config.enable_command_value_name")
                .define("enable_command", false);
    }

}

