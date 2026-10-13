package com.engear.vansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.engear.vansales.R;
import com.engear.vansales.common.AppConstants;
import com.engear.vansales.common.Constants;
import com.engear.vansales.common.SharedPreferenceManager;
import com.engear.vansales.interfaces.DrawerItemClickListner;
import com.engear.vansales.model.DrawerItemModel;

import java.util.List;

public class DrawarAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private List<DrawerItemModel> listStorage;
    DrawerItemClickListner drawerItemClickListner;
    private SharedPreferenceManager mPreferenceManager;


    //public ViewHolder listViewHolder;
    Context context;

    public DrawarAdapter(Context context, List<DrawerItemModel> customizedListView, DrawerItemClickListner drawerItemClickListner) {
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
        this.context = context;
        this.drawerItemClickListner = drawerItemClickListner;
        mPreferenceManager = new SharedPreferenceManager(this.context);
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = lInflater.inflate(R.layout.row_sidemenu, parent, false);

            listViewHolder.llListItem = (LinearLayout) convertView.findViewById(R.id.llListItem);
            listViewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            listViewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);

            convertView.setTag(listViewHolder);

        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }

        if (listStorage.get(position).getName() == "Journey Plan") {
            if (mPreferenceManager.getString(AppConstants.ROLE).equals("1") || mPreferenceManager.getString(AppConstants.ROLE).equals("6")) {
                if (mPreferenceManager.getString(AppConstants.IS_ENDDAY).equalsIgnoreCase("1") &&
                        mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                    listViewHolder.llListItem.setAlpha(0.5f);
                    listViewHolder.llListItem.setEnabled(false);
                    listViewHolder.llListItem.setClickable(false);
                } else {
                    if (mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                        listViewHolder.llListItem.setAlpha(1.0f);
                        listViewHolder.llListItem.setEnabled(true);
                        listViewHolder.llListItem.setClickable(true);
                    } else {
                        listViewHolder.llListItem.setAlpha(0.5f);
                        listViewHolder.llListItem.setEnabled(false);
                        listViewHolder.llListItem.setClickable(false);
                    }
                }
            } else {
                if (mPreferenceManager.getString(AppConstants.IS_LOAD_VERIFY).equalsIgnoreCase("1") &&
                        mPreferenceManager.getString(AppConstants.IS_ENDDAY).equalsIgnoreCase("1") &&
                        mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                    listViewHolder.llListItem.setAlpha(0.5f);
                    listViewHolder.llListItem.setEnabled(false);
                    listViewHolder.llListItem.setClickable(false);
                } else {
                    if (mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")
                            && mPreferenceManager.getString(AppConstants.IS_LOAD_VERIFY).equalsIgnoreCase("1")) {
                        listViewHolder.llListItem.setAlpha(1.0f);
                        listViewHolder.llListItem.setEnabled(true);
                        listViewHolder.llListItem.setClickable(true);
                    } else {
                        listViewHolder.llListItem.setAlpha(0.5f);
                        listViewHolder.llListItem.setEnabled(false);
                        listViewHolder.llListItem.setClickable(false);
                    }
                }
            }
        } else if (listStorage.get(position).getName() == "Manage Inventory") {
            if (mPreferenceManager.getString(AppConstants.IS_LOAD_VERIFY).equalsIgnoreCase("1") &&
                    mPreferenceManager.getString(AppConstants.IS_ENDDAY).equalsIgnoreCase("1") &&
                    mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                listViewHolder.llListItem.setAlpha(0.5f);
                listViewHolder.llListItem.setEnabled(false);
                listViewHolder.llListItem.setClickable(false);
            } else {
                if (mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                    listViewHolder.llListItem.setAlpha(1.0f);
                    listViewHolder.llListItem.setEnabled(true);
                    listViewHolder.llListItem.setClickable(true);
                } else {
                    listViewHolder.llListItem.setAlpha(0.5f);
                    listViewHolder.llListItem.setEnabled(false);
                    listViewHolder.llListItem.setClickable(false);
                }
            }
        } else if (listStorage.get(position).getName() == "Payment") {
            if (mPreferenceManager.getString(AppConstants.IS_ENDDAY).equalsIgnoreCase("1")) {
                listViewHolder.llListItem.setAlpha(1.0f);
                listViewHolder.llListItem.setEnabled(true);
                listViewHolder.llListItem.setClickable(true);
            } else {
                listViewHolder.llListItem.setAlpha(0.5f);
                listViewHolder.llListItem.setEnabled(false);
                listViewHolder.llListItem.setClickable(false);
            }
        } else if (listStorage.get(position).getName() == "End Trip") {

            if (mPreferenceManager.getString(AppConstants.ROLE).equals("1")) {
                if (mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                    if (mPreferenceManager.getString(Constants.IS_ENDTRIP).equalsIgnoreCase("1")) {
                        listViewHolder.llListItem.setAlpha(0.5f);
                        listViewHolder.llListItem.setEnabled(false);
                        listViewHolder.llListItem.setClickable(false);
                    } else {
                        listViewHolder.llListItem.setAlpha(1.0f);
                        listViewHolder.llListItem.setEnabled(true);
                        listViewHolder.llListItem.setClickable(true);
                    }

                } else {
                    listViewHolder.llListItem.setAlpha(0.5f);
                    listViewHolder.llListItem.setEnabled(false);
                    listViewHolder.llListItem.setClickable(false);
                }
            } else {
                if (mPreferenceManager.getString(AppConstants.IS_STARTDAY).equalsIgnoreCase("1")) {
                    if (mPreferenceManager.getString(AppConstants.IS_ENDDAY).equalsIgnoreCase("1")) {
                        if (mPreferenceManager.getString(Constants.IS_ENDTRIP).equalsIgnoreCase("1")) {
                            listViewHolder.llListItem.setAlpha(0.5f);
                            listViewHolder.llListItem.setEnabled(false);
                            listViewHolder.llListItem.setClickable(false);
                        } else {
                            listViewHolder.llListItem.setAlpha(1.0f);
                            listViewHolder.llListItem.setEnabled(true);
                            listViewHolder.llListItem.setClickable(true);
                        }
                    } else {
                        listViewHolder.llListItem.setAlpha(0.5f);
                        listViewHolder.llListItem.setEnabled(false);
                        listViewHolder.llListItem.setClickable(false);
                    }

                } else {
                    listViewHolder.llListItem.setAlpha(0.5f);
                    listViewHolder.llListItem.setEnabled(false);
                    listViewHolder.llListItem.setClickable(false);
                }
            }

        }

        listViewHolder.ivIcon.setImageResource(listStorage.get(position).getImageId());

        listViewHolder.tvName.setText(listStorage.get(position).getName());
        listViewHolder.llListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerItemClickListner.OnDrawerItemClicked(position, -1);
            }
        });
        return convertView;
    }

    public class ViewHolder {

        LinearLayout llListItem;
        TextView tvName;
        ImageView ivIcon;

    }
}