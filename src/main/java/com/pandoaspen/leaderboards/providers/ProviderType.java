package com.pandoaspen.leaderboards.providers;

import com.pandoaspen.leaderboards.LeaderboardsPlugin;
import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.providers.dataproviders.AsyncPapiDataProvider;
import com.pandoaspen.leaderboards.providers.dataproviders.IDataProvider;
import com.pandoaspen.leaderboards.providers.dataproviders.PapiDataProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public enum ProviderType {
    ASYNC_PAPI("ASYNC_PAPI", (plugin, config) -> new AsyncPapiDataProvider(plugin, config)),
    PAPI("PAPI", (plugin, config) -> new PapiDataProvider(plugin, config));

    private final String name;
    private final BiFunction<LeaderboardsPlugin, ProviderConfig, IDataProvider> instantiator;

}
