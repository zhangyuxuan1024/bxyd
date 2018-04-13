package net.iclassmate.bxyd.bean.message;

/**
 * 用于发送命令消息
 * Created by xyd on 2016/9/13.
 */
public class Extra {
    /**
     * Normal,         占位符0
     * SynchGroup,     同步群聊命令1
     * InviteGroup,    入群邀请命令2
     * KickOutGroup   踢出群命令3
     * SynchSpace,     同步空间命令4
     * InviteSpace,    空间邀请命令5
     * KickOutSpace   空间邀请命令6
     * ListedInBlack,      被列入黑名单7
     */
    private int cmd;
    private String groupid;
    private String groupname;
    private Extra.Operation operation;

    public Extra(Extra.Operation operation, String groupid, String groupname) {
        this.operation = operation;
        this.cmd = operation.getValue();
        this.groupid = groupid;
        this.groupname = groupname;
    }

    public Extra(int cmd, String groupid, String groupname) {
        this.cmd = cmd;
        this.groupid = groupid;
        this.groupname = groupname;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    //枚举，操作类型
    //获取Normal的索引
    //  int ordinal = Operation.Normal.ordinal();
    public static enum Operation {
        NORMAL(0, "Normal"),    //占位符
        SYNCH_GROUP(1, "SynchGroup"),   //同步群聊命令
        INVITE_GROUP(2, "InviteGroup"), //入群邀请命令
        KICK_OUT_GROUP(3, "KickOutGroup"),  //踢出群命令
        SYNCH_SPACE(4, "SynchSpace"),   //同步空间命令
        INVITE_SPACE(5, "InviteSpace"), //空间邀请命令
        KICK_OUT_SPACE(6, "KickOutSpace"),  //空间邀请命令
        LISTED_IN_BLACK(7, "ListedInBlack");    //被列入黑名单

        private int value = 0;
        private String name = "";

        Operation(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return "Extra{" +
                "cmd=" + cmd +
                ", groupid='" + groupid + '\'' +
                ", groupname='" + groupname + '\'' +
                ", operation=" + operation +
                '}';
    }
}
