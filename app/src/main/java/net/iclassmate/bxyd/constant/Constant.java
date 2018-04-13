package net.iclassmate.bxyd.constant;

/**
 * Created by xyd on 2016/5/21.
 */
public class Constant {
    public static String FIRST_LOGIN = "first_login";
    public static final String APP_DIR_NAME = "xydspace";
    public static final String SHARED_PREFERENCES = "xyd";
    public static final String ID_USER = "userid";
    public static final String ID_SPACE = "spaceid";
    public static final String ID_USERTYPE = "userType";
    public static final String AUTHTOKEN = "authToken";
    public static final String USER_CODE = "userCode";
    public static final String HOME_PAGE_TITLE = "home_title";
    public static final String USER_PHONE = "user_phone_bxyd";
    public static final String IS_CONCERN = "is_concern";   //用户是否关注某一空间
    //用户头像
    public static final String USER_ICON = "user_icon";
    //用户名字
    public static final String USER_NAME = "name";
    //登陆账号
    public static final String LOGIN_NUMBER = "usernumber";
    //登陆密码
    public static final String LOGIN_PASS = "userpass";
    //融云token
    public static final String LOGIN_TOKEN = "ryToken";
    //个人主页
    public static final int TYPE_PRIVATE = 1;
    //机构主页
    public static final int TYPE_GROUP = 0;
    //空间主页
    public static final int TYPE_SPACE = 2;
    //消息提醒
    public static final String HAS_RING = "has_ring";
    //是否有好友
    public static final String HAS_FRIEND = "has_friend";
    //复制消息的ID
    public static final String MESSAGE_ID = "message_copy";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String MESSAGE_CONTENT = "message_content";
    //文字
    public static final int MESSAGE_TYPE_WORD = 0;
    //文件
    public static final int MESSAGE_TYPE_FILE = 1;
    //单聊
    public static final int CHAT_TYPE_PRIVATE = 0;
    //群聊
    public static final int CHAT_TYPE_GROUPCHAT = 1;
    //群组
    public static final int CHAT_TYPE_GROUP = 2;
    //待办消息的id
//    public static final String WAIT_MESSAGE = "wait_message";
    //聊天类型
    public static final String CHAT_TYPE = "chat_type";
    //更新版本名称：微课空间
//    public static final String UPDATE_VERSIONNAME = "space";
    //更新版本名称：冰雪运动
    public static final String UPDATE_VERSIONNAME = "bxyd";

    // APP_ID 替换为你的应用从官方网站申请到的合法appId,微信支付部分
    public static final String PAY_WX_ID = "wx196178a067370555";
    //研发
//    public static String ADDRESS = "http://space-dev.iclassmate.cn:10000/user";
//    public static String ADDRESS_IM = "http://space-dev.iclassmate.cn:10000/im-store";
//    public static String ADDRESS_SPACE = "http://space-dev.iclassmate.cn:10000/space";
//    public static String ADDRESS_STUDY = "http://space-dev.iclassmate.cn:10000";

    //测试
//    public static String ADDRESS = "http://space-test.iclassmate.cn:10000/user";
//    public static String ADDRESS_IM = "http://space-test.iclassmate.cn:10000/im-store";
//    public static String ADDRESS_SPACE = "http://space-test.iclassmate.cn:10000/space";
//    public static String ADDRESS_STUDY = "http://space-test.iclassmate.cn:10000";

    //发布
    public static String ADDRESS = "http://space.iclassmate.cn:10000/user";
    public static String ADDRESS_IM = "http://space.iclassmate.cn:10000/im-store";
    public static String ADDRESS_SPACE = "http://space.iclassmate.cn:10000/space";
    public static String ADDRESS_STUDY = "http://space.iclassmate.cn:10000";

