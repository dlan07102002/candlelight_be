package vn.duclan.candlelight_be.model;

public class Notification {
    private String content;

    public Notification(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
