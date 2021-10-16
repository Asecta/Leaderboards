package com.pandoaspen.leaderboards.providers.dataproviders;

import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.providers.serializers.JsonDataSerializer;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractProvider extends JsonDataSerializer implements IDataProvider {

    @Getter private final ProviderConfig providerConfig;

    public AbstractProvider(JavaPlugin plugin, ProviderConfig providerConfig) {
        super(plugin);
        this.providerConfig = providerConfig;
    }


}