    //注册接口
    public static String REGISTER_URL = ADDRESS + "/api/v1/registerPersonal ";
    //获取验证码
    public static String VERIFICATION_URL = ADDRESS + "/api/v1/sendPhoneVerification";
    //登陆接口
    public static String LOGIN_URL = ADDRESS + "/api/v1/login";
    //用文字模糊搜索机构或者个人
    public static String FIND_ORG_OR_PERSON = ADDRESS_SPACE + "/api/v1/user/findUser?page=1&page-size=20";
    //用文字模糊搜索群组
    public static String FIND_GROUP = ADDRESS_SPACE + "/api/v1/space?page=1&page-size=20&name=%s&type=group";
    //精确查询好友接口
    public static String FIND_USER_ACCURATE = ADDRESS + "/api/v1/findUserInfo";
    //根据（用户号，手机号，机构代码或者姓名）查询用户信息  精确查找  http://123.56.224.241:10000/space/api/v1/user/findUserInfo/1
    public static String FIND_USER_URL = ADDRESS_SPACE + "/api/v1/user/findUserInfo/%s";
    //根据（手机号、用户号、机构代码）以及类型，精确查找用户信息，用于app端   get  /api/v1/user/app/findUserInfo/{key}  http://123.56.224.241:10000/space/api/v1/user/app/findUserInfo/18810128537?type=person
    //用户类型: person(个人),org(机构),group(群组),不传查全部
    public static String MESSAGE_SEARCH_INFO = ADDRESS_STUDY + "/space/api/v1/user/app/findUserInfo/%s?type=%s";
    //组合条件查询用户信息  http://123.56.224.241:10000/space/api/v1/user/findUser?page=1&page-size=20
    public static String MESSAGE_FIND_USER = Constant.ADDRESS_STUDY + "/space/api/v1/user/findUser?page=1&page-size=0";

    //根据（用户号，手机号，机构代码或者姓名）查询用户信息  模糊查找  http://123.56.224.241:10000/space/api/v1/user/findUserByName/1
    public static String MESSAGE_FIND_USER_VAGUE = ADDRESS_SPACE + "/api/v1/user/findUserByName/%s";
    //判断2人是否是好友  http://123.56.224.241:10000/user/api/v1/app/1/isFriend/1
    public static String MESSAGE_ISFRIEND = ADDRESS_STUDY + "/user/api/v1/%s/isFriend/%s";
    //添加好友接口  ADDRESS + "/api/v1/app/addFriendShip";
    public static String SEND_FRIEND_REQUEST = ADDRESS + "/api/v1/app/addFriendShip";
    //通过用户ID查询该用户所有好友  ADDRESS + "/api/v1/app/findAllFriends";
    public static String FIND_ALL_FRIENDS = ADDRESS + "/api/v1/findAllFriends";
    //根据用户ID查询用户信息
    public static String QUERY_USER_INFORMATION = ADDRESS_SPACE + "/api/v1/user/findUserInfoByID";
    //手机重置密码   ADDRESS + "/api/v1/app/resetPassword";
    public static String RESET_PW = ADDRESS + "/api/v1/app/resetPassword";
    //获取群组列表
    public static String FIND_GROUP_URL = ADDRESS_IM + "/api/v1/imtoolservice/getsessionlist";
    //get /api/v1/imtoolservice/getGrouplist/{userId} 获取用户的群组及群聊列表，用于app端  http://123.56.224.241:10000/im-store/api/v1/imtoolservice/groupAndSpace/1
    public static String MESSAGE_GET_CHAT_GROUP = Constant.ADDRESS_STUDY + "/im-store/api/v1/imtoolservice/groupAndSpace/%s";
    //获取用户的群组会话列表(只有群聊)http://space-dev.iclassmate.cn:10000/im-store/api/v1/imtoolservice/getGrouplist/35e6c6adfe67498d852a72331b1e35ea
    public static String MESSAGE_GET_CHAT_DISCUSSION = Constant.ADDRESS_STUDY + "/im-store/api/v1/imtoolservice/getGrouplist/%s";
    //获取文件的详细信息  http://123.56.224.241:10000/fs/api/v1/getFileDetail?resourceId=9DC706D116034CAEBD1F1B12FA962481
    public static String MESSAGE_GET_FILE_DETIAL = Constant.ADDRESS_STUDY + "/fs/api/v1/getFileDetail?resourceId=%s";
    //查询用户加入的群组空间和机构空间 http://space-dev.iclassmate.cn:10000/space/api/v1/space/groupAndOrg/55dcc2e327ce4169be6200b7e384150f
    public static String GET_GROUP_AND_SPACE = Constant.ADDRESS_SPACE + "/api/v1/space/groupAndOrg/%s";
    //查询群组信息  http://space.iclassmate.cn:10000/space/api/v1/space/574ec5d7174146f39cdf73bc92fb1a72?getOwner=false&userid=574ec5d7174146f39cdf73bc92fb1a72
    public static String MESSAGE_GET_ORG_INFO = Constant.ADDRESS_STUDY + "/space/api/v1/space/%s?getOwner=false&userid=%s";
    //消息界面获取单聊、群聊、群组 信息  http://space.iclassmate.cn:10000/fs/api/v1/getInfomations?userId=1&spaceId=1
    public static String MESSAGE_GET_CHAT_INFO = Constant.ADDRESS_STUDY + "/fs/api/v1/getInfomations?userId=%s&spaceId=%s&chatType=%s";

