package com.avsolution.networkUtils;

import static com.avsolution.networkUtils.Constants.TYPE_MOBILE_DATA;
import static com.avsolution.networkUtils.Constants.TYPE_WIFI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.avsolution.DataMonitorActivity;
import com.avsolution.R;
import com.avsolution.dao.Application;
import com.avsolution.modules.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LoadData extends AsyncTask {
    private final Context mContext;
    private final int session;
    List<Application> mUserAppsList = new ArrayList<>();
    List<Application> mSystemAppsList = new ArrayList<>();


    public LoadData(Context mContext, int session) {
        this.mContext = mContext;
        this.session = session;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DataMonitorActivity.isDataLoading = true;
        mUserAppsList.clear();
        mSystemAppsList.clear();
        Log.d("LoadData", "onPreExecute: load data");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected Object doInBackground(Object[] objects) {
        Long sent = 0L,
                received = 0L,

                totalSystemSent = 0L,
                totalSystemReceived = 0L,
                totalTetheringSent = 0L,
                totalTetheringReceived = 0L, totalUserSent = 0L, totalUserReceived = 0L,
                tetheringTotal = 0L;

        DatabaseHandler handler = new DatabaseHandler(mContext);
        List<Application> list = handler.getUsageList();
        Application model = null;

        Long deviceTotal = 0L;

        try {
            deviceTotal = NetworkStatsHelper.getDeviceTotalDataUsage(mContext, session)[2];
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < list.size(); i++) {
            Application currentData = list.get(i);
            if (currentData.isSystemApp()) {
                    try {
                        Long[] data = NetworkStatsHelper.getAppAllTypeDataUsage(mContext, currentData.getUid(), session);
                        sent =data[0];
                        received = data[1];
                        totalSystemSent = totalSystemSent + sent;
                        totalSystemReceived = totalSystemReceived + received;

                        if (sent > 0 || received > 0) {
                            Log.d("LoadData", currentData.getPackage()+" : "+String.valueOf(sent)+", "+ String.valueOf(received));
                            model = new Application();
                            model.setName(currentData.getName());
                            model.setPackage(currentData.getPackage());
                            model.setUid(currentData.getUid());
                            model.setIcon(currentData.getIcon());
                            model.setmSent(sent);
                            model.setmReceived(received);
                            model.setmTotalData((float) (sent+received));
                            model.setSession(session);
                            mSystemAppsList.add(model);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            } else {
                if (Util.isAppInstalled(mContext, currentData.getPackage())) {
                        try {
                            Long[] data = NetworkStatsHelper.getAppAllTypeDataUsage(mContext, currentData.getUid(), session);
                            sent =data[0];
                            received = data[1];
                            totalUserSent = totalUserSent + sent;
                            totalUserReceived = totalUserReceived + received;

                            if (sent > 0 || received > 0) {
                                Log.d("LoadData", currentData.getPackage()+" : "+String.valueOf(sent)+", "+ String.valueOf(received));
                                model = new Application();
                                model.setName(currentData.getName());
                                model.setPackage(currentData.getPackage());
                                model.setUid(currentData.getUid());
                                model.setIcon(currentData.getIcon());
                                model.setmSent(sent);
                                model.setmReceived(received);
                                model.setmTotalData((float) (sent+received));
                                model.setSession(session);
                                mUserAppsList.add(model);
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
        //Total System apps Data
        model = new Application();
        model.setName(mContext.getString(R.string.label_system_apps));
        model.setPackage(mContext.getString(R.string.package_system));
        model.setIcon(mContext.getDrawable(R.drawable.ic_system_app));
        model.setmSent(totalSystemSent);
        model.setmReceived(totalSystemReceived);
        model.setmTotalData((float) (totalSystemSent+totalSystemReceived));
        model.setSession(session);
        Log.d("LoadData", "SystemTotal"+" : "+String.valueOf(totalSystemSent)+", "+ String.valueOf(totalSystemReceived));

        //Total User Apps Data
        model = new Application();
        model.setName(mContext.getString(R.string.label_user_apps));
        model.setPackage(mContext.getString(R.string.package_user));
        model.setIcon(mContext.getDrawable(R.drawable.ic_userapp));
        model.setmSent(totalUserSent);
        model.setmReceived(totalUserReceived);
        model.setmTotalData((float) (totalUserSent+totalUserReceived));
        model.setSession(session);
        Log.d("LoadData", "UserTotal"+" : "+String.valueOf(totalUserSent)+", "+ String.valueOf(totalUserReceived));

        if (deviceTotal > 0) {
            mUserAppsList.add(model);
        }

        try {
                //Tethering DATA usage
                Long[] data=  NetworkStatsHelper.getTetheringDataUsage(mContext, session);
                totalTetheringSent = data[0];
                totalTetheringReceived = data[1];
                tetheringTotal = totalTetheringSent + totalTetheringReceived;

                model = new Application();
                model.setName(mContext.getString(R.string.label_tethering));
                model.setPackage(mContext.getString(R.string.package_tethering));
                model.setIcon(mContext.getDrawable(R.drawable.ic_tethering_apps));
                model.setmSent(totalTetheringSent);
                model.setmReceived(totalTetheringReceived);
                model.setmTotalData((float) (totalTetheringSent+totalTetheringReceived));
                model.setSession(session);
                model.setSession(session);
                Log.d("LoadData", "TetheringData"+" : "+totalTetheringSent+", "+ totalTetheringReceived);
                if (tetheringTotal > 0) {
                    mUserAppsList.add(model);
                }
            Collections.sort(mUserAppsList, new Comparator<Application>() {
                @Override
                public int compare(Application o1, Application o2) {
                    o1.setmTotalData((o1.getmSent() + o1.getmReceived()) / 1024f);
                    o2.setmTotalData((o2.getmSent() + o2.getmReceived()) / 1024f);
                    return o1.getmTotalData().compareTo(o2.getmTotalData());
                }
            });

            Collections.reverse(mUserAppsList);
            Collections.sort(mSystemAppsList, new Comparator<Application>() {
                @Override
                public int compare(Application o1, Application o2) {
                    o1.setmTotalData((o1.getmSent() + o1.getmReceived()) / 1024f);
                    o2.setmTotalData((o2.getmSent() + o2.getmReceived()) / 1024f);
                    return o1.getmTotalData().compareTo(o2.getmTotalData());
                }
            });
            Collections.reverse(mSystemAppsList);


        } catch (ParseException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        DataMonitorActivity.isDataLoading = false;
        DataMonitorActivity.mSystemAppsList =mSystemAppsList;
        DataMonitorActivity.mUserAppsList = mUserAppsList;
        FetchApps fetchApps = new FetchApps(mContext);
        fetchApps.execute();
    }

}
