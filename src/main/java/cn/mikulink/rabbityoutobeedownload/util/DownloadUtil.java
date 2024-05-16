package cn.mikulink.rabbityoutobeedownload.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * create by MikuLink on 2019/12/12 17:38
 * for the Reisen
 */
public class DownloadUtil {

    public static String downloadFile(HashMap<String, String> header, String webFileUrl, String localUrl, String fileName, Proxy proxy, boolean print) throws IOException {
        if (StringUtil.isEmpty(webFileUrl)) {
            throw new IOException("链接为空");
        }

        //类型根据创建连接
        HttpURLConnection conn = null;
        if (webFileUrl.startsWith("https")) {
            conn = HttpsUtil.getHttpsURLConnection(webFileUrl, HttpUtil.REQUEST_METHOD_GET, header, proxy);
        } else {
            conn = HttpUtil.getHttpURLConnection(webFileUrl, HttpUtil.REQUEST_METHOD_GET, proxy);
        }

        //设置链接超时时间
//        conn.setConnectTimeout(5 * 1000);
        //请求header
        if (null != header) {
            for (String key : header.keySet()) {
                conn.setRequestProperty(key, header.get(key));
            }
        }

        //获取文件大小
        Long fileLength = conn.getContentLengthLong();
        String fileLengthMB = String.valueOf(fileLength / 1024 / 1024);
        if (print) {
            System.out.println("文件大小 " + fileLength + "(" + fileLengthMB + "MB)");
        }

        //获取输出流
        InputStream inStream = conn.getInputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        long downloadLength = 0L;
        long outInterval = 1000L;
        long time = System.currentTimeMillis();
        DecimalFormat dFormat = new DecimalFormat(".00");

        while (true) {
            len = inStream.read(buffer);
            downloadLength += len;

            //控制台输出进度
            if (print) {
                //指定间隔输出一次
                if ((time + outInterval < System.currentTimeMillis())
                        | len == -1) {

                    String d = dFormat.format(downloadLength * 1.0 / fileLength * 100);

                    System.out.printf("[%s] %sMB/%sMB(%s%%)%n",
                            fileName,
                            downloadLength / 1024 / 1024,
                            fileLengthMB,
                            d);

                    time = System.currentTimeMillis();
                }
            }
            if (len == -1) {
                break;
            }

            outStream.write(buffer, 0, len);
        }

        inStream.close();
        //写入内存
        byte[] data = outStream.toByteArray();

        //创建本地文件
        File result = new File(localUrl);
        if (!result.exists()) {
            result.mkdirs();
        }

        //如果为空，使用网络图片的名称和后缀
        if (StringUtil.isEmpty(fileName)) {
            fileName = webFileUrl.substring(webFileUrl.lastIndexOf("/") + 1);
        }

        //写入文件数据
        String fileFullName = result + File.separator + fileName;
        FileOutputStream fileOutputStream = new FileOutputStream(fileFullName);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();

        //返回文件路径
        return fileFullName;
    }

}
