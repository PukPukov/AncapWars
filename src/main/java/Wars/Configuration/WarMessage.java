package Wars.Configuration;

import AncapLibrary.Message.Message;

public class WarMessage extends Message {

    private String[] messages;

    public WarMessage(String[] messages) {
        this.messages = messages;
    }

    public WarMessage() {
    }

    public String[] getStrings() {
        return this.messages;
    }

    public void format() {
        this.replace("&", "§");
    }

    public void replace(String placeholder, String value) {
        for(int i = 0; i < this.messages.length; ++i) {
            try {
                this.messages[i] = this.messages[i].replace(placeholder, value);
            } catch (NullPointerException var5) {
                return;
            }
        }
    }

    @Deprecated
    public Message format(String... strings) {
        for(int i = 0; i < this.messages.length; ++i) {
            for(int j = 0; j < strings.length; ++j) {
                if (this.messages[i] != null) {
                    if (strings[j] == null) {
                        strings[j] = "нет";
                    }

                    this.messages[i] = this.messages[i].replaceFirst("%s", strings[j]);
                }
            }
        }

        return new AncapLibrary.Message.Message(this.messages);
    }

    @Deprecated
    public void setStrings(String[] strings) {
        this.messages = strings;
    }
}
