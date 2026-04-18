package ui;

public class Message {
    private String contend;
    private String[] contends;
    private Object metadata;

    public Message(String contend) {
        this.contend = contend;
    }

    public Message(String[] contends) {
        this.contends = contends;
    }

    public Message(String contend, Object metadata) {
        this.contend = contend;
        this.metadata = metadata;
    }

    public Message(String[] contends, Object metadata) {
        this.contends = contends;
        this.metadata = metadata;
    }

    public String getContent() {
        return contend;
    }

    public String[] getContents() {
        return contends;
    }

    public Object getMetadata() {
        return metadata;
    }
}
