package com.artillexstudios.axtrade.hooks;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.hooks.currency.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class HookManager {
    private static final ArrayList<CurrencyHook> currency = new ArrayList<>();

    public void setupHooks() {
        updateHooks();
    }

    public void updateHooks() {
        currency.removeIf(currencyHook -> !currencyHook.isPersistent());


        if (HOOKS.getBoolean("currencies.Vault.register", true) && Bukkit.getPluginManager().getPlugin("Vault") != null) {
            currency.add(new VaultHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into Vault!"));
        }

        for (CurrencyHook hook : currency) hook.setup();
    }

    @SuppressWarnings("unused")
    public static void registerCurrencyHook(@NotNull Plugin plugin, @NotNull CurrencyHook currencyHook) {
        currency.add(currencyHook);
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into " + plugin.getName() + "! Note: You must set the currency provider to CUSTOM or it will be overridden after reloading!"));
    }

    @NotNull
    public static ArrayList<CurrencyHook> getCurrency() {
        return currency;
    }

    @Nullable
    public static CurrencyHook getCurrencyHook(@NotNull String name) {
        for (CurrencyHook hook : currency) {
            if (!hook.getName().equals(name)) continue;
            return hook;
        }

        return null;
    }
}