    //创建普通群聊
    public static String CREAT_GROUP_URL = ADDRESS_IM + "/api/v1/im/group/create";
    //获取群组名称
    public static String FIND_GROUP_NAME_URL = ADDRESS_IM + "/api/v1/imtoolservice/getsessionname";
    //加入群组
    public static String ADD_GROUP_URL = ADDRESS_IM + "/api/v1/im/group/join";
    //向空间批量添加成员 http://space.iclassmate.cn:10000/space/api/v1/group/space/574ec5d7174146f39cdf73bc92fb1a72/members?type=user
    public static String ADD_SPACES_URL = ADDRESS_SPACE + "/api/v1/group/space/%s/members?type=user";
    //获取会话用户列表信息（成员）
    public static String FIND_GROUP_MEMBER = Constant.ADDRESS_IM + "/api/v1/imtoolservice/getsessionuserlist/";
    //查看选中空间中的成员（成员）http://space.iclassmate.cn:10000/space/api/v1/space/574ec5d7174146f39cdf73bc92fb1a72/members?page=1&page-size=0
    public static String FIND_GROUP_AND_SPACE_MEMBER = Constant.ADDRESS_STUDY + "/space/api/v1/space/%s/members?page=1&page-size=0";
    //修改好友备注名
    public static String REMARKS_NAME = Constant.ADDRESS + "/api/v1/updateFriendRemark";
    //修改群组名称
    public static String UPDATE_GROUP_NAME = Constant.ADDRESS_IM + "/api/v1/imtoolservice/updatesession";
    //修改空间信息    http://space.iclassmate.cn:10000/space/api/v1/space/574ec5d7174146f39cdf73bc92fb1a72
    public static String UPDATE_SPACE_INFO = Constant.ADDRESS_SPACE + "/api/v1/space/%s";
    //获取群组、群聊信息
    public static String FIND_GROUP_INFO = Constant.ADDRESS_IM + "/api/v1/imtoolservice/sessionInfo";
    //退出群聊
    public static String EXIT_GROUP = Constant.ADDRESS_IM + "/api/v1/im/group/quit";
    //从空间成员分组批量删除成员，用于app端(多人)  http://space.iclassmate.cn:10000/space/api/v1/group/app/remove/members
    public static String EXIT_SPACES = Constant.ADDRESS_STUDY + "/space/api/v1/group/app/remove/members";
    //从空间成员分组删除成员，用于app端(单人,groupId) http://space.iclassmate.cn:10000/space/api/v1/group/app/c95a551ddebb434eafdf38fadb220ca0/member/502d9b0b2be94bb5a20ac89d1699f195
    public static String EXIT_SPACE = Constant.ADDRESS_STUDY + "/space/api/v1/group/app/%s/member/%s";
    //根据spaceId从空间删除成员，用于app端(单人,spaceId) http://space.iclassmate.cn:10000/space/api/v1/group/app/quit/space/574ec5d7174146f39cdf73bc92fb1a72/f384998dadfb4336a75e74bf79af6265
    public static String EXIT_SPACE2 = Constant.ADDRESS_STUDY + "/space/api/v1/group/app/quit/space/%s/%s";
    //消息列表  获取用户名  http://123.56.224.241:10000/space/api/v1/space/user/%@/name
    public static String MESSAGE_GET_USER_NAME = Constant.ADDRESS_STUDY + "/space/api/v1/space/user/%s/name";
    //删除好友  http://123.56.224.241:10000/user/api/v1/1/deleteFriendship/1
    public static String MESSAGE_DEL_FRI = Constant.ADDRESS_STUDY + "/user/api/v1/%s/deleteFriendship/%s";
    //查找好友备注名,用于app端  get /api/v1/app/friend/{userId}/{friendId}  http://123.56.224.241:10000/user/api/v1/app/friend/1/1
    public static String MESSAGE_FIND_REMARK_NAME = Constant.ADDRESS_STUDY + "/user/api/v1/app/friend/%s/%s";
    //获取用户的群组，群聊数量get  /api/v1/imtoolservice/groupNum/{userId}  http://123.56.224.241:10000/im-store/api/v1/imtoolservice/groupNum/1
    public static String MESSAGE_GET_GROUPNUM = Constant.ADDRESS_STUDY + "/im-store/api/v1/imtoolservice/groupNum/%s";
    //获取用户的群组及群聊列表，用于app端  http://123.56.224.241:10000/im-store/api/v1/imtoolservice/groupAndSpace/1   get /api/v1/imtoolservice/groupAndSpace/{userId}
    public static String MESSAGE_GET_GROUP_APP = Constant.ADDRESS_STUDY + "/im-store/api/v1/imtoolservice/groupAndSpace/%s";
    //根据用户ID查找用户信息  get /api/v1/user/findUserInfoByID/{userId}  http://123.56.224.241:10000/space/api/v1/user/findUserInfoByID/1?needIcon=false
    public static String MESSAGE_GET_USER_INFO = Constant.ADDRESS_STUDY + "/space/api/v1/user/findUserInfoByID/%s?needIcon=%s";
    //get /api/v1/imtoolservice/getsessionname/{sessionId}  http://123.56.224.241:10000/im-store/api/v1/imtoolservice/getsessionname/1获取会话名称
    public static String MESSAGE_GET_SESSION_NAME = Constant.ADDRESS_STUDY + "/im-store/api/v1/imtoolservice/getsessionname/%s";
    // 举报  http://space-dev.iclassmate.cn:10000/user/api/v1/report  post
    public static String MESSAGE_REPORT = Constant.ADDRESS_STUDY + "/user/api/v1/report";
    //查找自己是否有好友  http://123.56.224.241:10000/user/api/v1/app/friend/27da3123d3eb4740a06920a0e309aadb
    public static String MESSAGE_HAS_FRIEND = Constant.ADDRESS_STUDY + "/user/api/v1/app/friend/%s";
    //审核申请记录  http://space.iclassmate.cn:10000/auth/api/v1/actioncheck/1/audit   /api/v1/actioncheck/{auditId}/audit
    public static String MESSAGE_JOIN_GROUP = Constant.ADDRESS_STUDY + "/auth/api/v1/actioncheck/%s/audit ";

