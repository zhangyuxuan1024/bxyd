package net.iclassmate.bxyd.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import net.iclassmate.bxyd.bean.LoginResult;
import net.iclassmate.bxyd.bean.area.Province;
import net.iclassmate.bxyd.bean.contacts.GroupMember;
import net.iclassmate.bxyd.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 网络请求
 * Created by xyd on 2016/6/7.
 */
public class HttpManager {
    private DataCallback dataCallback;

    public HttpManager() {
        super();
    }

    public HttpManager(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }


    /**
     * Put方式获取验证码
     *
     * @param httpUrl
     * @return
     */
    public int httpUrlConnectionPut(String httpUrl, String phone) {
        int code = 0;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        final Request request = new Request.Builder()
                .url(httpUrl)
                .put(formBody)
                .build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            code = response.code();
            //Log.i("info", "获取验证码=" + code + ", " + response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return code;
    }

    /**
     * 注册帐号
     *
     * @param url
     * @param name
     * @param phone
     * @param password
     * @param verification
     */
    public void getRegisterData(String url, String name, String phone, String password, String verification) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone", phone);
            json.put("password", password);
            json.put("verification", verification);
            json.put("appType", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
//        Log.i("info", "注册=" + json.toString());
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dataCallback.sendData(404);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                int code = response.code();
                //Log.i("HttpManager", "注册请求成功" + result + "Response Code:" + code);
                try {
                    if (code == 200) {
                        dataCallback.sendData(result);
                    } else if (code == 400) {
                        dataCallback.sendData(400);
                    } else {
                        dataCallback.sendData(404);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        });

    }

