package ru.ancap.states.wars.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ancap.framework.identifier.Identifier;
import ru.ancap.framework.language.LAPI;

public class LAPIPAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "LAPIPAPI";
    }

    @Override
    public @NotNull String getAuthor() {
        return "PukPukov";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String string) {
        return LAPI.localized(string, Identifier.of(player));
    }
}
