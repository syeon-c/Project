package model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public String host;
    public String roomId;
    public String guest;


    //public Map<String, Boolean> users = new HashMap<>();
    public Map<String, Comment> comments = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
        public String profileUrl;
    }
}