    //地区
    public static String AREA_URL = ADDRESS + "/dictionary/api/v1/administrative-division/china";

    //学习圈
    //网盘文件
    public static String NETDISK_URL = ADDRESS_STUDY + "/fs/api/v1/getDirContentsByFullpath";
    //查看学习圈中的发布列表  http://123.56.224.241:10000/community/api/v1/listBulletinsForAppFriends?userId=a&spaceId=a&page=1&page-size=30
    public static String STUDY_RELEASE_LIST = ADDRESS_STUDY + "/community/api/v1/listBulletinsForAppFriends?userId=%s&spaceId=%s&page=%d&page-size=%d";
    //查看朋友圈中的发布列表  http://space-dev.iclassmate.cn:10000/community/api/v1/listBulletinsForFriends?spaceId=ccac6ad177a646bb81544b6109a3f686&page=1&page-size=1
    public static String STUDY_CRICLE_FRIEND_LIST = ADDRESS_STUDY + "/community/api/v1/listBulletinsForFriends?spaceId=%s&page=%d&page-size=%d";
    //发布动态  http://123.56.224.241:10000/community/api/v1/publishBulletin?userId=fd5d21fba0ed4e68a4c490bc5f50d42c
    public static String STUDY_RELEASE = ADDRESS_STUDY + "/community/api/v1/publishBulletin?userId=%s";
    //删除动态 http://123.56.224.241:10000/community/api/v1/cancelBulletins?userId=5c3057471a7544e788f9eac484774fa4
    public static String STUDY_CANCEL_RELEASE = ADDRESS_STUDY + "/community/api/v1/cancelBulletins?userId=%s";

