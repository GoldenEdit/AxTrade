package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axtrade.hooks.unifiedmetrics.MetricType;
import com.artillexstudios.axtrade.hooks.unifiedmetrics.MetricsManager;
import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trades {
    private static final ArrayList<Trade> trades = new ArrayList<>();

    public static void addTrade(Player p1, Player p2) {
        Trade trade = new Trade(p1, p2);
        trades.add(trade);
        MESSAGEUTILS.sendLang(p1, "trade.started", Map.of("%player%", p2.getName()));
        MESSAGEUTILS.sendLang(p2, "trade.started", Map.of("%player%", p1.getName()));
        SoundUtils.playSound(p1, "started");
        SoundUtils.playSound(p2, "started");
        MetricsManager.getInstance().incrementMetric(MetricType.INITIALIZED_TRADES);
    }

    public static void removeTrade(Trade trade) {
        trades.remove(trade);
    }

    public static ArrayList<Trade> getTrades() {
        return trades;
    }

    public static boolean isTrading(Player player) {
        return trades.stream().anyMatch(trade -> trade.player1.getPlayer().equals(player) || trade.player2.getPlayer().equals(player));
    }

    @Nullable
    public static Trade getTrade(Player player) {
        return trades.stream().filter(trade -> trade.player1.getPlayer().equals(player) || trade.player2.getPlayer().equals(player)).findAny().orElse(null);
    }
}
