package webSocketMessages;

import com.google.gson.Gson;

public class Action {
    public enum Type {
        ENTER,
        EXIT
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
