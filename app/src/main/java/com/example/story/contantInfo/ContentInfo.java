package com.example.story.contantInfo;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by story on 2017/4/8.
 */

public class ContentInfo
{
    private String contentText;
    private Drawable contentDrawable;
    private boolean isCollection;
    private boolean isRead=false;
    private String analysisText="";
    private boolean isClickTrue;
    private String rumourTitle;
    private String imageUrl;
    private int id;
    private boolean rumourResult;
    private ArrayList<CommentsInfo> commentsInfoList;
    public ContentInfo() {
        commentsInfoList=new ArrayList<>();
    }

    public ArrayList<CommentsInfo> getCommentsInfoList() {
        return commentsInfoList;
    }

    public int getNumberOfComments()
    {
        return commentsInfoList.size();
    }

    public void addComments(String userName,String comments,String timeDelta)
    {
        commentsInfoList.add(new CommentsInfo(userName,comments,timeDelta));
    }
    public void addComments(CommentsInfo commentsInfo)
    {
        commentsInfoList.add(commentsInfo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRumourResult() {
        return rumourResult;
    }

    public void setRumourResult(boolean rumourResult) {
        this.rumourResult = rumourResult;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRumourTitle() {
        return rumourTitle;
    }

    public void setRumourTitle(String rumourTitle) {
        this.rumourTitle = rumourTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Drawable getContentDrawable() {
        return contentDrawable;
    }

    public void setContentDrawable(Drawable contentDrawable) {
        this.contentDrawable = contentDrawable;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getAnalysisText() {
        return analysisText;
    }

    public void setAnalysisText(String analysisText) {
        this.analysisText = analysisText;
    }

    public boolean isClickTrue() {
        return isClickTrue;
    }

    public void setClickTrue(boolean clickTrue) {
        isClickTrue = clickTrue;
    }

    @Override
    public String toString() {
        return "ContentInfo{" +
                "contentText='" + contentText + '\'' +
                ", analysisText='" + analysisText + '\'' +
                ", rumourTitle='" + rumourTitle + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", id=" + id +
                ", rumourResult=" + rumourResult +
                '}';
    }
}
