package com.pandoaspen.leaderboards.providers.dataproviders;

import com.pandoaspen.leaderboards.config.providers.ProviderConfig;
import com.pandoaspen.leaderboards.providers.registry.DataRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncPapiDataProvider extends PapiDataProvider {

    private Map<Integer, List<DataRegistry>> queryMap;
    private ThreadPoolExecutor executorService;

    public AsyncPapiDataProvider(JavaPlugin plugin, ProviderConfig providerConfig) {
        super(plugin, providerConfig);
        this.queryMap = new ConcurrentHashMap<>();
        this.executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Override
    public void prepareNextTop(long since, int limit) {
        int queryHash = hashCode(since, limit);
        executorService.submit(() -> queryMap.put(queryHash, super.getTop(since, limit)));
    }

    @Override
    public List<DataRegistry> getTop(long since, int limit) {
        int queryHash = hashCode(since, limit);
        if (queryMap.containsKey(queryHash)) {
            return queryMap.get(queryHash);
        } else {
            return super.getTop(since, limit);
        }
    }

    private int hashCode(long since, int limit) {
        final int PRIME = 59;
        int result = 1;
        final long $since = since;
        result = result * PRIME + (int) ($since >>> 32 ^ $since);
        result = result * PRIME + limit;
        return result;
    }
}
