package ru.ancap.states.wars.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.assault.AttackWait;

@Getter
@AllArgsConstructor
public class AttackDeclareLoadEvent extends AncapEvent {

    private final AttackWait attackWait;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}