    /**
     * 获得登录需要的TOKEN
     *
     * @param userName
     * @param pwd
     */
    public void loginGetToken(String userName, String pwd) {
        String tokenUrl = Constant.LOGIN_URL;
//        Log.i("info", "登陆=" + tokenUrl);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        JSONObject json = new JSONObject();
//        {"userName":"","pwd":""}
        try {
            json.put("userName", userName);
            json.put("pwd", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        final Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dataCallback.sendData(404);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                int code = response.code();
                try {
                    LoginResult login = null;
                    if (code == 200) {
                        login = new LoginResult(code, result);
                        dataCallback.sendData(login);
                    } else if (code == 400) {
                        JSONObject object = new JSONObject(result);
                        code = object.optInt("code");
                        result = object.optString("reason");
                        login = new LoginResult(code, result);
                        dataCallback.sendData(login);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        });
    }


    public void getArea() {
        String url = Constant.AREA_URL;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dataCallback.sendData(404);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    int result_code = response.code();
                    if (result_code == 200) {
                        String result = response.body().string();
                        List<Province> provinceList = JsonUtils.StartProvinceJson(result);
                        dataCallback.sendData(provinceList);
                    } else if (result_code == 400) {
                        dataCallback.sendData(400);
                    } else if (result_code == 404) {
                        dataCallback.sendData(404);
                    }
                }
                if (response != null) {
                    response.close();
                }
            }
        });
    }

    /**
     * 精确查找好友
     *
     * @param key
     */
    public void findUserInfo(String key) {
        String url = Constant.FIND_USER_ACCURATE + "/" + key;
        //Log.e("findUserInfo", url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.i("info", "请求失败" + e.getMessage());
                Looper.prepare();
                dataCallback.sendData(404);
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                int code = response.code();
//                Log.i("HttpManager", "请求成功" + result + "Response Code:" + code);
                Looper.prepare();
                try {
                    if (code == 200) {
                        dataCallback.sendData(result);
                    } else {
                        dataCallback.sendData(404);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
                Looper.loop();
            }
        });
    }

    //精确查找好友
    public String findUsers(String key) {
        String url = String.format(Constant.FIND_USER_URL, key);
        String result = null;
//        Log.i("info", "查找好友=" + url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //模糊查找
    public String findUserVague(String key) {
        String url = String.format(Constant.MESSAGE_FIND_USER_VAGUE, key);
        String result = null;
        //Log.i("info", "查找好友模糊=" + url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 同意好友申请
     *
     * @param userAId
     * @param userBId
     * @param userAName
     * @param userBName
     * @return
     */
    public int agreeFriendRequest(String userAId, String userBId, String userAName, String userBName) {
        int code = 0;
        String tokenUrl = Constant.SEND_FRIEND_REQUEST;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("userAId", userAId);
            json.put("userBId", userBId);
            json.put("userAName", userAName);
            json.put("userBName", userBName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("info", "同意添加好友=" + json.toString());
//        Log.i("info", "好友=" + tokenUrl);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            code = response.code();
            if (code == 400) {
                String ret = response.body().string();
                JSONObject object = new JSONObject(ret);
                code = object.optInt("code");
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return code;
    }

    /**
     * 获得好友列表
     *
     * @param userId
     * @return
     */
    public String findAllFriends(String userId) {
        String url = Constant.FIND_ALL_FRIENDS + "/" + userId;
//        Log.i("info", "好友列表路径url=" + url);
        String result = "";
        //Log.e("findAllFriends", url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("findAllFriends", result);
        return result;
    }

    /**
     * 获取用户的群组及群聊列表
     * sessionType为1是群组，2是群聊，3是空间。为3的时候，如果spaceId不为空，根据spaceId取头像
     *
     * @param userId
     * @return
     */
    public String findAllGroup(String userId) {
        String url = String.format(Constant.MESSAGE_GET_CHAT_GROUP, userId);
        String result = null;
//        Log.i("info", "获取群组=" + url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
//               result=response.toString();
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //      Log.e("findAllGroup", result);
        return result;
    }

    /**
     * 获取用户的群组会话列表（只有群聊）
     * sessionType为1是群组，2是群聊，3是空间。为3的时候，如果spaceId不为空，根据spaceId取头像
     *
     * @param userId
     * @return
     */
    public String findAllDiscussion(String userId) {
        String url = String.format(Constant.MESSAGE_GET_CHAT_DISCUSSION, userId);
        String result = null;
//        Log.i("info", "获取群聊=" + url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
//               result=response.toString();
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //      Log.e("findAllGroup", result);
        return result;
    }

    /**
     * 查询用户加入的群组空间和机构空间
     *
     * @param userId
     * @return
     */
    public String findAllGroupAndSpace(String userId) {
        String url = String.format(Constant.GET_GROUP_AND_SPACE, userId);
        String result = null;
//        Log.i("info", "获取用户加入的群组空间和机构空间url=" + url);
        //创建okHttpClient对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient mOkHttpClient = builder.build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
//               result=response.toString();
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //      Log.e("findAllGroup", result);
        return result;
    }

    /**
     * 通过Id查找对方信息
     *
     * @param friendId
     * @return
     */
    public String friendInfoById(String friendId) {
        String url = Constant.QUERY_USER_INFORMATION + "/" + friendId;
        String result = null;
//        Log.i("info", "获取好友资料=" + url);
        //    Log.e("friendInfoById", url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //    Log.e("findAllFriends", result);
        return result;
    }

    /**
     * @param phone
     * @param password
     * @return
     */
    public int resetPassword(String phone, String password, String verification) {
        String url = Constant.RESET_PW;
        int code = 0;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("phone", phone);
            json.put("passWord", password);
            json.put("verification", verification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            code = response.code();
//            Log.i("info", "返回码=" + code + ",返回结果=" + response.body().string());
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 通过sessionId（群id）获取群会话名称结果
     */
    public String findSessionName(String sessionId) {
        String url = Constant.FIND_GROUP_NAME_URL + "/" + sessionId;
        String result = null;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取群组、群聊信息(sessionType:1单聊  2群聊  3群组)
     *
     * @param targetId
     * @return
     */
    public String findGroupInfo(String targetId) {
        String url = Constant.FIND_GROUP_INFO + "/" + targetId;
        String result = null;
        //创建okHttpClient对象
//        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .connectTimeout(10,TimeUnit.SECONDS)
//                .writeTimeout(60,TimeUnit.SECONDS)
//                .readTimeout(60,TimeUnit.SECONDS);
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加入群组
     *
     * @param sessionId
     * @param sessionName
     * @param userIdList
     * @param userNameList
     */

    public String addGroup(String sessionId, String sessionName, List<String> userIdList, List<String> userNameList) {
        String url = Constant.ADD_GROUP_URL;
        String result = null;
        String userId;
        String userName;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            JSONArray userList = new JSONArray();
            for (int i = 0; i < userIdList.size(); i++) {
                userId = userIdList.get(i);
                userName = userNameList.get(i);
                JSONObject user = new JSONObject();
                user.put("userId", userId);
                user.put("userName", userName);
                userList.put(user);
            }

            json.put("sessionId", sessionId);
            json.put("sessionName", sessionName);
            json.put("userList", userList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 向空间批量添加成员
     *
     * @param userIdList
     */

    public String addSpaces(String spaceId, List<String> userIdList) {
        String url = String.format(Constant.ADD_SPACES_URL, spaceId);
        Log.i("HttpManager", "向空间批量添加成员url:" + url);
        String result = null;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < userIdList.size(); i++) {
            String userId = userIdList.get(i);
            stringBuffer.append(userId);
            if (i != userIdList.size() - 1) {
                stringBuffer.append(",");
            }
        }
        String userIds = stringBuffer.toString();
        Log.i("HttpManager", "向空间批量添加成员id:" + userIds);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), userIds);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 退出群聊
     *
     * @param sessionId
     * @param userIdList
     * @param userNameList
     * @author LvZhangFeng
     */
    public String exitGroup(String sessionId, List<String> userIdList, List<String> userNameList) {
        String result = null;
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            String url = Constant.EXIT_GROUP;
            Log.i("HttpManager", "退出群聊url:" + url);
            String userId;
            String userName;
            //创建okHttpClient对象
            OkHttpClient mOkHttpClient = new OkHttpClient();

            JSONObject json = new JSONObject();
            try {
                JSONArray userList = new JSONArray();
                for (int i = 0; i < userIdList.size(); i++) {
                    userId = userIdList.get(i);
                    userName = userNameList.get(i);
                    JSONObject user = new JSONObject();
                    user.put("userId", userId);
                    user.put("userName", userName);
                    userList.put(user);
                }

                json.put("sessionId", sessionId);
                json.put("userList", userList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            try {
                Response response = mOkHttpClient.newCall(request).execute();
                if (response.code() == 200) {
                    result = response.body().string();
                } else {
                    result = "404";
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            result = "100";
        }
        return result;
    }

    /**
     * 从空间成员分组批量删除成员，用于app端(多人)
     *
     * @param groupIdList
     * @param userIdList
     * @author LvZhangFeng
     */
    public String exitSpaces(List<String> groupIdList, List<String> userIdList) {
        String result = null;
        if (NetWorkUtils.isNetworkAvailable(UIUtils.getContext())) {
            String url = Constant.EXIT_SPACES;

            String userId;
            String groupId;
            //创建okHttpClient对象
            OkHttpClient mOkHttpClient = new OkHttpClient();

            JSONArray userList = new JSONArray();
            for (int i = 0; i < userIdList.size(); i++) {
                userId = userIdList.get(i);
                groupId = groupIdList.get(i);
                JSONObject user = new JSONObject();
                try {
                    user.put("groupId", groupId);
                    user.put("userId", userId);
                    userList.put(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), userList.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            try {
                Response response = mOkHttpClient.newCall(request).execute();
                if (response.code() == 200) {
                    result = response.body().string();
                } else {
                    result = "404";
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            result = "100";
        }
        return result;
    }

    /**
     * 从空间成员分组删除成员，用于app端(单人)
     *
     * @param userId
     * @param groupId 要退空间的人在此空间中所在的某个分组的groupid
     * @return
     */
    public String exitSpace1(String userId, String groupId) {
        String url = String.format(Constant.EXIT_SPACE, groupId, userId);
        Log.i("HttpManager", "从空间成员分组删除成员，用于app端(单人)url:" + url);
        String result = "404";
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据spaceId从空间删除成员，用于app端(单人)
     *
     * @param userId
     * @param spaceId
     * @return
     */
    public String exitSpace2(String userId, String spaceId) {
        String url = String.format(Constant.EXIT_SPACE2, spaceId, userId);
        Log.i("HttpManager", "根据spaceId从空间删除成员，用于app端url:" + url);
        String result = "404";
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取会话用户列表信息（成员）
     *
     * @param sessionId 群ID
     * @author LvZhangFeng
     */
    public ArrayList<GroupMember> findGroupMember(String sessionId) {
        ArrayList<GroupMember> list = new ArrayList<>();
        String url = Constant.FIND_GROUP_MEMBER + sessionId;
        String result = null;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
                GroupMember groupMember = null;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("userList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    groupMember = new GroupMember(jsonObject1.getString("userId"), jsonObject1.getString("userName"));
                    list.add(groupMember);
                }
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.i("TAG", "群组的成员有：" + list.toString());
        return list;
    }

    /**
     * 获取会话用户列表信息（群聊成员），并且获取备注名
     *
     * @param userId
     * @param sessionId
     * @return result
     * @author LvZhangFeng
     */
    public ArrayList<GroupMember> findGroupRemarks(String userId, String sessionId) {
        ArrayList<GroupMember> list = new ArrayList<>();
        String url = Constant.FIND_GROUP_MEMBER + sessionId;
        String result = null;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
                GroupMember groupMember = null;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("userList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    groupMember = new GroupMember(jsonObject1.getString("userId"), jsonObject1.getString("userName"));
                    if (jsonObject1.getString("icon") != null && jsonObject1.getString("icon").contains("http://")) {
                        groupMember.setIcon(jsonObject1.getString("icon"));
                    }
                    String remarks = getUserRemarkName(userId, groupMember.getUserId());
                    String remark = "";
                    String userCode = "";
                    if (!remarks.equals("404")) {
                        JSONObject jsonObject2 = new JSONObject(remarks);
                        remark = jsonObject2.getString("remark");
                        userCode = jsonObject2.getString("userCode");
                        if (remark == null || TextUtils.isEmpty(remark)) {
                            remark = jsonObject1.getString("userName");
                        }
                    }
//                    String icon = getUserIconUrl(groupMember.getUserId());
//                    if(icon != null && !icon.equals("404")){    //等后台能返回头像后就删除
//                        groupMember.setIcon(icon);              //等后台能返回头像后就删除
//                    }                                           //等后台能返回头像后就删除
                    groupMember.setRemark(remark);
                    groupMember.setUserCode(userCode);
                    list.add(groupMember);
                }
            } else {
                return list;
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("TAG", "群组的成员有：" + list.toString());
        return list;
    }

    /**
     * 查看选中空间中的成员（群组和机构成员），并且获取备注名
     *
     * @param spaceId 群组机构id
     * @return result
     * @author LvZhangFeng
     */
    public ArrayList<GroupMember> findGroupRemarks2(String spaceId, String userId) {
        ArrayList<GroupMember> list = new ArrayList<>();
        String url = String.format(Constant.FIND_GROUP_AND_SPACE_MEMBER, spaceId);
//        Log.i("HttpManager", "查看选中空间中的成员url:"+url);
        String result = null;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
                GroupMember groupMember = null;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("responses");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    groupMember = new GroupMember(jsonObject1.getString("memberAlias"), jsonObject1.getString("memberId"), jsonObject1.getString("spaceId"), jsonObject1.getString("groupId"));
                    String remarks = getUserRemarkName(userId, groupMember.getUserId());
                    String remark = "";
                    String userCode = "";
                    if (!remarks.equals("404")) {
                        JSONObject jsonObject2 = new JSONObject(remarks);
                        remark = jsonObject2.getString("remark");
                        userCode = jsonObject2.getString("userCode");
                        if (remark == null || TextUtils.isEmpty(remark)) {
                            remark = jsonObject1.getString("userName");
                        }
                    }
//                    String icon = getUserIconUrl(groupMember.getUserId());
//                    if(icon != null && !icon.equals("404")){    //等后台能返回头像后就删除
//                        groupMember.setIcon(icon);              //等后台能返回头像后就删除
//                    }                                           //等后台能返回头像后就删除
                    groupMember.setRemark(remark);
                    groupMember.setUserCode(userCode);
                    list.add(groupMember);
                }
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("TAG", "群组的成员有：" + list.toString());
        return list;
    }

    /**
     * 修改好友备注名
     *
     * @param friendId
     * @param remark
     * @param userId
     * @return result
     * @author LvZhangFeng
     */
    public int remarksName(String friendId, String remark, String userId) {
        int result = 404;
        String tokenUrl = Constant.REMARKS_NAME;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("friendId", friendId);
            json.put("remark", remark);
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.code();
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改群聊名称
     *
     * @param sessionId
     * @param sessionName
     * @param updatedFlag 0假名  1真名
     * @return result
     * @author LvZhangFeng
     * @time 2016/8/9下午
     */
    public int updateGroupName(String sessionId, String sessionName, int updatedFlag) {
        int result = 404;
        String tokenUrl = Constant.UPDATE_GROUP_NAME;
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("sessionId", sessionId);
            json.put("sessionName", sessionName);
            if (updatedFlag > 0) {
                json.put("updatedFlag", updatedFlag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.code();
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改空间信息
     *
     * @param sessionId
     * @param sessionName
     * @return result
     * @author LvZhangFeng
     * @time 2016/8/9下午
     */
    public int updateSpaceName(String sessionId, String sessionName) {
        int result = 404;
        String tokenUrl = String.format(Constant.UPDATE_SPACE_INFO, sessionId);
        Log.i("HttpManager", "修改空间信息url:" + tokenUrl);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("name", sessionName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(tokenUrl)
                .put(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.code();
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断一个空间是否已经关注指定空间,判断是否关注
     *
     * @param mainSpaceId
     * @param subSpaceId
     * @return
     */
    public String getSpaceRelation(String mainSpaceId, String subSpaceId) {
        String result = "404";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_SELECT_SPACE_RELATIONSHIP, mainSpaceId, subSpaceId))
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (response.code() == 200) {
                    result = response.body().string();
                }
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建空间关系（含关注）空间关系，添加关注
     *
     * @param mainSpaceId
     * @param subSpaceId
     * @return
     */
    public String addCare(final String mainSpaceId, final String subSpaceId) {
        String result = "404";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
//            String sid = sp.getString(Constant.ID_SPACE, "");
            jsonObject.put("mainSpaceId", mainSpaceId);
            jsonObject.put("subSpaceId", subSpaceId);
            jsonObject.put("relationType", "concern");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        //Log.i("info", "添加关注参数=" + json);
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(Constant.STUDY_ADD_CARE)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            //mHandler.sendEmptyMessage(400);
        }
        return result;
    }

    /**
     * 取消空间关系（含关注）空间关系，取消关注
     *
     * @param mainSpaceId
     * @param subSpaceId
     * @return
     */
    public String cancelCare(String mainSpaceId, String subSpaceId) {
//        String sid = sp.getString(Constant.ID_SPACE, "");
        String result = "404";
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_CANCEL_CARE, mainSpaceId, subSpaceId, "concern"))
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断当前用户是否是该群组的成员
     *
     * @param
     * @param
     * @param
     * @param
     * @return
     * @author LvZhangFeng
     */
    public Boolean isGroup() {
        Boolean is_group = false;
        return is_group;
    }

    /**
     * 查看空间信息（个人和机构）
     *
     * @param getOwner 结果集是否包含空间所有者信息
     * @param userId   userid获取spaceid
     * @param type     类型
     * @return
     */
    public String findSpaceInfo(boolean getOwner, String userId, String type) {
        String result = null;
        String url;
        if (type.equals("person") || type.equals("org")) {
//            http://123.56.224.241:10000/space/api/v1/space/my?getOwner=false&userId=35e6c6adfe67498d852a72331b1e35ea
            url = Constant.PRIVACY_URL + "my?getOwner=" + getOwner + "&userId=" + userId;
        } else {
//            http://space-dev.iclassmate.cn:10000/space/api/v1/space/27f53f88ec59495180a955dcc2d07cdd?getOwner=false
            url = Constant.PRIVACY_URL + "/" + userId + "?getOwner=" + getOwner;
        }

//        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查看空间信息（userid）
     *
     * @param getOwner 结果集是否包含空间所有者信息
     * @param userId   userid获取spaceid
     * @return
     */
    public String findSpaceInfo2(boolean getOwner, String userId) {
        String result = null;
        String url;
//            http://123.56.224.241:10000/space/api/v1/space/my?getOwner=false&userId=35e6c6adfe67498d852a72331b1e35ea
        url = Constant.PRIVACY_URL + "my?getOwner=" + getOwner + "&userId=" + userId;
//        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查看空间信息（空间）
     *
     * @param getOwner 结果集是否包含空间所有者信息
     * @param spaceId  userid获取spaceid
     * @return
     */
    public String findSpaceInfo3(boolean getOwner, String spaceId) {
        String result = null;
        String url;
        //
        url = Constant.PRIVACY_URL + "/" + spaceId + "?getOwner=" + getOwner;
        Log.i("info", "请求查看空间信息路径=" + url);
//        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 查看朋友圈中的发布列表(移动端)
     *
     * @param spaceId
     * @param page      页码
     * @param page_size 页面大小，0表示全部
     */
    public String findCircleFriend(String spaceId, int page, int page_size) {
        String result = null;
        String url = String.format(Constant.STUDY_CRICLE_FRIEND_LIST, spaceId, page, page_size);
//        Log.i("info", "请求学习圈主页路径=" + url);
        final Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查看主页中的发布列表
     *
     * @param spaceId
     * @param page      页码
     * @param page_size 页面大小，0表示全部
     */
    public String findHomepageList(String spaceId, int page, int page_size) {
        String result = null;
        String url = String.format(Constant.STUDY_MY_PAGE_LIST, spaceId, page, page_size);
        Log.i("info", "请求主页中的发布列表路径=" + url);
        final Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断用户与一个空间有哪些关系（userid）
     *
     * @param userId  用户id
     * @param spaceId 判断目标空间id
     * @return
     */
    public String userSelectSpaceRelationship(String userId, String spaceId) {
        String result = null;
        String url;
//        http://space.iclassmate.cn:10000/space/api/v1/space/user/e25be749c16b4ae48dda38662b240445/with/spaceId/574ec5d7174146f39cdf73bc92fb1a72
        url = String.format(Constant.USER_SELECT_SPACE_RELATIONSHIP, userId, spaceId);
        Log.i("info", "判断用户与一个空间有哪些关系=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //创建群聊
    public String creatGroup(String author, String iconContent, String sessionName, List<String> userIdList, List<String> userNameList) {
        String result = null;
        String userId;
        String userName;
        String addGroupUrl = Constant.CREAT_GROUP_URL;
//        Log.i("TAG", "创建群聊的url：" + addGroupUrl);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            JSONArray userList = new JSONArray();
            for (int i = 0; i < userIdList.size(); i++) {
                userId = userIdList.get(i);
                userName = userNameList.get(i);
                JSONObject user = new JSONObject();
                user.put("userId", userId);
                user.put("userName", userName);
                userList.put(user);
            }

            json.put("author", author);
            json.put("iconContent", iconContent);
            json.put("sessionName", sessionName);
            json.put("sessionType", 2); //1是单聊，2是群聊，3是群组
            json.put("spaceId", "");
            json.put("userList", userList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(addGroupUrl)
                .post(formBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("addGroup", result);
        return result;
    }

    //判断是否为好友
    public String isFriend(String userAId, String userBId) {
        String result = null;
        String url = String.format(Constant.MESSAGE_ISFRIEND, userAId, userBId);
        //Log.i("info", "判断是否是好友=" + url);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
//               result=response.toString();
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //判断是否加入某一空间
    public String isJoin(String userId, String targetSpaceId) {
        String result = null;
        String url = String.format(Constant.IS_JOIN, userId, targetSpaceId);
        Log.i("info", "判断是否加入某一空间=" + url);
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.code() == 200) {
//               result=response.toString();
                result = response.body().string();
            } else {
                result = "404";
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //判断手机是否注册
    public String isRegister(String phone) {
        String ret = "";
        String url = Constant.PHONEISREGISTER_URL + phone;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "");
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            ret = response.code() + "," + response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            ret = "404";
        }
        if (response != null) {
            response.close();
        }
        return ret;
    }

    //删除好友
    public int delFri(String userId, String friId) {
        int ret = 0;
        String url = String.format(Constant.MESSAGE_DEL_FRI, userId, friId);
//        Log.i("info", "删除好友=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            ret = response.code();
        } catch (IOException e) {
            e.printStackTrace();
            ret = 404;
        }
        if (response != null) {
            response.close();
        }
        return ret;
    }

    //获取用户名和用户号（备注名）
    public String getUserRemarkName(String userid, String friendid) {
        String result = "";
        String url = String.format(Constant.MESSAGE_FIND_REMARK_NAME, userid, friendid);
//        Log.i("info", "获取用户名和用户号=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //获取群组数量
    public String getGroupNum(String userid) {
        String result = "";
        String url = String.format(Constant.MESSAGE_GET_GROUP_APP, userid);
//        Log.i("info", "获取群组数量=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //根据用户ID查找用户信息
    public String getUserInfo(String userid, boolean needIcon) {
        String result = "";
        String url = String.format(Constant.MESSAGE_GET_USER_INFO, userid, needIcon);
        Log.i("info", "获取用户信息=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //查找好友，群组，机构
    public String searchInfo(String key, String type) {
        String result = "";
        String url = String.format(Constant.MESSAGE_SEARCH_INFO, key, type);
//        Log.i("info", "查找好友，群组，机构=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //zyx 用文字模糊搜索个人或机构
    public static String searchInfoByName(String key, int type) {
        String result = "";
        String url = Constant.FIND_ORG_OR_PERSON;
//        Log.i("info", "用文字模糊搜索个人或机构：" + url);
        MediaType Json = MediaType.parse("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("name", key);
            json.put("userType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(Json, json.toString());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //zyx 用文字模糊查询群组
    public static String searchGroupByName(String name) {
        String result = "";
        String name2code = "";
        try {
            name2code = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(Constant.FIND_GROUP, name2code);
        Log.i("info", "用文字模糊查询群组：" + url);
//        MediaType Json = MediaType.parse("application/json;charset=utf-8");
//        JSONObject json = new JSONObject();
//        try {
//            json.put("name",name);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(Json, json.toString());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //模糊查找  http://123.56.224.241:10000/space/api/v1/user/findUser?page=1&page-size=20
    /*
    * 6，11，18位数字调用第一个接口
       别的位数或者汉字的话用模糊
    * */
    public String searchInfoByName(String key) {
        String result = "";
        String url = Constant.MESSAGE_FIND_USER;
//        Log.i("info", "");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("name", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, json.toString());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    //获取文件详细信息
    public String getFileInfo(String url) {
        String result = "";
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
            return result;
        }
    }

    //点赞和取消点赞
    public static Message addLiked(String bulletinId, String userid) {
        Message message = new Message();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bulletinId", bulletinId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(String.format(Constant.STUDY_ADD_LIKE, userid))
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            message.what = 3;
            message.arg1 = response.code();
            message.obj = response.body().string();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return message;
    }

    //获取用户头像的网址
    public String getUserIconUrl(String userId) {
        String result = "";
        String url = String.format(Constant.STUDY_GET_USER_PIC, userId);
//        Log.i("info", "头像=" + url);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
            return result;
        }
    }

    //获取缩略图
    public String getThumbnailIconUrl(String fileId) {
        String result = "";
        String url = String.format(Constant.STUDY_GET_THUMBNAIL_PIC, fileId);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                result = "404";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "404";
        } catch (Exception e) {
            result = "404";
        } finally {
            if (response != null) {
                response.close();
            }
            return result;
        }
    }

    //获取图片  flag  true 原图  false 压缩图片
    public Bitmap getBitmap(String url, boolean flag) {
        Bitmap bitmap = null;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream stream = response.body().byteStream();
                bitmap = BitmapFactory.decodeStream(stream);
                if (!flag) {
                    bitmap = compressImage(bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return bitmap;
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    //自己是否有好友
    public boolean isHasFriend(String uid) {
        boolean ret = true;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        String url = String.format(Constant.MESSAGE_HAS_FRIEND, uid);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                if (result.equalsIgnoreCase("false")) {
                    ret = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    //申请加入机构群组  auditId审核记录id  auditor审核人id
    public int reqJoinGroup(String auditId, String auditor) {
        int code = 0;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        String url = String.format(Constant.MESSAGE_JOIN_GROUP, auditId);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), auditor);
        Request request = new Request.Builder()
                .url(url)
                .put(formBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            code = response.code();
//            String ret = response.body().string();
//            Log.i("ifno", "code=" + code + ",ret=" + ret);
        } catch (IOException e) {
            e.printStackTrace();
            code = 404;
        }
        return code;
    }

    //获取 单聊、群聊、群组信息
    public String getChatMessageInfo(String uid, String sid, int chatType) {
        String result = "404";
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        String url = String.format(Constant.MESSAGE_GET_CHAT_INFO, uid, sid, chatType);
//        Log.i("info", "单聊=" + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getVideoPath(String id) {
        String videoPath = null;
        String url = String.format(Constant.VideoPath, id);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                videoPath = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoPath;
    }

    //根据资源id获取视频播放的路径
    public String getVideoUrl(String resourceid) {
        String result = "";
        String url = Constant.VIDEO_URL + resourceid;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
