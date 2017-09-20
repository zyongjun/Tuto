package com.windhike.annotation.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.windhike.annotation.common.AESHelper;
import com.windhike.annotation.configsapp.Configs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ManagerImageObject
        implements Parcelable
{
    private static final String TAG = "ManagerImageObject";
    public static final Creator CREATOR = new Parcelable.Creator()
    {
        public ManagerImageObject createFromParcel(Parcel paramAnonymousParcel)
        {
            ManagerImageObject localManagerImageObject = new ManagerImageObject();
            try
            {
                localManagerImageObject.init(paramAnonymousParcel);
                return localManagerImageObject;
            }
            catch (Throwable e)
            {  e.printStackTrace();
                Log.e(TAG, "createFromParcel: Failed to init Edition from parcel:"+e.getMessage());
            }
            return null;
        }

        public ManagerImageObject[] newArray(int size) {
            return new ManagerImageObject[size];
        }
    };
    private ArrayList<ImageDrawObject> listChooseDrawObject = new ArrayList();
    private int mFirstCreateProject;
    private String mSessionName = "";


    public static ManagerImageObject readFromFile(Context context, String projectName) {
        Parcel p = Parcel.obtain();
        try {
            File file = context.getFileStreamPath(new StringBuilder(String.valueOf(projectName)).append(Configs.TYPE_FORMAT_PROJECT_NAME).toString());
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[((int) file.length())];
            fis.read(data);
            fis.close();

            p.unmarshall(data, 0, data.length);
            p.setDataPosition(0);
            return (ManagerImageObject) CREATOR.createFromParcel(p);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void init(Parcel parcel) {
        parcel.readList(listChooseDrawObject,getClass().getClassLoader());
//        this.listChooseDrawObject = parcel.readArrayList(ArrayList.class.getClassLoader());
        this.mFirstCreateProject = parcel.readInt();
        this.mSessionName = parcel.readString();
    }

    public ArrayList<ImageDrawObject> getListChooseDrawObject() {
        return this.listChooseDrawObject;
    }

    public void setListChooseDrawObject(ArrayList<ImageDrawObject> listChooseDrawObject) {
        this.listChooseDrawObject = listChooseDrawObject;
    }

    public int getmFirstCreateProject() {
        return this.mFirstCreateProject;
    }

    public void setmFirstCreateProject(int mFirstCreateProject) {
        this.mFirstCreateProject = mFirstCreateProject;
    }

    public String getmSessionName() {
        return this.mSessionName;
    }

    public void setmSessionName(String mSessionName) {
        this.mSessionName = mSessionName;
    }

    public void resetData() {
        for (int i = 0; i < this.listChooseDrawObject.size(); i++) {
            this.listChooseDrawObject.get(i).resetData();
        }
        this.listChooseDrawObject.clear();
    }

    public void writeToFile(Context context, String projectName) {
        try {
            this.mSessionName = projectName;
            Log.e(TAG, "writeToFile: ==="+projectName+"---.dat" );
            projectName = AESHelper.encrypt(Configs.ENCRYPT_KEY, projectName.trim());
            if (context.deleteFile(new StringBuilder(String.valueOf(projectName)).append(Configs.TYPE_FORMAT_PROJECT_NAME).toString())) {
//                Log.e("context.deleteFile", "context.deleteFile context.deleteFile");
            }
            FileOutputStream fos = context.openFileOutput(new StringBuilder(String.valueOf(projectName)).append(Configs.TYPE_FORMAT_PROJECT_NAME).toString(), 0);
            Parcel p = Parcel.obtain();
            writeToParcel(p, 0);
            fos.write(p.marshall());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeList(this.listChooseDrawObject);
        parcel.writeInt(this.mFirstCreateProject);
        parcel.writeString(this.mSessionName);
    }
}