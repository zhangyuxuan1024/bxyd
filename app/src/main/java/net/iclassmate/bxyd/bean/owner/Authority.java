package net.iclassmate.bxyd.bean.owner;

/**
 * Created by xydbj on 2016.7.30.
 */
public class Authority {
    private boolean focusMe;
    private boolean searchMe;
    private boolean searchMyresource;

    private boolean noticeOwnerWhenForward;
    private boolean noticeOwnerWhenPraise;
    private boolean noticeOwnerWhenComment;
    private boolean noticeOwnerWhenReply;
    private boolean visitHomepage;
    private boolean visitDisk;
    private boolean becomeContact;

    @Override
    public String toString() {
        return "Authority{" +
                "focusMe=" + focusMe +
                ", searchMe=" + searchMe +
                ", searchMyresource=" + searchMyresource +
                ", noticeOwnerWhenForward=" + noticeOwnerWhenForward +
                ", noticeOwnerWhenPraise=" + noticeOwnerWhenPraise +
                ", noticeOwnerWhenComment=" + noticeOwnerWhenComment +
                ", noticeOwnerWhenReply=" + noticeOwnerWhenReply +
                ", visitHomepage=" + visitHomepage +
                ", visitDisk=" + visitDisk +
                ", becomeContact=" + becomeContact +
                '}';
    }

    public boolean isFocusMe() {
        return focusMe;
    }

    public void setFocusMe(boolean focusMe) {
        this.focusMe = focusMe;
    }

    public boolean isSearchMe() {
        return searchMe;
    }

    public void setSearchMe(boolean searchMe) {
        this.searchMe = searchMe;
    }

    public boolean isSearchMyresource() {
        return searchMyresource;
    }

    public void setSearchMyresource(boolean searchMyresource) {
        this.searchMyresource = searchMyresource;
    }

    public boolean isNoticeOwnerWhenForward() {
        return noticeOwnerWhenForward;
    }

    public void setNoticeOwnerWhenForward(boolean noticeOwnerWhenForward) {
        this.noticeOwnerWhenForward = noticeOwnerWhenForward;
    }

    public boolean isNoticeOwnerWhenPraise() {
        return noticeOwnerWhenPraise;
    }

    public void setNoticeOwnerWhenPraise(boolean noticeOwnerWhenPraise) {
        this.noticeOwnerWhenPraise = noticeOwnerWhenPraise;
    }

    public boolean isNoticeOwnerWhenComment() {
        return noticeOwnerWhenComment;
    }

    public void setNoticeOwnerWhenComment(boolean noticeOwnerWhenComment) {
        this.noticeOwnerWhenComment = noticeOwnerWhenComment;
    }

    public boolean isNoticeOwnerWhenReply() {
        return noticeOwnerWhenReply;
    }

    public void setNoticeOwnerWhenReply(boolean noticeOwnerWhenReply) {
        this.noticeOwnerWhenReply = noticeOwnerWhenReply;
    }

    public boolean isVisitHomepage() {
        return visitHomepage;
    }

    public void setVisitHomepage(boolean visitHomepage) {
        this.visitHomepage = visitHomepage;
    }

    public boolean isVisitDisk() {
        return visitDisk;
    }

    public void setVisitDisk(boolean visitDisk) {
        this.visitDisk = visitDisk;
    }

    public boolean isBecomeContact() {
        return becomeContact;
    }

    public void setBecomeContact(boolean becomeContact) {
        this.becomeContact = becomeContact;
    }
}
