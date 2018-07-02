package com.andrognito.patternlockdemo;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by algorithm on 2017/8/31.
 */

public class SPUtils {

    public static final String FILE_NAME = "SignInUser";// 登录用户SharedPreference文件名称

    private static SharedPreferences sSharedPreferences;
    private static SPUtils sInstance;

    // 一些通用常量的key
    public static final String MENU = "menu";
    public static final String WORKER_NAME = "workerName";
    public static final String WORKER_ID = "workerID";// 操作人员的工号
    public static final String HOST = "host";// 主机地址

    public static final String SIGN_IN ="signIn";// 用户是否登录
    public static final String SIGN_IN_LOGIN_INFO = "signInLoginInfo";
    public static final String SIGN_IN_USER_INFO = "signInUserInfo";
    public static final String SIGN_IN_NAME = "signInName";// 登录的账号
    public static final String SIGN_IN_PASSWORD = "signInPassword";// 登录密码

    private static Context mContext;

    public static void init(Context context){
          mContext=context;
    }

    // 单例模式 同步锁
    public static synchronized SPUtils getInstance() {

        if (sInstance == null) {
            sInstance = new SPUtils();
        }

        if (sSharedPreferences == null) {
            sSharedPreferences =mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }

        return sInstance;
    }

    public void set(String key, Object object) {

        if (object == null) {
            object = "";
        }

        SharedPreferences.Editor editor = sSharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    public Object get(String key, Object object) {

        if (object instanceof String) {
            return sSharedPreferences.getString(key, (String) object);
        } else if (object instanceof Integer) {
            return sSharedPreferences.getInt(key, (Integer) object);
        } else if (object instanceof Float) {
            return sSharedPreferences.getFloat(key, (Float) object);
        } else if (object instanceof Long) {
            return sSharedPreferences.getLong(key, (Long) object);
        } else if (object instanceof Boolean) {
            return sSharedPreferences.getBoolean(key, (Boolean) object);
        } else {
            return null;
        }
    }

    /**
     * 移除某个key值对应的value
     * @param key
     * @return
     */
    public SPUtils remove(String key) {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
        return sInstance;
    }

    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clazz = SharedPreferences.Editor.class;
                return clazz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                return null;
            }

        }

        public static void apply(SharedPreferences.Editor editor) {

            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
            editor.commit();
        }
    }
}