    //朋友圈举报
    public static String STUDY_REPORT = ADDRESS_STUDY + "/community/api/v1/reportBulletin?userId=%s";
    //发表评论
    public static String STUDY_RELEASE_COMMENT = ADDRESS_STUDY + "/community/api/v1/commentBulletin?userId=%s";
    //删除评论  http://123.56.224.241:10000/community/api/v1/revokeComment?userId=fd5d21fba0ed4e68a4c490bc5f50d42c
    public static String STUDY_DEL_COMMENT = ADDRESS_STUDY + "/community/api/v1/revokeComment?userId=%s";
    //转发到主页
    public static String STUDY_FORWARD = ADDRESS_STUDY + "/community/api/v1/forwardBulletin?userId=%s";
    //查询网盘文件
    public static String STUDY_SELECT_NETFILE = ADDRESS_STUDY + "/fs/api/v1/getDirContentsByFullpath";
    //个人主页 http://123.56.224.241:10000/community/api/v1/listBulletinsForHome?spaceId=c881f6ea9b564d32877333947b24ec9b&first=false&page=1&page-size=3
    public static String STUDY_MY_PAGE = ADDRESS_STUDY + "/community/api/v1/listBulletinsForHome?spaceId=%s&page=%d&page-size=%d";
    //个人主页 查看主页中的发布列表(带userId当前用户ID参数) http://123.56.224.241:10000/community/api/v1/listBulletinsForHomeWithUserId?spaceId=1&userId=1&first=false&page=1&page-size=30
    public static String STUDY_MY_PAGE_ID = ADDRESS_STUDY + "/community/api/v1/listBulletinsForHomeWithUserId?spaceId=%s&userId=%s&first=false&page=%d&page-size=%d";
    //查看主页中的发布列表  http://space-dev.iclassmate.cn:10000/community/api/v1/listBulletinsForHome?spaceId=27f53f88ec59495180a955dcc2d07cdd&first=false&page=1&page-size=1
    public static String STUDY_MY_PAGE_LIST = ADDRESS_STUDY + "/community/api/v1/listBulletinsForHome?spaceId=%s&first=false&page=%d&page-size=%d";
    //查询收藏列表
    public static String STUDY_SAVE_LIST = ADDRESS_STUDY + "/community/api/v1/listFavorites?spaceId=%s&page=%d&page-size=%d";
    //查看收藏列表 带ID http://123.56.224.241:10000/community/api/v1/listFavoritesWithUserId?spaceId=1&userId=1&page=1&page-size=30
    public static String STUDY_SAVE_LIST_ID = ADDRESS_STUDY + "/community/api/v1/listFavoritesWithUserId?spaceId=%s&userId=%s&page=%d&page-size=%d";
    //添加收藏  http://123.56.224.241:10000/community/api/v1/addFavorite
    public static String STUDY_ADD_SAVE = ADDRESS_STUDY + "/community/api/v1/addFavorite";
    //取消收藏  http://123.56.224.241:10000/community/api/v1/deleteFavorites
    public static String STUDY_CANCEL_SAVE = ADDRESS_STUDY + "/community/api/v1/deleteFavorites";
    //发送图片  http://123.56.224.241:10000/zuul/fs/api/v1/uploadFile
    public static String STUDY_UP_FILE = ADDRESS_STUDY + "/zuul/fs/api/v1/uploadFile?userId=%s&spaceId=%s&fullPath=%s";
    //查询文件夹是否存    http://123.56.224.241:10000/fs/api/v1/queryFileDir?spaceId=2467fea2b5bb496a828e0c589e4383d6&key=image%E8%A7%84%E8%8C%83%E5%92%8C%E4%B8%AA%E4%BA%BA%E7%9A%84%E8%B4%AD%E6%88%BF
    public static String STUDY_FIND_FILE = ADDRESS_STUDY + "/fs/api/v1/queryFileDir?spaceId=%s&key=%s";
    //创建文件夹
    public static String STUDY_CREATE_FILE = ADDRESS_STUDY + "/fs/api/v1/createDir";
    //获取用户头像  http://123.56.224.241:10000/fs/api/v1/5c3057471a7544e788f9eac484774fa4/getImage/0
    public static String STUDY_GET_USER_PIC = ADDRESS_STUDY + "/fs/api/v1/%s/getImage/0";
    //获取缩略图  http://123.56.224.241:10000/fs/api/v1/5c3057471a7544e788f9eac484774fa4/getImage/0
    public static String STUDY_GET_THUMBNAIL_PIC = ADDRESS_STUDY + "/fs/api/v1/%s/getImage/1";
    //打开文件
    public static String STUDY_OPEN_FILE = ADDRESS_STUDY + "/fs/api/v1/open/%s";
    //保存空间发布资源到网盘  http://123.56.224.241:10000/fs/api/v1/saveToSpace
    public static String STUDY_SAVE_NET = ADDRESS_STUDY + "/fs/api/v1/saveToSpace";
    //查询空间之间关系  http://123.56.224.241:10000/space/api/v1/space/c881f6ea9b564d32877333947b24ec9b/related/subSpaceId/53b64241d6da45be86d21b5618ac99ff
    public static String STUDY_SELECT_SPACE_RELATIONSHIP = ADDRESS_STUDY + "/space/api/v1/space/%s/related/subSpaceId/%s";
    //判断用户与一个空间有哪些关系    http://space.iclassmate.cn:10000/space/api/v1/space/user/e25be749c16b4ae48dda38662b240445/with/spaceId/574ec5d7174146f39cdf73bc92fb1a72
    public static String USER_SELECT_SPACE_RELATIONSHIP = ADDRESS_STUDY + "/space/api/v1/space/user/%s/with/spaceId/%s";
    //获取好友列表 http://123.56.224.241:10000/user/api/v1/app/findAllFriends/5c3057471a7544e788f9eac484774fa4
    public static String STUDY_GET_FRI_LIST = ADDRESS_STUDY + "/user/api/v1/findAllFriends/%s";
    //关注别人空间 http://123.56.224.241:10000/space/api/v1/space/related
    public static String STUDY_ADD_CARE = ADDRESS_STUDY + "/space/api/v1/space/related";
    //取消关注  http://123.56.224.241:10000/space/api/v1/space/mainSpaceid/space/subSpaceid/type/{type}
    public static String STUDY_CANCEL_CARE = ADDRESS_STUDY + "/space/api/v1/space/%s/space/%s/type/%s";
    //点赞  http://123.56.224.241:10000/community/api/v1/likeBulletin?userId=fd5d21fba0ed4e68a4c490bc5f50d42c
    public static String STUDY_ADD_LIKE = ADDRESS_STUDY + "/community/api/v1/likeBulletin?userId=%s";
    //获取群组列表  http://123.56.224.241:10000/im-store/api/v1/imtoolservice/getGrouplist/userid
    public static String STUDY_GET_GROUP = ADDRESS_STUDY + "/im-store/api/v1/imtoolservice/getGrouplist/%s";
    //删除网盘文件
    public static String DELETE_NETDISK_URL = ADDRESS_STUDY + "/fs/api/v1/deleteComplete";
    //获取发布详情  http://123.56.224.241:10000/community/api/v1/getBulletinDetails?bulletinId=b55110befb80465f87e3b68672385bc6
    public static String STUDY_GET_BULLETIN_DETIAL = ADDRESS_STUDY + "/community/api/v1/getBulletinDetails?bulletinId=%s";
    //获取发布详情  查看发布详情（带userId当前用户ID参数）http://123.56.224.241:10000/community/api/v1/getBulletinDetailsWithUserId?userId=a37c22d767624e6984a36cede77fd39e&bulletinId=23822424994444f8b730811d483b0231
    public static String MESSAGE_GET_BULLETIN_DETIAL = ADDRESS_STUDY + "/community/api/v1/getBulletinDetailsWithUserId?userId=%s&bulletinId=%s";
    //获取群组主页数据  http://123.56.224.241:10000/community/api/v1/listBulletinsForHomeWithUserId?spaceId=574ec5d7174146f39cdf73bc92fb1a72&userId=fd5d21fba0ed4e68a4c490bc5f50d42c&first=false&page=1&page-size=30
    public static String STUDY_DISCOUS_HOME_PAGE = ADDRESS_STUDY + "/community/api/v1/listBulletinsForHomeWithUserId?spaceId=%s&userId=%s&first=false&page=%d&page-size=%d";

