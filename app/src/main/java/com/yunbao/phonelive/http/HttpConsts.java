package com.yunbao.phonelive.http;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpConsts {
    public static final String LANGUAGE = "language";
    public static final String GET_MAP_INFO = "getMapInfoByTxSdk";
    public static final String GET_MAP_SEARCH = "searchInfoByTxSdk";
    public static final String GET_LOCAITON = "getLocationByTxSdk";
    public static final String IF_TOKEN = "ifToken";
    public static final String GET_CONFIG = "getConfig";
    public static final String GET_BASE_INFO = "getBaseInfo";
    public static final String LOGIN = "setLoginInfo";
    public static final String LOGIN_BY_THIRD = "loginByThird";
    public static final String LOGIN_BY_CODE = "loginByCode";
    public static final String LOGIN_CHECK_CODE = "checkSmsCode";
    public static final String GET_USER_SHARE_CODE = "getUserShareCode";
    public static final String GET_USER_RANDOM_NAME = "getRandomNickName";
    public static final String GET_QQ_LOGIN_UNION_ID = "getQQLoginUnionID";
    public static final String GET_REGISTER_CODE = "getRegisterCode";
    public static final String REGISTER = "register";
    public static final String FIND_PWD = "findPwd";
    public static final String GET_FIND_PWD_CODE = "getFindPwdCode";
    public static final String GET_HOT = "getHot";
    public static final String GET_FOLLOW = "getFollow";
    public static final String GET_CLASS_LIVE = "getClassLive";
    public static final String GET_NEAR = "getNear";
    public static final String PROFIT_LIST = "profitList";
    public static final String CONSUME_LIST = "consumeList";
    public static final String RANK_RICH_LIST = "rankRichList";
    public static final String SET_ATTENTION = "setAttention";
    public static final String UPDATE_AVATAR = "updateAvatar";
    public static final String UPDATE_FIELDS = "updateFields";
    public static final String GET_FOLLOW_LIST = "getFollowList";
    public static final String GET_FANS_LIST = "getFansList";
    public static final String GET_LIVE_RECORD = "getLiveRecord";
    public static final String GET_SETTING_LIST = "getSettingList";
    public static final String REQUEST_BONUS = "requestBonus";
    public static final String NIUREN_LIST = "niurenList";//牛人榜
    public static final String GET_BONUS = "getBonus";
    public static final String MODIFY_PWD = "modifyPwd";
    public static final String CREATE_ROOM = "createRoom";
    public static final String CHANGE_LIVE = "changeLive";
    public static final String STOP_LIVE = "stopLive";
    public static final String GET_LIVE_END_INFO = "getLiveEndInfo";
    public static final String ROOM_CHARGE = "roomCharge";
    public static final String TIME_CHARGE = "timeCharge";
    public static final String CHECK_LIVE = "checkLive";
    public static final String ENTER_ROOM = "enterRoom";
    public static final String SEND_DANMU = "sendDanmu";
    public static final String GET_GIFT_LIST = "getGiftList";
    public static final String GET_COIN = "getCoin";
    public static final String SEND_GIFT = "sendGift";
    public static final String GET_ALL_IMPRESS = "getAllImpress";
    public static final String GET_MY_IMPRESS = "getMyImpress";
    public static final String SET_IMPRESS = "setImpress";
    public static final String GET_LIVE_USER = "getLiveUser";
    public static final String GET_ADMIN_LIST = "getAdminList";
    public static final String SET_ADMIN = "setAdmin";
    public static final String DOWNLOAD_GIF = "downloadGif";
    public static final String KICKING = "kicking";
    public static final String SET_SHUT_UP = "setShutUp";
    public static final String SUPER_CLOSE_ROOM = "superCloseRoom";
    public static final String GET_USER_HOME = "getUserHome";
    public static final String SET_BLACK = "setBlack";
    public static final String SEARCH_MUSIC = "searchMusic";
    public static final String GET_MUSIC_URL = "getMusicUrl";
    public static final String GET_PROFIT = "getProfit";
    public static final String GET_USER_ACCOUNT_LIST = "GetUserAccountList";
    public static final String ADD_CASH_ACCOUNT = "addCashAccount";
    public static final String DEL_CASH_ACCOUNT = "deleteCashAccount";
    public static final String DO_CASH = "doCash";
    public static final String GET_BALANCE = "getBalance";
    public static final String GET_ALI_ORDER = "getAliOrder";
    public static final String GET_WX_ORDER = "getWxOrder";
    public static final String GET_IM_USER_INFO = "getImUserInfo";
    public static final String CHECK_BLACK = "checkBlack";
    public static final String SEARCH = "search";
    public static final String GET_LINK_MIC_STREAM = "getLinkMicStream";
    public static final String LINK_MIC_SHOW_VIDEO = "linkMicShowVideo";
    public static final String GET_USER_LIST = "getUserList";
    public static final String GET_ALI_CDN_RECORD = "getAliCdnRecord";
    public static final String SET_REPORT = "setReport";
    public static final String GET_RECOMMEND = "getRecommend";
    public static final String RECOMMEND_FOLLOW = "recommendFollow";
    public static final String SET_DISTRIBUT = "setDistribut";
    public static final String GET_GUARD_BUY_LIST = "getGuardBuyList";
    public static final String BUY_GUARD = "buyGuard";
    public static final String GET_GUARD_LIST = "getGuardList";
    public static final String GET_LIVE_PK_LIST = "getLivePkList";
    public static final String LIVE_PK_SEARCH_ANCHOR = "livePkSearchAnchor";
    public static final String LIVE_PK_CHECK_LIVE = "livePkCheckLive";
    public static final String SEND_RED_PACK = "sendRedPack";
    public static final String GET_RED_PACK_LIST = "getRedPackList";
    public static final String ROB_RED_PACK = "robRedPack";
    public static final String GET_RED_PACK_RESULT = "getRedPackResult";
    public static final String GET_SYSTEM_MESSAGE_LIST = "getSystemMessageList";
    public static final String GET_HOME_VIDEO_LIST = "getHomeVideoList";
    public static final String SET_VIDEO_LIKE = "setVideoLike";
    public static final String GET_VIDEO_COMMENT_LIST = "getVideoCommentList";
    public static final String SET_COMMENT_LIKE = "setCommentLike";
    public static final String SET_COMMENT = "setComment";
    public static final String GET_COMMENT_REPLY = "getCommentReply";
    public static final String GET_MUSIC_CLASS_LIST = "getMusicClassList";
    public static final String GET_HOT_MUSIC_LIST = "getHotMusicList";
    public static final String SET_MUSIC_COLLECT = "setMusicCollect";
    public static final String GET_MUSIC_COLLECT_LIST = "getMusicCollectList";
    public static final String GET_MUSIC_LIST = "getMusicList";
    public static final String VIDEO_SEARCH_MUSIC = "videoSearchMusic";
    public static final String GET_QI_NIU_TOKEN = "getQiNiuToken";
    public static final String SAVE_UPLOAD_VIDEO_INFO = "saveUploadVideoInfo";
    public static final String GET_HOME_VIDEO = "getHomeVideo";
    public static final String GET_VIDEO_REPORT_LIST = "getVideoReportList";
    public static final String VIDEO_REPORT = "videoReport";
    public static final String VIDEO_DELETE = "videoDelete";
    public static final String SET_VIDEO_SHARE = "setVideoShare";
    public static final String VIDEO_WATCH_START = "videoWatchStart";
    public static final String VIDEO_WATCH_END = "videoWatchEnd";
    public static final String SET_LINK_MIC_ENABLE = "setLinkMicEnable";
    public static final String CHECK_LINK_MIC_ENABLE = "checkLinkMicEnable";
    public static final String GET_LIVE_REPORT_LIST = "getLiveReportList";
    public static final String GET_ContactList = "pay.getContactList";
    public static final String GET_CHARGERULES = "Infitite.getChargeRules";
    public static final String SET_COINS = "Game.set_coins";

    public static final String GAME_YFKS_LOG = "gameYfksLog";
    public static final String GAME_YF115_LOG = "gameYf15Log";
    public static final String GAME_YFLHC_LOG = "gameYflhcLog";
    public static final String GAME_YFSC_LOG = "gameYfscLog";
    public static final String GAME_YFSSC_LOG = "gameYfsscLog";
    public static final String GAME_YFKLSF_LOG = "gameYfklsfLog";
    public static final String GAME_YFXYNC_LOG = "gameYfxyncLog";

    public static final String GAME_LOG = "getGameLog";
    public static final String USER_LOG = "getUserLog";
    public static final String TZ_INFO = "getTzInfoDetail";
    public static final String USER_LOG_DETAIL = "getUserLogDetail";

    public static final String GAME_YFKS_INFO = "gameYfksInfo";
    public static final String GAME_YFLHC_INFO = "gameYflhcInfo";
    public static final String GAME_YF115_INFO = "gameYf115Info";
    public static final String GAME_YFSC_INFO = "gameYfscInfo";
    public static final String GAME_YFSSC_INFO = "gameYfsscInfo";
    public static final String GAME_YFKLSF_INFO = "gameYfklsfInfo";
    public static final String GAME_YFKLNC_INFO = "gameYfxyncInfo";

    //游戏相关
    public static final String GAME_JINHUA_CREATE = "gameJinhuaCreate";
    public static final String GAME_JINHUA_BET = "gameJinhuaBet";
    public static final String GAME_SETTLE = "gameSettle";
    public static final String GAME_HAIDAO_CREATE = "gameHaidaoCreate";
    public static final String GAME_HAIDAO_BET = "gameHaidaoBet";
    public static final String GAME_NIUZAI_CREATE = "gameNiuzaiCreate";
    public static final String GAME_NIUZAI_BET = "gameNiuzaiBet";
    public static final String GAME_NIU_RECORD = "gameNiuRecord";
    public static final String GAME_NIU_BANKER_WATER = "gameNiuBankerWater";
    public static final String GAME_NIU_GET_BANKER = "gameNiuGetBanker";
    public static final String GAME_NIU_SET_BANKER = "gameNiuSetBanker";
    public static final String GAME_NIU_QUIT_BANKER = "gameNiuQuitBanker";
    public static final String GAME_EBB_CREATE = "gameEbbCreate";
    public static final String GAME_EBB_BET = "gameEbbBet";
    public static final String GAME_LUCK_PAN_CREATE = "gameLuckPanCreate";
    public static final String GAME_LUCK_PAN_BET = "gameLuckPanBet";

    //一分游戏
    public static final String LIVE_GAME_OPTION_DIALOG_FRAGMENT = "LiveGameOptionDialogFragment";
    public static final String LIVE_GAME_TZ_DIALOG_FRAGMENT = "LiveGameTzDialogFragment";
    public static final String LIVE_GAME_TZ_INFO_DIALOG_FRAGMENT = "LiveGameTzInfoDialogFragment";

    public static final String SOCKET_YFKS_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1238";
    public static final String SOCKET_YFLHC_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1242";
    public static final String SOCKET_YF115_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1239";
    public static final String SOCKET_YFSC_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1240";
    public static final String SOCKET_YFSSC_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1241";
    public static final String SOCKET_YFKLSF_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1243";
    public static final String SOCKET_YFKLNC_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1244";

    //投注推送
    public static final String SOCKET_GAME_PUSH_URL = "ws://" + AppConfig.SOCKET_BASE_IP + ":1237";

    //基本信息
    public static final String GET_YFKS_INFO = "Game.get_yfks_info";
    public static final String GET_YFLH_INFO = "Game.get_yflhc_info";
    public static final String GET_YF115_INFO = "Game.get_yf115_info";
    public static final String GET_YFSC_INFO = "Game.get_yfsc_info";
    public static final String GET_YFSSC_INFO = "Game.get_yfssc_info";
    public static final String GET_YFKLSF_INFO = "Game.get_yfklsf_info";
    public static final String GET_YFXYNC_INFO = "Game.get_yfxync_info";
    public static final String GET_COUNT = "Home.getCount";
    public static final String GET_REBATENUM = "Guide.getRebateNum";

    //用户投注
    public static final String TZ_YFKS_LOG = "Game.user_yfks_log";
    public static final String GAME_NAME_YFKS="一分快三";
    public static final String GAME_NAME_YF115="一分11选5";
    public static final String GAME_NAME_YFLHC="一分六合彩";
    public static final String GAME_NAME_YFSC="一分赛车";
    public static final String GAME_NAME_YFSSC="一分时时彩";
    public static final String GAME_NAME_YFKLSF="一分快乐十分";
    public static final String GAME_NAME_YFXYNC="一分幸运农场";

    public static final int GAME_STOP_IN_TIME = 10 ;//封盘时间

    //送礼物界面获取等级
    public static final String GET_GRADE="Home.getGrade";


    public static String getGameNameByType(String gameType){
        if("1".equals(gameType)){
            return GAME_NAME_YFKS ;
        }else if("2".equals(gameType)){
            return GAME_NAME_YF115 ;
        }else if("3".equals(gameType)){
            return GAME_NAME_YFSC ;
        }else if("4".equals(gameType)){
            return GAME_NAME_YFSSC ;
        }else if("5".equals(gameType)){
           return GAME_NAME_YFLHC ;
        }else if("6".equals(gameType)){
            return GAME_NAME_YFKLSF ;
        }else if("7".equals(gameType)){
            return GAME_NAME_YFXYNC ;
        }else{
            return GAME_NAME_YFKS ;
        }
    }

    public static int getGameIconByType(String gameType){
        if("1".equals(gameType)){
            return R.mipmap.icon_ks_game ;
        }else if("2".equals(gameType)){
            return R.mipmap.icon_syxw_game ;
        }else if("3".equals(gameType)){
            return R.mipmap.icon_syf_game ;
        }else if("4".equals(gameType)){
            return R.mipmap.icon_ssc_game ;
        }else if("5".equals(gameType)){
            return R.mipmap.icon_yfl_game ;
        }else if("6".equals(gameType)){
            return R.mipmap.icon_ten_game ;
        }else if("7".equals(gameType)){
            return R.mipmap.icon_xfnc_game ;
        }else{
            return R.mipmap.icon_ks_game ;
        }
    }

    /**
     * 获取游戏结果socket地址
     * @param gameType type
     * @return socket url
     */
    public static String getGameResultUrlByType(String gameType){
        String url ;
        switch (gameType){
            case "1":
                url = SOCKET_YFKS_URL ;
                break;
            case "2":
                url = SOCKET_YF115_URL;
                break;
            case "3":
                url = SOCKET_YFSC_URL;
                break;
            case "4":
                url = SOCKET_YFSSC_URL;
                break;
            case "5":
                url = SOCKET_YFLHC_URL;
                break;
            case "6":
                url = SOCKET_YFKLSF_URL;
                break;
            case "7":
                url = SOCKET_YFKLNC_URL;
                break;
            default:
                url = SOCKET_YFKS_URL ;
                break;
        }

        return url ;
    }


    /**
     * 根据游戏类型和玩法id，获取玩法名称
     * @param gameId gameId
     * @param typeId typeId
     * @return name
     */
    public static String getGameTypeNameById(String gameId,String typeId){
        String typeName = null ;

        if("1".equals(gameId)){//一分快三
            if("1".equals(typeId)){
                typeName = "总和" ;
            }else if("2".equals(typeId)){
                typeName = "三军" ;
            }else if("3".equals(typeId)){
                typeName = "短牌" ;
            }
        } else if("2".equals(gameId)){//一分11选5
            if("1".equals(typeId)){
                typeName = "总和" ;
            }else if("2".equals(typeId)){
                typeName = "第一球两面" ;
            }else if("3".equals(typeId)){
                typeName = "全五中一" ;
            }
        } else if("3".equals(gameId)){//一分赛车
            if("1".equals(typeId)){
                typeName = "冠军单码" ;
            }else if("2".equals(typeId)){
                typeName = "冠亚和" ;
            }else if("3".equals(typeId)){
                typeName = "冠军两面" ;
            }
        } else if("4".equals(gameId)){//一分时时彩
            if("1".equals(typeId)){
                typeName = "第一球两面" ;
            }else if("2".equals(typeId)){
                typeName = "第一球VS第五球" ;
            }else if("3".equals(typeId)){
                typeName = "全5中1" ;
            }
        } else if("5".equals(gameId)){//一分六合彩
            if("1".equals(typeId)){
                typeName = "特码两面" ;
            }else if("2".equals(typeId)){
                typeName = "特码生肖" ;
            }else if("3".equals(typeId)){
                typeName = "特码色波" ;
            }
        } else if("6".equals(gameId)){//一分快乐十分
            if("1".equals(typeId)){
                typeName = "总和" ;
            }else if("2".equals(typeId)){
                typeName = "第一球两面" ;
            }else if("3".equals(typeId)){
                typeName = "第八球两面" ;
            }
        } else if("7".equals(gameId)){//一分幸运农场
            if("1".equals(typeId)){
                typeName = "总和" ;
            }else if("2".equals(typeId)){
                typeName = "第一球两面" ;
            }else if("3".equals(typeId)){
                typeName = "第八球两面" ;
            }
        }

        return null == typeName ? "未知" : typeName ;
    }


    public static int getGameRuleByType(String gameType){
        if("1".equals(gameType)){
            return R.string.game_rule_cp_ks;
        }else if("2".equals(gameType)){
            return R.string.game_rule_cp_115;
        }else if("3".equals(gameType)){
            return R.string.game_rule_cp_sc;
        }else if("4".equals(gameType)){
            return R.string.game_rule_cp_ssc;
        }else if("5".equals(gameType)){
            return R.string.game_rule_cp_lhc;
        }else if("6".equals(gameType)){
            return R.string.game_rule_cp_klsf;
        }else if("7".equals(gameType)){
            return R.string.game_rule_cp_xync;
        }else{
            return R.string.game_rule_cp_ks;
        }
    }



}
