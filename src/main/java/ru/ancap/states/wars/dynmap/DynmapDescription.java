package ru.ancap.states.wars.dynmap;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DynmapDescription {

    private final List<String> lines;

    public String generateHtml() {
        if (this.lines.isEmpty()) return "";
        StringBuilder generated = new StringBuilder(this.lines.get(0));
        for (String line : this.lines.stream().skip(1).toList()) generated = new StringBuilder(generated + "<br>" + line);
        return generated.toString();
    }

}