    //获取评论列表
    //http://123.56.224.241:10000/community/api/v1/listComments?bulletinId=627423d190854a90bab1b3c727b3beb2&page=1&page-size=30
    //http://123.56.224.241:10000/community/api/v1/listComments?bulletinId=239f04885d2b488997a80e780fc76821&page=0&page-size=30
    public static String STUDY_GET_COMMENT = ADDRESS_STUDY + "/community/api/v1/listComments?bulletinId=%s&page=%d&page-size=%d";
    //根据UserId获取SpaceId
    //http://123.56.224.241:10000/space/api/v1/space/my?getOwner=true&userId=
    public static String GETSPACEID_URL = ADDRESS_STUDY + "/space/api/v1/space/my?getOwner=true&userId=";
    //根据UserId获取机构群组网盘
    public static String GETOTHERDISK_URL = ADDRESS_STUDY + "/space/api/v1/space/member?";
    //修改用户密码
    public static String CHANGEPASSWORD_URL = ADDRESS_STUDY + "/user/api/v1/updatePassword";
    //通过UserId获取用户信息：
    public static String CHANGEPHONE_URL = ADDRESS_STUDY + "/user/api/v1/findUserInfoByID/";
    public static String GETUSERINFO_URL = ADDRESS_STUDY + "/space/api/v1/user/findUserInfoByID/";
    //手机号是否已被注册
    public static String PHONEISREGISTER_URL = ADDRESS_STUDY + "/user/api/v1/isExistPhone/";
    //修改用户手机号
    public static String CHANGEPHONE = ADDRESS_STUDY + "/user/api/v1/updatePhone";
    //获取关注列表
    public static String GETATTENTION = ADDRESS_STUDY + "/space/api/v1/space/";
    //取消关注
    public static String UNFOLLOWURL = ADDRESS_STUDY + "/space/api/v1/space/spacerelated/";
    //用户注销
    public static String LOGOUT_URL = ADDRESS_STUDY + "/user/api/v1/logout";
    //修改用户信息
    public static String CHANGEINFO_URL = ADDRESS_STUDY + "/space/api/v1/user/updateUserInfo";
    //修改头像
    public static String CUTICON_URL = ADDRESS_STUDY + "/space/api/v1/space/user/";
    //下载文件
    public static String DOWNLOAD_URL = ADDRESS_STUDY + "/fs/api/v1/downFile/";
    //查看空间信息
    public static String PRIVACY_URL = ADDRESS_STUDY + "/space/api/v1/space/";
    //意见反馈
    public static String FEEDBACK_URL = ADDRESS_STUDY + "/user/api/v1/feedback";
    //版本更新
    public static String UPDATE_URL = ADDRESS_STUDY + "/user/api/v1/client/2/";

