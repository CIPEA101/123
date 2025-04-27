package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.dialog.LiveGameOptionDialogFragment;
import com.yunbao.phonelive.game.GameBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.socket.JWebSocketClient;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.Vh> {

    private List<GameBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private String gameId;
    private JWebSocketClient client;

    public GameAdapter(Context context, LayoutInflater inflater, List<GameBean> list) {
        this.mContext = context;
        mInflater = inflater;
        mList = list;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_game_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) { }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void release() {
        if (mList != null) {
            mList.clear();
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.iv_game);
            mName =  itemView.findViewById(R.id.tv_game);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    switch (getAdapterPosition()){
//                        case 0:
//                            getYfSocket(HttpConsts.SOCKET_YFKS_URL);
//                            break;
//                        case 1:
//                            getYfSocket(HttpConsts.SOCKET_YF115_URL);
//                            break;
//                        case 2:
//                            getYfSocket(HttpConsts.SOCKET_YFSC_URL);
//                            break;
//                        case 3:
//                            getYfSocket(HttpConsts.SOCKET_YFSSC_URL);
//                            break;
//                        case 4:
//                            getYfSocket(HttpConsts.SOCKET_YFLHC_URL);
//                            break;
//                        case 5:
//                            getYfSocket(HttpConsts.SOCKET_YFKLSF_URL);
//                            break;
//                        case 6:
//                            getYfSocket(HttpConsts.SOCKET_YFKLNC_URL);
//                            break;
//                    }
                    LiveGameOptionDialogFragment fragment = new LiveGameOptionDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.LIVE_GAME_NAME, mList.get(getAdapterPosition()).getName());
                    bundle.putInt(Constants.LIVE_GAME_IGAMEURL, mList.get(getAdapterPosition()).getImageId());
                    fragment.setArguments(bundle);
                    List<Fragment> fragments = ((LiveActivity) mContext).getSupportFragmentManager().getFragments();
                    for (Fragment fragment1 : fragments){
                        ((LiveActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment1).commit();
                    }
                    fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), HttpConsts.LIVE_GAME_OPTION_DIALOG_FRAGMENT);
                }
            });
        }

        void setData(GameBean bean, Object payload) {
            if (payload == null) {
                ImgLoader.display(bean.getImageId(), mIcon);
                bean.setView(mIcon);
                mName.setText(bean.getName());
            }
        }
    }
//
//    private void getYfSocket(final String url){
//        URI uri = URI.create(url);
//        if (client == null) {
//            client = new JWebSocketClient(uri) {
//                @Override
//                public void onMessage(String message) {
//                    super.onMessage(message);
//
//                    Log.e(GameAdapter.class.getSimpleName(), message);
//
//                    final SocketGameReceiveBean scrb = GsonUtil.fromJson(message, SocketGameReceiveBean.class);
//
//                    if (scrb != null) {
//                        String dianshu = scrb.getDianshu_result();
//                        final String[] strArr = dianshu.split(",");
//                        List<Fragment> fragments = ((LiveActivity) mContext).getSupportFragmentManager().getFragments();
//                        if (fragments != null && fragments.size() > 0) {
//                            for (final Fragment fragment : fragments) {
//                                if (fragment.getTag().equals(HttpConsts.LIVE_GAME_OPTION_DIALOG_FRAGMENT)) {
//                                    ((LiveActivity) mContext).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            switch (url) {
//                                                case HttpConsts.SOCKET_YFSC_URL:
//                                                    gameId = scrb.getNew_yfsc_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYfscLastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                                case HttpConsts.SOCKET_YFKS_URL:
//                                                    gameId = scrb.getNew_yfks_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYfksLastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                                case HttpConsts.SOCKET_YFLHC_URL:
//                                                    gameId = scrb.getNew_yflhc_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYflhcLastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                                case HttpConsts.SOCKET_YF115_URL:
//                                                    gameId = scrb.getNew_yf115_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYf115LastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                                case HttpConsts.SOCKET_YFSSC_URL:
//                                                    gameId = scrb.getNew_yfssc_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYf115LastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                                case HttpConsts.SOCKET_YFKLSF_URL:
//                                                    gameId = scrb.getNew_yfklsf_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYf115LastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                                case HttpConsts.SOCKET_YFKLNC_URL:
//                                                    gameId = scrb.getNew_yfxync_game_id();
//                                                    ((LiveGameOptionDialogFragment) fragment).initYfklncLastAward(client, strArr, scrb.getTime(), gameId);
//                                                    break;
//                                            }
//                                        }
//                                    });
//                                } else if (fragment.getTag().equals(HttpConsts.LIVE_GAME_TZ_DIALOG_FRAGMENT)) {
//                                    ((LiveGameTzDialogFragment) fragment).initParam(gameId, Long.parseLong(scrb.getTime()));
//                                }
//                            }
//                        }
//                    }
//                }
//            };
//
//            client.connect();
//        }
//    }
}
