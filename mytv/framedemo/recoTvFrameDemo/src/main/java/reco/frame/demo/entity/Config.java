package reco.frame.demo.entity;


public class Config {
    static public String http = "";

    static public void init(String ip, String port1) {
        url = "http://" + ip + ":" + port1 + "/index.php/";
        imgurl = "http://" + ip + ":" + port1 + "/";
        video = "http://" + ip + ":" + port1 + "/videos/";
        login = url + "Home/App/login";
        gettongzhi = url + "Home/App/gettongzhi";
        getmenu = url + "Home/App/getmenu";
        gettv = url + "Home/App/gettv";
        addlog = url + "Home/App/addlog";
        kaihuicheck = url + "Home/App/kaihuicheck";
        version = url + "Home/App/version";
        getvideos = url + "Home/App/getvideos";
        file = imgurl + "Content/images/folder.png";
        getapp = url + "Home/App/getapp";
        getfengcai = url + "Home/App/getfengcai";
        fengcaiview = url + "Home/App/fengcaiview";
    }

    static public String url = "";
    static public String imgurl = "";
    static public String login = "";
    static public String gettongzhi = "";
    static public String getmenu = "";
    static public String gettv = "";
    static public String addlog = "";
    static public String kaihuicheck = "";
    static public String huanying = "欢迎您";
    static public String news = "http://222.43.239.226:7000/index.ashx";
    static public String newsview = "http://222.43.239.226:7000/news.aspx";
    static public String version = "";
    static public String getvideos = "";
    static public String file = "";
    static public String video = "";
    static public String getapp = "";
    static public String getfengcai = "";
    static public String fengcaiview = "";
    static public String Socket_URL = "";


}