    //扫一扫
    public static String SYS_URL = ADDRESS_STUDY + "/fs/api/v1/winterSports";
    //获取视频地址
    public static String VIDEO_URL = ADDRESS_STUDY + "/fs/api/v1/openByPath/";
    //申请加入群组或空间
    public static String JOIN_ORG_GROUP = ADDRESS_SPACE + "/api/v1/group/space/%s/member/%s/myGroupId/%s/needAudit";
    //生成fieldId
    public static String GETFIELD_ID = ADDRESS_STUDY + "/fs/api/v1/generateFieldId";
    //生成分块上传的单位大小
    public static String GET_BLOCK_SIZE = ADDRESS_STUDY + "/fs/api/v1/getBlockSize";
    //分块上传
    public static String BLOCK_UPLOAD = ADDRESS_STUDY + "/fs/api/v1/simpleBlockUpload?fileName=%s&fieldId=%s&chunks=%s&chunk=%s&md5=%s&userId=%s&spaceId=%s&fullPath=%s";
    //判斷一个用户是否加入某个空间
    public static String IS_JOIN = ADDRESS_STUDY + "/space/api/v1/group/user/%s/isjoin/space/%s";


    //冰雪首页接口  http://space.iclassmate.cn:10000/fs/api/v1/bxyd/firstPage?bannerNum=4&recommendNum=2
    public static String INDEX_INFO = ADDRESS_STUDY + "/fs/api/v1/bxyd/firstPage";
    //活动订单 http://space.iclassmate.cn/ice_space/#/order/10001
    public static String BX_OWNER_ACTIVITY = "http://space.iclassmate.cn/ice_space/#/order/%s";
    //生成订单 http://space.iclassmate.cn:10000/fs/api/v1/bxyd/order
    public static String BX_MAKE_ORDER = ADDRESS_STUDY + "/fs/api/v1/bxyd/order";
    //支付订单 http://space.iclassmate.cn:10000/fs/api/v1/bxyd/order/pay/orderId/userId
    public static String BX_PAY_MONEY = ADDRESS_STUDY + "/fs/api/v1/bxyd/order/pay/%s/%s";
    //立即报名 http://space.iclassmate.cn/ice_space/#/alipay/ts00001/10001/18335835602
    public static String BX_SIGN_UP = "http://space.iclassmate.cn/ice_space/#/alipay/%s/%s/%s";
    //根据视频文件的id获取视频的地址
    public static String VideoPath = ADDRESS_STUDY + "/fs/api/v1/openByPath/%s";
}
