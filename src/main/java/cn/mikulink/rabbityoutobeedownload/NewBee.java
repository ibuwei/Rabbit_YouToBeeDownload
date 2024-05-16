package cn.mikulink.rabbityoutobeedownload;

import cn.hutool.http.*;
import cn.mikulink.rabbityoutobeedownload.util.DownloadUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * created by MikuNyanya on 2024/5/8 21:23
 * For the Reisen
 * 新蜜蜂 牛【哔————】
 */
public class NewBee {
    public static void main(String[] args) {
        //油管视频链接
        String youtobeeUrl = "https://www.youtube.com/watch?v=ygY2qObZv24&list=RDygY2qObZv24&index=1";
        //本地保存路径
        String localPath = "D:/";
        //代理 不需要代理传null即可
        String proxyUrl = "localhost";
        int port = 30001;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, port));

        try {
            //下载视频
            String downloadFileLocalPath = start(youtobeeUrl, localPath, proxy);

            System.out.println("---END---");
            System.out.println("文件下载至:" + downloadFileLocalPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    static String start(String youtobeeUrl, String localPath, Proxy proxy) throws IOException {

        //解析视频信息
        JSONObject videoListInfo = getVideoList(youtobeeUrl, proxy);
        JSONObject links = JSONObject.parseObject(videoListInfo.get("links").toString());
        JSONObject videoList = JSONObject.parseObject(links.get("mp4").toString());

        //获取1080p视频 如果没有则获取720p
        String downloadKey = parseVideoKey(videoList, "1080p");
        if (null == downloadKey) {
            downloadKey = parseVideoKey(videoList, "720p");
        }

        //获取下载链接
        String vid = videoListInfo.get("vid").toString();
        JSONObject downloadLinkJson = getDownloadLink(vid, downloadKey, proxy);
        String downloadLink = downloadLinkJson.get("dlink").toString();
        String title = downloadLinkJson.get("title").toString();
        String ftype = downloadLinkJson.get("ftype").toString();
        String fquality = downloadLinkJson.get("fquality").toString();

        //针对windows文件名禁止字符处理
        String tags = "\\\\/:*?\"<>|";
        for (int i = 0; i < tags.length(); i++) {
            String rex = String.valueOf(tags.charAt(i));
            if (title.contains(rex)) {
                title = title.replaceAll(rex, "");
            }
        }

        //文件名称
        String fileName = String.format("%s_%sp.%s", title, fquality, ftype);

        //下载视频
        return DownloadUtil.downloadFile(null, downloadLink, localPath, fileName, proxy, true);

    }

    static JSONObject getVideoList(String youtobeeUrl, Proxy proxy) {
        //获取视频解析列表
        String url = "https://www.y2mate.com/mates/analyzeV2/ajax";
        Map<String, Object> param = new HashMap<>();
        param.put("k_query", youtobeeUrl);
        param.put("k_page", "home");
        param.put("hl", "en");
        param.put("q_auto", "1");

        HttpRequest httpRequest = HttpUtil.createPost(url);
        httpRequest.contentType(ContentType.MULTIPART.getValue());
        httpRequest.setProxy(proxy);

        HttpResponse response = httpRequest.timeout(HttpGlobalConfig.getTimeout()).form(param).execute();
        String body = response.body();

//            String body = "{\"status\":\"ok\",\"mess\":\"\",\"page\":\"detail\",\"vid\":\"e9dZQelULDk\",\"extractor\":\"youtube\",\"title\":\"Happiness\",\"t\":256,\"a\":\"Steve Cutts\",\"links\":{\"mp4\":{\"137\":{\"size\":\"108.9 MB\",\"f\":\"mp4\",\"q\":\"1080p\",\"q_text\":\"1080p (.mp4) <span class=\\\"label label-primary\\\"><small>HD<\\/small><\\/span>\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxyUfMcutx\\/hKr5c4ceff9Bnt6+HM4A6W+LJg==\"},\"22\":{\"size\":\"37.8 MB\",\"f\":\"mp4\",\"q\":\"720p\",\"q_text\":\"720p (.mp4) <span class=\\\"label label-primary\\\"><small>m-HD<\\/small><\\/span>\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwx0U\\/tQ9918mb3wbZgcdulcmZavAYpIp2w=\"},\"135\":{\"size\":\"21.1 MB\",\"f\":\"mp4\",\"q\":\"480p\",\"q_text\":\"480p (.mp4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwx3WftQ9918mb3wbZgcdulcmZavAYpIpG2N\"},\"18\":{\"size\":\"14.2 MB\",\"f\":\"mp4\",\"q\":\"360p\",\"q_text\":\"360p (.mp4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxwV\\/tQ9918mb3wbZgcdulcmZavAYpIpGY=\"},\"133\":{\"size\":\"8.9 MB\",\"f\":\"mp4\",\"q\":\"240p\",\"q_text\":\"240p (.mp4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxxVftcutx\\/hKr5c4ceff9Bnt6+HM4A6W+LIg==\"},\"160\":{\"size\":\"6.3 MB\",\"f\":\"mp4\",\"q\":\"144p\",\"q_text\":\"144p (.mp4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxyVf9cutx\\/hKr5c4ceff9Bnt6+HM4A6W+OIQ==\"},\"3gp@144p\":{\"size\":\"MB\",\"f\":\"3gp\",\"q\":\"144p\",\"q_text\":\"144p (.3gp)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghIY91wxyVf9cutx\\/hKr5c4ceff9Bnt6+HM4A6W3fYfDaZFuZ\"},\"auto\":{\"size\":\"\",\"f\":\"mp4\",\"q\":\"auto\",\"selected\":\"selected\",\"q_text\":\"MP4 auto quality\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwwiFL9Dutx\\/hKr5c4ceff9Bnt4=\"}},\"mp3\":{\"140\":{\"size\":\"4 MB\",\"f\":\"m4a\",\"q\":\".m4a\",\"q_text\":\".m4a (128kbps)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNhuxgxyU\\/NQ9918mb3wbZgcdulcmZavAY1IpGqI\"},\"mp3128\":{\"size\":\"4 MB\",\"f\":\"mp3\",\"q\":\"128kbps\",\"q_text\":\"MP3 - 128kbps\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqlAxyU\\/NQ9918mb3wbZgcdulcmQ==\"}},\"other\":{\"249\":{\"size\":\"1.6 MB\",\"f\":\"webm\",\"q\":\"Audio\",\"q_text\":\"Audio .webm (48kbps)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/VfNQ9918mb3wbZgcdulcmZatBdZR5yKKJYk=\"},\"250\":{\"size\":\"2 MB\",\"f\":\"webm\",\"q\":\"Audio\",\"q_text\":\"Audio .webm (64kbps)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/V\\/9Q9918mb3wbZgcdulcmZatBdZR5yKKJIA=\"},\"251\":{\"size\":\"4 MB\",\"f\":\"webm\",\"q\":\"Audio\",\"q_text\":\"Audio .webm (160kbps)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/UP0cutx\\/hKr5c4ceff9Bnt6+Hspc8CzEI4Xa\"},\"598\":{\"size\":\"825.1 KB\",\"f\":\"webm\",\"q\":\"Video\",\"q_text\":\"Video only 144p (WEBM)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/UP8Y5p0zhrTzbZAfYPZclcTxRcJb4TbdY8zeaVc=\"},\"133\":{\"size\":\"8.9 MB\",\"f\":\"mp4\",\"q\":\"Video\",\"q_text\":\"Video only 240P (MP4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxxVftcutx\\/hKr5c4ceff9Bnt6+Hspc8CzEIIPY\"},\"242\":{\"size\":\"5.4 MB\",\"f\":\"webm\",\"q\":\"Video\",\"q_text\":\"Video only 240P  (WEBM)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/U\\/8ctpF+h7fuepkBf\\/RXg9n2DdFA\\/TvKbYLfYg==\"},\"134\":{\"size\":\"14.2 MB\",\"f\":\"mp4\",\"q\":\"Video\",\"q_text\":\"Video only 360P (MP4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxwV\\/tcutx\\/hKr5c4ceff9Bnt6+Hspc8CzEIIPf\"},\"243\":{\"size\":\"10.1 MB\",\"f\":\"webm\",\"q\":\"Video\",\"q_text\":\"Video only 360P  (WEBM)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/Uv0ctpF+h7fuepkBf\\/RXg9n2DdFA\\/TvKbYLfYw==\"},\"135\":{\"size\":\"21.1 MB\",\"f\":\"mp4\",\"q\":\"Video\",\"q_text\":\"Video only 480P (MP4)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwx3WftQ9918mb3wbZgcdulcmZatBdZR5yKJIoU=\"},\"244\":{\"size\":\"17.8 MB\",\"f\":\"webm\",\"q\":\"Video\",\"q_text\":\"Video only 480P  (WEBM)\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/VfMctpF+h7fuepkBf\\/RXg9n2DdFA\\/TvKbYLfZA==\"},\"137\":{\"size\":\"108.9 MB\",\"f\":\"mp4\",\"q\":\"Video\",\"q_text\":\"Video only (MP4)<span class=\\\"label label-primary\\\"><small>1K<\\/small><\\/span>\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghNgqkwxyUfMcutx\\/hKr5c4ceff9Bnt6+Hspc8CzEIIPc\"},\"248\":{\"size\":\"62.3 MB\",\"f\":\"webm\",\"q\":\"Video\",\"q_text\":\"Video only (WEBM)<span class=\\\"label label-primary\\\"><small>1K<\\/small><\\/span>\",\"k\":\"joAQXNe0zpP3a8X07afYqcH+Al7j4qoghMI\\/xR0\\/UPsU9pF+h7fuepkBf\\/RXg9n2DdFA\\/TvKbYLfaA==\"}}},\"related\":[{\"title\":\"Related Videos\",\"contents\":[]}]}";
        return JSONObject.parseObject(body);

    }

    static String parseVideoKey(JSONObject videoList, String p) {
        //获取指定分辨率的视频key
        String downloadKey = null;
        for (String key : videoList.keySet()) {
            JSONObject video = JSONObject.parseObject(videoList.get(key).toString());

            if (video.get("q").equals(p)) {
                downloadKey = video.get("k").toString();
                break;
            }
        }
        return downloadKey;
    }

    static JSONObject getDownloadLink(String vid, String videoKey, Proxy proxy) {

        //根据获取到的下载key，获取下载链接
        String url = "https://www.y2mate.com/mates/convertV2/index";
        Map<String, Object> param = new HashMap<>();
        param.put("vid", vid);
        param.put("k", videoKey);

        HttpRequest httpRequest = HttpUtil.createPost(url);
        httpRequest.contentType(ContentType.MULTIPART.getValue());
        httpRequest.setProxy(proxy);

        HttpResponse response = httpRequest.timeout(HttpGlobalConfig.getTimeout()).form(param).execute();
        String body = response.body();

        return JSONObject.parseObject(body);
    }

}
