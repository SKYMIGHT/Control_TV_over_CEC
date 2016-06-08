package com.via.hdmicec;


import com.via.hdmicec.HdmiPortInfo;
/**
 * Created by lewis on 12/8/15.
 */

enum OptionFlag {
    HDMI_OPTION_WAKEUP,
    HDMI_OPTION_ENABLE_CEC,
    HDMI_OPTION_SYSTEM_CEC_CONTROL;
}

interface TypeCecCallback{
    void onMessageCallback(int srcAddress, int dstAddress, byte[] body, int port);
    void onHotplugCallback(int port, boolean connected);
}

public class ViaCecDevice{
    static {
        System.loadLibrary("via_hdmi_cec");
    }
    private TypeCecCallback _cecCallback;

    private native long Init(ViaCecDevice handler, String stringMessageCB, String stringHotplugCB, String stringPortinfoClass);
    public native int SendCecCommand(long nativeClassPtr, int srcAdd, int dstAdd, byte[] body, int portId);
    public native int AddLogicalAddress(long nativeClassPtr, int logicalAddr, int portId);
    public native void ClearLogicalAddress(long nativeClassPtr, int portId);
    public native int GetPhysicalAddress(long nativeClassPtr, int portId);
    public native int GetVersion(long nativeClassPtr);
    public native int GetVendorId(long nativeClassPtr);
    public native HdmiPortInfo[] GetPortInfos(long nativeClassPtr);
    public native void SetOption(long nativeClassPtr, int flag, int value);
    public native boolean IsConnected(long nativeClassPtr,int portId);

    public  long InitCec(ViaCecDevice handler, TypeCecCallback callback, String path) {
        _cecCallback = callback;
        return Init(handler, "onMessageCB", "onHotplugCB", path);
    }

    private void onMessageCB(int srcAddress, int dstAddress, byte[] body, int port) {
        _cecCallback.onMessageCallback(srcAddress, dstAddress, body, port);
    }

    private void onHotplugCB(int port, boolean connected) {
        _cecCallback.onHotplugCallback(port, connected);
    }
}
