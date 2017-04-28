package com.example.story.contantInfo;

/**
 * Created by story on 2017/4/15.
 */

public class CommentsInfo {
    private String userName;
    private String userComments;
    private String timeDelta;

    public String getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(String timeDelta) {
        this.timeDelta = timeDelta;
    }

    public CommentsInfo(String userName, String userComments,String createdTime) {
        this.userName = userName;
        this.userComments = userComments;
        this.timeDelta=createdTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComments() {
        return userComments;
    }

    public void setUserComments(String userComments) {
        this.userComments = userComments;
    }

    @Override
    public String toString() {
        return "CommentsInfo{" +
                "userName='" + userName + '\'' +
                ", userComments='" + userComments + '\'' +
                ", createdTime='" + timeDelta + '\'' +
                '}';
    }
}
