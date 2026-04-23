package ui;

public class Message {
    private String content;
    private String[] contents;
    private Object metadata;

    public Message(String content) {
        this.content = content;
    }

    public Message(String[] contents) {
        this.contents = contents.clone();
    }

    public Message(String content, Object metadata) {
        this.content = content;
        this.metadata = metadata;
    }

    public Message(String[] contents, Object metadata) {
        this.contents = contents.clone();
        this.metadata = metadata;
    }

    public String getContent() {
        return content;
    }

    public String[] getContents() {
        return contents.clone();
    }

    public Object getMetadata() {
        return metadata;
    }
}
