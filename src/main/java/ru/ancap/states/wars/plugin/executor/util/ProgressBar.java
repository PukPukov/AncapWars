package ru.ancap.states.wars.plugin.executor.util;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.communicate.message.Message;

@AllArgsConstructor
public class ProgressBar implements CallableMessage {
    
    private final double filledPart;
    private final int totalUnits;
    private final String unit;
    private final String filledStyle;
    private final String unfilledStyle;
    
    @Override
    public String call(String nameIdentifier) {
        int filledUnits = (int) (this.totalUnits * this.filledPart);

        return new Message(
               Strings.repeat(this.filledStyle  .replace("%s", this.unit), filledUnits)
            +  Strings.repeat(this.unfilledStyle.replace("%s", this.unit), this.totalUnits - filledUnits)
        ).call(nameIdentifier);
    }
}