package com.pandoaspen.leaderboards.config.providers;

import com.google.gson.annotations.SerializedName;
import com.pandoaspen.leaderboards.providers.ProviderTrigger;
import com.pandoaspen.leaderboards.utils.Duration;
import lombok.Getter;

@Getter
public class TriggerConfig {
    @SerializedName("trigger-type") private ProviderTrigger.TriggerType triggerType;
    @SerializedName("delay") private Duration delay;
}
