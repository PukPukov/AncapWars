package ru.ancap.states.wars.plugin.listener;

import lombok.AllArgsConstructor;
import ru.ancap.framework.communicate.message.CallableMessage;

import java.util.*;
import java.util.function.Function;

@AllArgsConstructor
public class ChatBook<LISTED> implements CallableMessage {
    
    private final List<LISTED> content;
    private final Function<LISTED, CallableMessage> provider;
    private final Comparator<LISTED> comparator;

    @Override
    public String call(String nameIdentifier) {
        this.content.sort(this.comparator);
        List<String> result = new ArrayList<>();
        for (LISTED listed : this.content) result.add(this.provider.apply(listed).call(nameIdentifier));
        return String.join("\n", result);
    }
}
