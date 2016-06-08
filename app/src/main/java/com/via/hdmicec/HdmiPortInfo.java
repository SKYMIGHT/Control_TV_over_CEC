package com.via.hdmicec;

/**
 * Created by lewis on 12/8/15.
 */
public class HdmiPortInfo {
    private int mId;
    private int mType;
    private int mAddress;
    private boolean mCecSupported;
    private boolean mArcSupported;
    private boolean mMhlSupported;

    public HdmiPortInfo(int id, int type, int address, boolean cec, boolean mhl, boolean arc) {
        mId = id;
        mType = type;
        mAddress = address;
        mCecSupported = cec;
        mArcSupported = arc;
        mMhlSupported = mhl;
    }

    public int GetId() {
        return mId;
    }

    public int GetType() {
        return mType;
    }

    public int GetAddress() {
        return mAddress;
    }

    public boolean IsCecSupported() {
        return mCecSupported;
    }

    public boolean IsArcSupported() {
        return mArcSupported;
    }

    public boolean IsMhlSupported() {
        return mMhlSupported;
    }
}
