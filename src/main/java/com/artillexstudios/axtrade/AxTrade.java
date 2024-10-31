package com.artillexstudios.axtrade;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.data.ThreadedQueue;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.utils.FeatureFlags;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.commands.Commands;
import com.artillexstudios.axtrade.currencyconverter.CurrencyConverter;
import com.artillexstudios.axtrade.hooks.HookManager;
import com.artillexstudios.axtrade.hooks.unifiedmetrics.TradeCollectorCollection;
import com.artillexstudios.axtrade.lang.LanguageManager;
import com.artillexstudios.axtrade.listeners.EntityInteractListener;
import com.artillexstudios.axtrade.listeners.TradeListeners;
import com.artillexstudios.axtrade.trade.Trade;
import com.artillexstudios.axtrade.trade.TradeTicker;
import com.artillexstudios.axtrade.utils.NumberUtils;
import com.artillexstudios.axtrade.utils.UpdateNotifier;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import dev.cubxity.plugins.metrics.api.UnifiedMetrics;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;

public final class AxTrade extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static Config GUIS;
    public static Config HOOKS;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;
    private static ThreadedQueue<Runnable> threadedQueue;
    public static BukkitAudiences BUKKITAUDIENCES;

    public static ThreadedQueue<Runnable> getThreadedQueue() {
        return threadedQueue;
    }

    public static AxPlugin getInstance() {
        return instance;
    }

    public static StateFlag TRADING;

    public void onLoad() {
        super.onLoad();
        registerFlags();
    }
    public void enable() {
        instance = this;

        int pluginId = 21500;
        new Metrics(this, pluginId);

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        GUIS = new Config(new File(getDataFolder(), "guis.yml"), getResource("guis.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        new CurrencyConverter(new Config(new File(getDataFolder(), "currencies.yml")));
        HOOKS = new Config(new File(getDataFolder(), "currencies.yml"), getResource("currencies.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());

        LanguageManager.reload();

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        threadedQueue = new ThreadedQueue<>("AxTrade-Datastore-thread");

        BUKKITAUDIENCES = BukkitAudiences.create(this);

        getServer().getPluginManager().registerEvents(new EntityInteractListener(), this);
        getServer().getPluginManager().registerEvents(new TradeListeners(), this);

        new HookManager().setupHooks();
        NumberUtils.reload();

        new TradeTicker().start();

        Commands.registerCommand();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FFDD[AxTrade] Loaded plugin!"));

        if (CONFIG.getBoolean("update-notifier.enabled", true)) new UpdateNotifier(this, 5943);

        RegisteredServiceProvider<UnifiedMetrics> registration =
                getServer().getServicesManager().getRegistration(UnifiedMetrics.class);
        if (registration != null) {
            UnifiedMetrics api = registration.getProvider();
            api.getMetricsManager().registerCollection(new TradeCollectorCollection());
            getLogger().info("[GoldenEdit Addition] Registered UnifiedMetrics logging!");


        }
    }

    public void updateFlags() { // This is not WorldGaurd
        FeatureFlags.USE_LEGACY_HEX_FORMATTER.set(true);
    }

    private void registerFlags() { // This is WorldGaurd :)
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("trading", false);
            registry.register(flag);
            TRADING = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("trading");
            if (existing instanceof IntegerFlag) {
                TRADING = (StateFlag) existing;
            } else {
                getLogger().severe("Trading flag conflict: existing flag is not an StateFlag");
            }
        }
    }
}