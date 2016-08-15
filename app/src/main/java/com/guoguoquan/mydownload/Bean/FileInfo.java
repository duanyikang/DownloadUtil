package com.guoguoquan.mydownload.Bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author 小段果果
 * @time 2016/8/15  10:21
 * @E-mail duanyikang@mumayi.com
 */

public class FileInfo implements Parcelable {
    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;

    /**
     *
     * @param id
     * @param url
     * @param fileName
     * @param length
     * @param finished
     */
    public FileInfo(int id,String url,String fileName,int length,int finished)
    {
        this.id=id;
        this.url=url;
        this.fileName=fileName;
        this.length=length;
        this.finished=finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.fileName);
        dest.writeInt(this.length);
        dest.writeInt(this.finished);
    }

    public FileInfo() {
    }

    protected FileInfo(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.fileName = in.readString();
        this.length = in.readInt();
        this.finished = in.readInt();
    }

    public static final Parcelable.Creator<FileInfo> CREATOR = new Parcelable.Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}

