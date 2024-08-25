package com.tencentcs.iotvideo.messagemgr;

import static com.tencentcs.iotvideo.AppLinkState.APP_LINK_STATE_UNSET;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.tencentcs.iotvideo.AppLinkState;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.utils.FileIOUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageMgr implements IMessageMgr {

    public static final String TAG = "CLMessageMgr";
    private static Application mContext = null;
    private static AppLinkState mLastAppReceiveSdkStatus = APP_LINK_STATE_UNSET;
    private CopyOnWriteArrayList<IAppLinkListener> mAppLinkListeners = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<Integer, IResultListener<Boolean>> mSubscribeDeviceMap = new ConcurrentHashMap<>();
    private List<IModelListener> mModelListeners = new ArrayList();
    private List<IEventListener> mEventListeners = new ArrayList();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static class MessageMgrHolder {
        private static final MessageMgr INSTANCE = new MessageMgr();

        private MessageMgrHolder() {
        }
    }

    public static MessageMgr getInstance() {
        return MessageMgrHolder.INSTANCE;
    }

    // When there's multiple cameras initializing at a time, it becomes a bit of a race condition.
    private static AppLinkState _mSdkStatus = AppLinkState.APP_LINK_OFFLINE;
    public static AppLinkState getSdkStatus() {
            return _mSdkStatus;
    }
    private static void setSdkStatus(AppLinkState status) {
        _mSdkStatus = status;
    }

    public void register(Application application) {
        LogUtils.i(TAG, "MessageMgr register");
        mContext = application;
        mLastAppReceiveSdkStatus = APP_LINK_STATE_UNSET;
        nativeRegister();
    }

    public native long nativeGetData(String str, String str2);

    private native void nativeNotifyDevModelToServer(String str, String str2);

    private native void nativeRegister();

    public native long nativeSendDataToDevice(String str, byte[] bArr, int i10, int i11);

    public native long nativeSendDataToServer(String str, byte[] bArr);

    public native long nativeSetData(String str, String str2, String str3);

    public native long nativeSetUserParam(String str, String str2, String str3, String str4);

    private native void nativeUnregister();

    @SuppressWarnings("unused")
    private static void onModelMessage(String deviceId, long id, int type, int error, String path, String data) {
        //LogUtils.i(TAG, "onModelMessage deviceId:" + deviceId + ", id:" + id + ", type:" + type + ", error:" + error + ", path:" + path + ", data:" + data);
        getInstance().dispatchModelMessage(new ModelMessage(deviceId, id, type, error, path, data));
    }

    @SuppressWarnings("unused")
    private static void onEventMessage(final String topic, final String data) {
        LogUtils.i(TAG, "onEventMessage topic:" + topic + ", data:" + data);
        getInstance().dispatchEventMessage(new EventMessage(MessageType.MSG_TYPE_EVENT, topic, data));
    }

    @SuppressWarnings("unused")
    private static void onDataMessage(String deviceId, long id, int type, int error, byte[] data) {
        LogUtils.i(TAG, "onDataMessage deviceId = " + deviceId + ", id " + id + ", type:" + type + ", error:" + error + "; data:" + Arrays.toString(data));
    }

    @SuppressWarnings("unused")
    private static void onSubscribeDevice(final int messageId, final int error) {
        // todo: get the error code value if possible
        LogUtils.i(TAG, "onSubscribeDevice ==========start==========");
        if (getInstance().mSubscribeDeviceMap != null && !getInstance().mSubscribeDeviceMap.isEmpty())
        {
            IResultListener<Boolean> currentResListener = getInstance().mSubscribeDeviceMap.get(messageId);
            if (currentResListener == null)
            {
                LogUtils.w(TAG, "onSubscribeDevice listener is null");
                return;
            }
            LogUtils.i(TAG, "onSubscribeDevice: msgId: " + messageId + " error: " + error);
            if (error != 0)
            {
                if (isMainThread())
                {
                    currentResListener.onError(error, "subscribe device error" + error);
                    return;
                }
                getInstance().mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i(TAG, "onSubscribeDevice app status at subThread");
                        currentResListener.onError(error, "subscribe device error" + error);
                    }
                });
                LogUtils.i(TAG, "onSubscribeDevice ==========end==========");
                return;
            }
            if (isMainThread())
            {
                LogUtils.i(TAG, "onSubscribeDevice app status at uiThread");
                currentResListener.onSuccess(Boolean.TRUE);
                getInstance().mSubscribeDeviceMap.remove(messageId);
            } else {
                getInstance().mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i(TAG, "onSubscribeDevice app status at uiThread");
                        currentResListener.onSuccess(Boolean.TRUE);
                        getInstance().mSubscribeDeviceMap.remove(messageId);
                    }
                });
            }
        }
        LogUtils.i(TAG, "onSubscribeDevice ==========end==========");
    }

    private static int onNetworkDetect(int fromIp, int totalSent, int totalRec, int[] pingDelay, int resv) {
        String ret = "onNetworkDetech fromIp: " + fromIp + ", totalSend: " + totalSent + ", totalRecv: " + totalRec + ", pingDelay: " + Arrays.toString(pingDelay) + ", resv: " + resv;
        LogUtils.i(TAG, ret);
        return 0;
    }

    private static int ivCommonGetCb(int type, byte[] valueOut, int minimumLength) {
        LogUtils.i(TAG, String.format(Locale.getDefault(), "ivCommonGetCb type:%d,  len:%d, minimum:%d", type, minimumLength, minimumLength));
        byte[] readFile2BytesByStream = FileIOUtils.readFile2BytesByStream(getP2PSavePath(type));
        System.arraycopy(readFile2BytesByStream, 0, valueOut, 0, Math.min(minimumLength, readFile2BytesByStream.length));
        return Math.min(minimumLength, readFile2BytesByStream.length);
    }

    private static int ivCommonSetCb(int type, byte[] value, int length) {
        LogUtils.d(TAG, String.format(Locale.getDefault(), "ivCommonSetCb type:%d, data:%s, len:%d", type, Arrays.toString(value), length));
        if (FileIOUtils.writeFileFromBytesByStream(getP2PSavePath(type), value)) {
            return 0;
        }
        return -1;
    }

    private static String getP2PSavePath(int type) {
        Locale locale = Locale.getDefault();
        String absolutePath = mContext.getExternalFilesDir(null).getAbsolutePath();
        String pathSeparator = File.separator;
        String filePath = String.format(locale, "%s%s%s%s%d%s", absolutePath, pathSeparator, "p2pSave", pathSeparator, type, ".p2p");
        String folder = new File(filePath).getParent();
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    static Boolean isSubbed = false;

    // This gets called by JNI when the app link state changes
    @SuppressWarnings("unused")
    private static void onAppLinkStateChanged(final int status) {
        AppLinkState state = AppLinkState.fromInt(status);

        // this switch statement will only log when the state changes
        switch (state) {
            case APP_LINK_ONLINE:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link online");
                break;
            case APP_LINK_OFFLINE:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link offline");
                break;
            case APP_LINK_ACCESS_TOKEN_ERROR:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link access token error");
                break;
            case APP_LINK_TID_INIT_ERROR:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link TID init error");
                break;
            case APP_LINK_INVALID_TID:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link invalid TID");
                break;
            case APP_LINK_KICK_OFF:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link kick off");
                break;
            case APP_LINK_DEV_DISABLE:
                if(getSdkStatus() == state) LogUtils.d(TAG, "App link dev disable");
                break;
        }
        setSdkStatus(state);

        if (getInstance().mAppLinkListeners.isEmpty())
        {
            LogUtils.w(TAG, "no app link listeners to send to! status: " + getSdkStatus());
        } else {
            if (isMainThread())
            {
                for (IAppLinkListener mAppLinkListener : getInstance().mAppLinkListeners) {
                    mAppLinkListener.onAppLinkStateChanged(state);
                }
            } else {
                getInstance().mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (IAppLinkListener mAppLinkListener : MessageMgr.getInstance().mAppLinkListeners) {
                            mAppLinkListener.onAppLinkStateChanged(state);
                        }
                    }
                });
            }
            mLastAppReceiveSdkStatus = state;
        }
    }


    private void dispatchEventMessage(final EventMessage eventMessage) {
        if (getInstance().mEventListeners.isEmpty())
            {
            LogUtils.i(TAG, "no event listeners to send to! status: " + getSdkStatus());
        } else {
            if (isMainThread()) {
                for (IEventListener mEventListener : getInstance().mEventListeners) {
                    mEventListener.onNotify(eventMessage);
                }
            } else {
                getInstance().mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (IEventListener mEventListener : getInstance().mEventListeners) {
                            mEventListener.onNotify(eventMessage);
                            }
                        }
                    }
                    );

            }

        }
    }

    private void dispatchModelMessage(final ModelMessage modelMessage) {
        if (getInstance().mModelListeners.isEmpty())
        {
            LogUtils.i(TAG, "no model listeners to send to! status: " + getSdkStatus());
        } else {
            if (isMainThread())
            {
                for (IModelListener mModelListener : getInstance().mModelListeners)
                {
                    mModelListener.onNotify(modelMessage);
                }
            } else {
                getInstance().mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (IModelListener mModelListener : getInstance().mModelListeners)
                        {
                            mModelListener.onNotify(modelMessage);
                        }
                    }
                });
            }
        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public void unregister() {
        unregister(true);
    }
    public void unregister(boolean clearCacheData) {
        LogUtils.i(TAG, "unregister start， clearCacheData：" + clearCacheData);
        if (clearCacheData) {
            mContext = null;
            mModelListeners.clear();
        }
        mLastAppReceiveSdkStatus = AppLinkState.APP_LINK_STATE_UNSET;
        nativeUnregister();
        setSdkStatus(AppLinkState.APP_LINK_OFFLINE);
        LogUtils.i(TAG, "unregister end");
    }

    public void addAppLinkListener(IAppLinkListener iAppLinkListener) {
        boolean alreadyExists = false;
        for (IAppLinkListener mAppLinkListener : getInstance().mAppLinkListeners) {
            if (mAppLinkListener == iAppLinkListener) {
                alreadyExists = true;
                break;
            }
        }
        if (!alreadyExists) {
            this.mAppLinkListeners.add(iAppLinkListener);
        } else {
            // remove the old listener and add the new one
            this.mAppLinkListeners.remove(iAppLinkListener);
            this.mAppLinkListeners.add(iAppLinkListener);
        }
    }

    @Override
    public void addEventListener(IEventListener iEventListener) {
        boolean alreadyExists = false;
        Iterator<IEventListener> it = getInstance().mEventListeners.iterator();
        for (IEventListener mEvtListener : getInstance().mEventListeners) {
            if (mEvtListener == iEventListener) {
                alreadyExists = true;
                break;
            }
        }
        if (!alreadyExists) {
            this.mEventListeners.add(iEventListener);
        }
    }

    @Override
    public void addModelListener(IModelListener iModelListener) {
        boolean alreadyExists = false;
        Iterator<IModelListener> it = getInstance().mModelListeners.iterator();
        for (IModelListener mModelListener : getInstance().mModelListeners) {
            if (mModelListener == iModelListener) {
                alreadyExists = true;
            break;
            }
        }
        if (!alreadyExists) {
            this.mModelListeners.add(iModelListener);
        }
    }

    @Override
    public void notifyDevModelToServer(String str, String str2) {

    }

    @Override
    public void operateProUser(String str, String str2, String str3, String str4, int i10, IResultListener<ModelMessage> iResultListener) {

    }

    @Override
    public void operateProUser(String str, String str2, String str3, String str4, IResultListener<ModelMessage> iResultListener) {

    }

    @Override
    public void readProperty(String str, String str2, int i10, IResultListener<ModelMessage> iResultListener) {

    }

    @Override
    public void readProperty(String str, String str2, IResultListener<ModelMessage> iResultListener) {

    }

    @Override
    public void removeAppLinkListener(IAppLinkListener iAppLinkListener) {

    }

    @Override
    public void removeAppLinkListeners() {

    }

    @Override
    public void removeEventListener(IEventListener iEventListener) {

    }

    @Override
    public void removeEventListeners() {

    }

    @Override
    public void removeModelListener(IModelListener iModelListener) {
        if (iModelListener != null) {
            this.mModelListeners.remove(iModelListener);
        }
    }

    @Override
    public void removeModelListeners() {
        this.mModelListeners.clear();
    }

    @Override
    public void sendDataToDevice(String str, byte b10, byte[] bArr, boolean z10, int i10, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToDevice(String str, byte[] bArr, boolean z10, int i10, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToDevice(String str, byte[] bArr, boolean z10, int i10, boolean z11, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToDeviceWithResponse(String str, byte b10, byte[] bArr, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToDeviceWithResponse(String str, byte[] bArr, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToDeviceWithoutResponse(String str, byte b10, byte[] bArr, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToDeviceWithoutResponse(String str, byte[] bArr, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToServer(String str, byte[] bArr, int i10, IResultListener<DataMessage> iResultListener) {

    }

    @Override
    public void sendDataToServer(String str, byte[] bArr, IResultListener<DataMessage> iResultListener) {

    }

    public void subscribeDevice(@Nullable ConcurrentHashMap<Integer, IResultListener<Boolean>> concurrentHashMap) {
        this.mSubscribeDeviceMap = concurrentHashMap;
    }

    @Override
    public void writeProperty(String str, String str2, String str3, int i10, IResultListener<ModelMessage> iResultListener) {

    }

    @Override
    public void writeProperty(String str, String str2, String str3, IResultListener<ModelMessage> iResultListener) {

    }

    private static void getLocalNetIpFromP2p(byte[] bArr, byte[] bArr2) {
        try {
            String[] split = IoTVideoSdk.getLocalIPAddress().split("\\.");
            if (4 == split.length) {
                int min = Math.min(bArr.length, split.length);
                for (int i10 = 0; i10 < min; i10++) {
                    int intValue = Integer.valueOf(split[i10]).intValue();
                    if (intValue > 127) {
                        intValue -= 256;
                    }
                    bArr[i10] = (byte) intValue;
                }
                Arrays.fill(bArr2, (byte) 0);
                return;
            }
            LogUtils.e(TAG, "net list is invalid");
            Arrays.fill(bArr, (byte) 0);
            Arrays.fill(bArr2, (byte) 0);
        } catch (Exception e10) {
            e10.printStackTrace();
            Arrays.fill(bArr, (byte) 0);
            Arrays.fill(bArr2, (byte) 0);
        }
    }

}