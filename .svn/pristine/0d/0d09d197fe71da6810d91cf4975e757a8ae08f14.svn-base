package net.iclassmate.bxyd.utils;

import android.text.TextUtils;
import android.util.Log;

import net.iclassmate.bxyd.bean.Owner;
import net.iclassmate.bxyd.bean.UserInFo;
import net.iclassmate.bxyd.bean.area.City;
import net.iclassmate.bxyd.bean.area.County;
import net.iclassmate.bxyd.bean.area.Province;
import net.iclassmate.bxyd.bean.attention.Attention_All;
import net.iclassmate.bxyd.bean.attention.Attention_Responses;
import net.iclassmate.bxyd.bean.attention.SubSpace;
import net.iclassmate.bxyd.bean.contacts.FindUserInfo;
import net.iclassmate.bxyd.bean.contacts.FriendInfo;
import net.iclassmate.bxyd.bean.contacts.Group;
import net.iclassmate.bxyd.bean.contacts.GroupInfo;
import net.iclassmate.bxyd.bean.contacts.UserInfo;
import net.iclassmate.bxyd.bean.message.UserMessage;
import net.iclassmate.bxyd.bean.netdisk.Administartors;
import net.iclassmate.bxyd.bean.netdisk.Extend;
import net.iclassmate.bxyd.bean.netdisk.FileDirList;
import net.iclassmate.bxyd.bean.netdisk.FileLabel;
import net.iclassmate.bxyd.bean.netdisk.NetDisk;
import net.iclassmate.bxyd.bean.netdisk.OtherDisk;
import net.iclassmate.bxyd.bean.netdisk.Responses;
import net.iclassmate.bxyd.bean.netdisk.list;
import net.iclassmate.bxyd.bean.owner.Authority;
import net.iclassmate.bxyd.bean.owner.Information;
import net.iclassmate.bxyd.bean.owner.SpaceInfo;
import net.iclassmate.bxyd.bean.owner.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * json解析帮助类
 * Created by xyd on 2016/6/7.
 */
