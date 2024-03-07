package com.boilednimame.refinedproxyreborn.config;

import com.boilednimame.refinedproxyreborn.ObjectID;
import net.minecraftforge.common.ForgeConfigSpec;

/*
 * reference:
 *    https://github.com/refinedmods/refinedstorage/blob/v1.10.6/src/main/java/com/refinedmods/refinedstorage/config/ServerConfig.java
 */

public class ServerConfig implements ObjectID {
    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private final ForgeConfigSpec spec;
    private final RefinedProxy rProxy;

    public ServerConfig() {
        rProxy = new RefinedProxy();

        spec = builder.build();
    }

    public RefinedProxy getrRefinedProxy() {
        return rProxy;
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public class RefinedProxy {
        private final ForgeConfigSpec.IntValue usage;

        public RefinedProxy() {
            builder.push(ID_REFINEDPROXY);

            usage = builder.comment("The energy used by the RefinedProxy")
                           .defineInRange("usage", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }

    }
}