public class JsonUtils {
    public static List<UserMessage> StartUserMessageJson(String json) {

        List<UserMessage> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.optJSONArray("responses");
            for (int i = 0; i < jsonArray.length() && jsonArray != null; i++) {
                UserMessage userMessage = new UserMessage();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                net.iclassmate.bxyd.bean.message.UserInfo userInfo = new net.iclassmate.bxyd.bean.message.UserInfo();

                userInfo.setName(jsonObject1.getString("name"));
                userInfo.setTaggetId(jsonObject1.getString("uuid"));
                userInfo.setUserType(jsonObject1.getString("type"));
                userInfo.setUserIcon(jsonObject1.getString("icon"));

                userMessage.setUserInfo(userInfo);
                list.add(userMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 地区
     */
    public static List<Province> StartProvinceJson(String json) {
        List<Province> provinceList = null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            provinceList = new ArrayList<Province>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Province province = new Province();
                province.setProvince_name(jsonObject.getString("name"));
                province.setProvince_code(jsonObject.getString("code"));
                JSONArray jsonArray1 = jsonObject.getJSONArray("children");
                List<City> cityList = new ArrayList<City>();
                if (jsonArray1.length() != 0) {
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        City city = new City();
                        city.setCity_name(jsonObject1.getString("name"));
                        city.setCity_code(jsonObject1.getString("code"));
                        JSONArray jsonArray2 = jsonObject1.getJSONArray("children");
                        List<County> countyList = new ArrayList<County>();
                        if (jsonArray2.length() != 0) {
                            for (int k = 0; k < jsonArray2.length(); k++) {
                                JSONObject jsonObject2 = jsonArray2.getJSONObject(k);
                                County county = new County();
                                county.setCounty_name(jsonObject2.getString("name"));
                                county.setCounty_code(jsonObject2.getString("code"));
                                countyList.add(county);
                            }
                        } else {
                            County county = new County();
                            county.setCounty_name("");
                            county.setCounty_code("");
                            countyList.add(county);
                        }
                        city.setCountyList(countyList);
                        cityList.add(city);
                    }
                } else {
                    City city = new City();
                    city.setCity_name("");
                    city.setCity_code("");
                    List<County> countyList = new ArrayList<>();
                    County county = new County();
                    county.setCounty_name("");
                    county.setCounty_code("");
                    countyList.add(county);
                    city.setCountyList(countyList);
                    cityList.add(city);
                }
                province.setCityList(cityList);
                provinceList.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinceList;
    }

    public JsonUtils() {
    }

    public static List<FindUserInfo> jsonFindUserInfoList(String jsonString) {
        List<FindUserInfo> findUserInfos = null;
        FindUserInfo findUserInfo = null;
        UserInfo userInfo = null;
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                if (jsonArray != null) {
                    findUserInfos = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String id = jsonObject1.getString("id");
                        JSONObject userObject = jsonObject1.getJSONObject("userInfo");
                        if (userObject != null) {

                            String icon = userObject.optString("icon");
                            String name = userObject.optString("name");
                            String userCode = userObject.optString("userCode");
                            userInfo = new UserInfo(icon, userCode, name);
                        }
                        findUserInfo = new FindUserInfo(id, userInfo);
                        findUserInfos.add(findUserInfo);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return findUserInfos;
    }

    public static FindUserInfo jsonFindUserInfo(String jsonString) {
        FindUserInfo findUserInfo = null;
        UserInfo userInfo = null;
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String id = jsonObject.getString("id");
                JSONObject userObject = jsonObject.getJSONObject("userInfo");
                if (userObject != null) {
                    String icon = userObject.getString("icon");
                    String name = userObject.getString("name");
                    String phone = userObject.getString("phone");
                    userInfo = new UserInfo(icon, phone, name);
                }
                findUserInfo = new FindUserInfo(id, userInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return findUserInfo;
    }

    public static ArrayList<FriendInfo> jsonFriendInfo(String jsonString) {
        FriendInfo friendInfo = null;
        ArrayList<FriendInfo> friendInfos = null;
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                if (jsonArray != null) {
                    friendInfos = new ArrayList<FriendInfo>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String userId = jsonArray.getJSONObject(i).getString("userId");
                        String friendId = jsonArray.getJSONObject(i).getString("friendId");
                        String groupId = jsonArray.getJSONObject(i).getString("groupId");
                        String remark = jsonArray.getJSONObject(i).getString("remark");
                        String userName = jsonArray.getJSONObject(i).getString("userName");
//                        String icon=jsonArray.getJSONObject(i).getString("icon");
                        if (userName != null && !TextUtils.isEmpty(userName) && !userName.equals("")) {
                            friendInfo = new FriendInfo(userId, friendId, groupId, remark, userName);
                            friendInfos.add(friendInfo);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friendInfos;
    }

    public static FindUserInfo jsonFriendInformation(String jsonString) {
        FindUserInfo findUserInfo = null;
        UserInfo userInfo = null;
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String id = jsonObject.getString("id");
                JSONObject userObject = jsonObject.getJSONObject("userInfo");
                if (userObject != null) {

                   /* {
                        "id" : "245e35ec5a1b4e0dab72d9dda85d9b0a",
                            "userInfo" : {
                        "fsRoot" : "b8ce0d75c28e45d9a89ff993f3ba8c07",
                                "userCode" : "625224",
                                "icon" : null,
                                "name" : "tanding",
                                "userType" : "1",
                                "labels" : null,
                                "searchMe" : "1",
                                "phone" : "13260026009",
                                "capacity" : "100",
                    }*/

                    String ryToken = userObject.optString("ryToken");
                    String icon = userObject.optString("icon");
                    String userCode = userObject.optString("userCode");
                    String phone = userObject.optString("phone");
                    String searchMe = userObject.optString("searchMe");
                    String name = userObject.optString("name");
                    String userType = userObject.optString("userType");

                    String area = userObject.optString("area");
                    String gender = userObject.optString("gender");
                    String city = userObject.optString("city");
                    String eduLevel = userObject.optString("eduLevel");
                    String remark = userObject.optString("remark");
                    String province = userObject.optString("province");
                    String dateBirth = userObject.optString("dateBirth");
                    String email = userObject.optString("email");
                    userInfo = new UserInfo(area, gender, city, eduLevel, ryToken, icon, remark, userCode, province, phone, searchMe, name, userType, dateBirth, email);
                }
                findUserInfo = new FindUserInfo(id, userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return findUserInfo;
    }

    public static Group jsonGroupInfo(String json) {
        Group group = null;
        ArrayList<GroupInfo> groupInfos = null;
        GroupInfo groupInfo = null;
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int resultCode = jsonObject.getInt("resultCode");
                String resultMarks = jsonObject.getString("resultMarks");
                JSONArray groupArray = jsonObject.getJSONArray("sessionList");
                groupInfos = new ArrayList<>();
                for (int i = 0; i < groupArray.length(); i++) {

                    String sessionId = groupArray.getJSONObject(i).getString("sessionId");
                    String sessionName = groupArray.getJSONObject(i).getString("sessionName");
                    String author = groupArray.getJSONObject(i).getString("author");
                    int sessionType = groupArray.getJSONObject(i).getInt("sessionType");
                    String updateTime = groupArray.getJSONObject(i).getString("updateTime");
                    String spaceId = groupArray.getJSONObject(i).getString("updateTime");
                    String sessionIcon = groupArray.getJSONObject(i).getString("sessionIcon");
                    groupInfo = new GroupInfo(sessionId, sessionName, author, sessionType, updateTime, spaceId, sessionIcon);
                    groupInfos.add(groupInfo);
                }
                group = new Group(resultCode, resultMarks, groupInfos);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return group;
    }

    public static Group jsonGroupInfo2(String json) {
        Group group = null;
        ArrayList<GroupInfo> groupInfos = null;
        GroupInfo groupInfo = null;
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray groupArray = jsonObject.getJSONArray("groupSpaces");
                groupInfos = new ArrayList<>();
                for (int i = 0; i < groupArray.length(); i++) {
                    String sessionId = groupArray.getJSONObject(i).getString("uuid");
                    String sessionName = groupArray.getJSONObject(i).getString("name");
                    String author = groupArray.getJSONObject(i).getString("ownerId");
                    String type = groupArray.getJSONObject(i).getString("type");
                    String icon = groupArray.getJSONObject(i).getString("icon");
                    int sessionType;
                    sessionType = 3;
                    groupInfo = new GroupInfo(sessionId, sessionName, author, sessionType, icon);
                    groupInfos.add(groupInfo);
                }
                JSONArray groupArray2 = jsonObject.getJSONArray("orgSpaces");
                for (int i = 0; i < groupArray2.length(); i++) {
                    String sessionId = groupArray2.getJSONObject(i).getString("uuid");
                    String sessionName = groupArray2.getJSONObject(i).getString("name");
                    String author = groupArray2.getJSONObject(i).getString("ownerId");
                    String type = groupArray2.getJSONObject(i).getString("type");
                    String icon = groupArray2.getJSONObject(i).getString("icon");
                    int sessionType = 4;
                    JSONObject properties = groupArray2.getJSONObject(i).getJSONObject("properties");
                    String code = properties.getString("userCode");
                    groupInfo = new GroupInfo(sessionId, sessionName, author, sessionType, icon);
                    groupInfo.setCode(code);
                    groupInfos.add(groupInfo);
                }
                group = new Group(1, "", groupInfos);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return group;
    }

    public static List<io.rong.imlib.model.Group> jsonSysnGroup(String json) {

        io.rong.imlib.model.Group group = null;
        List<io.rong.imlib.model.Group> groups = null;
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray groupArray = jsonObject.getJSONArray("sessionList");
                groups = new ArrayList<>();
                for (int i = 0; i < groupArray.length(); i++) {
                    String sessionId = groupArray.getJSONObject(i).getString("sessionId");
                    String sessionName = groupArray.getJSONObject(i).getString("sessionName");
                    group = new io.rong.imlib.model.Group(sessionId, sessionName, null);
                    groups.add(group);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return groups;
    }


    public static NetDisk StartNetDiskJson(String json) {
        NetDisk netDisk = null;
        List<FileDirList> fileDirLists = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("fileDirList");
            netDisk = new NetDisk();
            fileDirLists = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                FileDirList fileDirList = new FileDirList();
                fileDirList.setAuth(jsonObject1.getString("auth"));
                fileDirList.setCreateTime(jsonObject1.getString("createTime"));

                JSONObject jsonObject2 = jsonObject1.optJSONObject("fileLabel");
                if (jsonObject2 != null) {
                    FileLabel fileLabel = new FileLabel();
                    fileLabel.setCreateTime(jsonObject2.optString("createTime"));
                    JSONObject jsonObject3 = jsonObject2.optJSONObject("extend");
                    if (jsonObject3 != null) {
                        Extend extend = new Extend();
                        extend.setContact(jsonObject3.optString("contact"));
                        extend.setGrade(jsonObject3.optString("grade"));
                        extend.setIntro(jsonObject3.optString("intro"));
                        extend.setSubject(jsonObject3.optString("subject"));
                        fileLabel.setExtend(extend);
                    }
                    fileLabel.setFileId(jsonObject2.optString("fileId"));
                    fileLabel.setOwnerId(jsonObject2.optString("ownerId"));
                    fileLabel.setSpaceId(jsonObject2.optString("spaceId"));
                    fileLabel.setTagIcon(jsonObject2.optString("tagIcon"));
                    fileLabel.setTagId(jsonObject2.optString("tagId"));
                    fileLabel.setTagName(jsonObject2.optString("tagName"));
                    fileLabel.setTagType(jsonObject2.optString("tagType"));
                    fileDirList.setFileLabel(fileLabel);
                }
                fileDirList.setFileType(jsonObject1.getString("fileType"));
                fileDirList.setFullPath(jsonObject1.getString("fullPath"));
                fileDirList.setId(jsonObject1.getString("id"));
                fileDirList.setLabel(jsonObject1.getString("label"));
                fileDirList.setParentId(jsonObject1.getString("parentId"));
                fileDirList.setSaveUuid(jsonObject1.getString("saveUuid"));
                fileDirList.setScale(jsonObject1.getString("scale"));
                fileDirList.setOssPath(jsonObject1.getString("ossPath"));
                fileDirList.setSeq(jsonObject1.getString("seq"));
                fileDirList.setShortName(jsonObject1.getString("shortName"));
                fileDirList.setSize(jsonObject1.getString("size"));
                fileDirList.setSpaceUuid(jsonObject1.getString("spaceUuid"));
                fileDirList.setType(jsonObject1.getInt("type"));
                fileDirList.setUpdateTime(jsonObject1.getString("updateTime"));
                fileDirList.setUserUuid(jsonObject1.getString("userUuid"));
                fileDirLists.add(fileDirList);
            }
            netDisk.setFileDirLists(fileDirLists);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return netDisk;
    }

    public static UserInFo StartUserInfoJson(String json) {
        UserInFo userInFo = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            userInFo = new UserInFo();
            userInFo.setUuid(jsonObject.getString("uuid"));
            userInFo.setUsedSpace(jsonObject.getLong("usedSpace"));
            Owner owner = new Owner();
            JSONObject jsonObject1 = jsonObject.getJSONObject("owner");
            net.iclassmate.bxyd.bean.owner.UserInfo userInfo = new net.iclassmate.bxyd.bean.owner.UserInfo();
            JSONObject jsonObject2 = jsonObject1.getJSONObject("userInfo");
            userInfo.setUserType(jsonObject2.getInt("userType"));
            owner.setUserInfo(userInfo);
            userInFo.setOwner(owner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userInFo;
    }

    public static OtherDisk StartOtherDiskJson(String json) {
        OtherDisk otherDisk = null;
        List<Responses> responsesList = null;
        try {
            otherDisk = new OtherDisk();
            JSONObject jsonObject = new JSONObject(json);
            otherDisk.setPageSize(jsonObject.optInt("pageSize"));
            otherDisk.setStartPage(jsonObject.optInt("startPage"));
            otherDisk.setTotal(jsonObject.getInt("total"));
            JSONArray jsonArray = jsonObject.getJSONArray("responses");
            responsesList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Responses responses = new Responses();
                JSONObject jsonObject2 = jsonObject1.getJSONObject("administrators");
                Administartors admin = new Administartors();
                JSONArray jsonArray1 = jsonObject2.getJSONArray("list");
                List<list> list = new ArrayList<>();
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                    list l = new list();
                    l.setUserId(jsonObject3.getString("userId"));
                    l.setUserName(jsonObject3.getString("userName"));
                    list.add(l);
                    admin.setListList(list);
                }
                responses.setAdministartors(admin);
                responses.setUuid(jsonObject1.getString("uuid"));
                responses.setDescription(jsonObject1.getString("description"));
                responses.setIcon(jsonObject1.optString("icon"));
                responses.setJoinGroupId(jsonObject1.getString("joinGroupId"));
                responses.setJoinTime(jsonObject1.getString("joinTime"));
                responses.setMyGroupId(jsonObject1.getString("myGroupId"));
                responses.setName(jsonObject1.getString("name"));
                responses.setOwnerId(jsonObject1.getString("ownerId"));
                responses.setRelatedCount(jsonObject1.getInt("relatedCount"));
                responses.setType(jsonObject1.getString("type"));
                responsesList.add(responses);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        otherDisk.setResponsesList(responsesList);

        return otherDisk;
    }

    public static Attention_All StartAttentionJson(String json) {
        Attention_All attention_all = null;
        try {
            attention_all = new Attention_All();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("responses");
            List<Attention_Responses> attention_responsesList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Attention_Responses attention_responses = new Attention_Responses();
                attention_responses.setNoteName(jsonObject1.getString("noteName"));
                attention_responses.setUuid(jsonObject1.getString("uuid"));
                attention_responses.setSubSpaceId(jsonObject1.getString("subSpaceId"));
                attention_responsesList.add(attention_responses);
                JSONObject jsonObject2 = jsonObject1.getJSONObject("subSpace");
                SubSpace subSpace = new SubSpace();
                if (jsonObject2.optString("icon") != null) {
                    subSpace.setIcon(jsonObject2.getString("icon"));
                }
                subSpace.setType(jsonObject2.getString("type"));
                subSpace.setOwnerId(jsonObject2.getString("ownerId"));
                attention_responses.setSubSpace(subSpace);
            }
            attention_all.setAttention_responsesList(attention_responsesList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attention_all;
    }

    public static Information StartInformationJson(String json) {
        Information info = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            info = new Information();
            info.setId(jsonObject.getString("id"));
            JSONObject jsonObject1 = jsonObject.getJSONObject("userInfo");
            net.iclassmate.bxyd.bean.owner.UserInfo userInfo = new net.iclassmate.bxyd.bean.owner.UserInfo();
            userInfo.setArea(jsonObject1.optString("area"));
            userInfo.setCapacity(jsonObject1.optString("capacity"));
            userInfo.setCity(jsonObject1.optString("city"));
            userInfo.setProvince(jsonObject1.optString("province"));
            userInfo.setDateBirth(jsonObject1.optString("dateBirth"));
            userInfo.setName(jsonObject1.optString("name"));
            userInfo.setEduLevel(jsonObject1.optString("eduLevel"));
            userInfo.setEmail(jsonObject1.optString("email"));
            userInfo.setFsRoot(jsonObject1.optString("fsRoot"));
            userInfo.setGender(jsonObject1.optString("gender"));
            userInfo.setIcon(/*Constant.ADDRESS_STUDY + */jsonObject1.optString("icon"));
            userInfo.setIntroduction(jsonObject1.optString("Introduction"));
            userInfo.setPhone(jsonObject1.optString("phone"));
            userInfo.setRemark(jsonObject1.optString("remark"));
            userInfo.setRyToken(jsonObject1.optString("ryToken"));
            userInfo.setSearchMe(jsonObject1.optInt("searchMe"));
            userInfo.setUserCode(jsonObject1.optString("userCode"));
            userInfo.setUserType(jsonObject1.optInt("userType"));
            userInfo.setTradeType(jsonObject1.optString("tradeType"));
            info.setUserInfo(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static SpaceInfo StartSpaceInfoJson(String json) {
        SpaceInfo spaceInfo = new SpaceInfo();
        Authority authority = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObject1 = jsonObject.getJSONObject("authority");
            authority = new Authority();
            authority.setBecomeContact(jsonObject1.getBoolean("becomeContact"));
            authority.setFocusMe(jsonObject1.getBoolean("focusMe"));
            authority.setNoticeOwnerWhenComment(jsonObject1.getBoolean("noticeOwnerWhenComment"));
            authority.setNoticeOwnerWhenForward(jsonObject1.getBoolean("noticeOwnerWhenForward"));
            authority.setNoticeOwnerWhenPraise(jsonObject1.getBoolean("noticeOwnerWhenPraise"));
            authority.setNoticeOwnerWhenReply(jsonObject1.getBoolean("noticeOwnerWhenReply"));
            authority.setSearchMe(jsonObject1.getBoolean("searchMe"));
            authority.setSearchMyresource(jsonObject1.getBoolean("searchMyresource"));
            authority.setVisitDisk(jsonObject1.getBoolean("visitDisk"));
            authority.setVisitHomepage(jsonObject1.getBoolean("visitHomepage"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        spaceInfo.setAuthority(authority);

        return spaceInfo;
    }

    public static Update StartUpdateJson(String json) {
        Update update = new Update();
        try {
            JSONObject jsonObject = new JSONObject(json);
            update.setVersion(jsonObject.getString("version"));
            update.setUrl(jsonObject.getString("url"));
            update.setUpdateDesc(jsonObject.getString("updateDesc"));
            update.setSize(jsonObject.getString("size"));
            update.setCreateTime(jsonObject.getLong("createTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return update;
    }
}
