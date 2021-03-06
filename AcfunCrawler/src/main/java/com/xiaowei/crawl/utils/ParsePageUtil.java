package com.xiaowei.crawl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.xiaowei.crawl.entity.VideoInfo;
import com.xiaowei.crawl.factory.LoggingFactory;
import com.xiaowei.crawl.factory.MySqlSessionFactory;
import org.apache.http.util.TextUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * @author tangjingya  吴晓伟
 * Desc:包含
 * 1.获取视频信息工具
 * 2.找到ts文件并合并的工具
 * 3.合并二级制文件工具
 */
public class ParsePageUtil {
    public static void main(String[] args) throws InterruptedException, IOException {

//        VideoInfo a=getFileDownloadUrls("https://m.acfun.cn/v/?ac=14134176");
//        System.out.println(a);


        List<Thread> tasks = new LinkedList<Thread>();
        String res = HttpRequest.sendGet("https://tx-safety-video.acfun.cn/mediacloud/acfun/acfun_video/segment/nHc9-Eaglr3iybF9AAyrU-IZGvdRJIJC1Mlmz2GQ-ICHlI_ShUqwn9L94F_34A7j.m3u8?pkey=AAIeAzNb1-IaeygAR-MYgLkBWMwNoitf0v64iTtD9eohKZXUtOpWJkyGTgARYYaxbaAXPKfmpz-iiN51xSgQWyolvjOI4XQ4W8etFZ3agTkFfyARO5MvZFGw_6b08_zeGLPzT-oRiZL8uLHV4sEwMBtfsxLN_25g-tc4YlMLb5g9T3VLDm6I3o4WxT-xTdb9pN33GQ05xL_pFJHtijwifdKLnTa1-F0L09cUbP56VgMjxg&safety_id=AALiHMNr8NZQFMcXpIGj-KRo");

        String[] text = res.split(",");

        for (int i = 1; i < text.length; i++) {
            String video_chip = text[i].split("#")[0];
            final String api = "https://tx-safety-video.acfun.cn/mediacloud/acfun/acfun_video/segment/" + video_chip;

            final int finalI = i;
            Thread a = new Thread(new Runnable() {
                public void run() {
                    System.out.println(finalI);
                    FileUrlDownloadUtil.downloadFile(api, "F:/study_project/webpack/scrapy/acfun_video", "/" + String.format("%04d", finalI) + ".ts", "GET");
                }
            });
            a.start();
            tasks.add(a);
        }
        for (Thread item : tasks) {
            item.join();
        }

        mergeDownloadFiles("F:\\study_project\\webpack\\scrapy\\acfun_video\\", "aaaa.mp4");


        System.out.println("主线程结束啦！");

    }

    /**
     * @param url 输入文件ac号的地址 例如 https://www.acfun.cn/v/ac100000000001
     * @return VideoInfo 包含视频全部信息 主要包含了3-4个m3u8的文件下载地址
     * desc 输入url返回一个VideoInfo视频对象
     * @author tangyajing 吴晓伟
     */
    public static VideoInfo getFileDownloadUrls(String url) {
        LinkedList<String> m3u8_urls = new LinkedList<String>();

        StringBuffer stringBuffer = new StringBuffer(HttpRequest.sendGet(url));

        int start = stringBuffer.indexOf("window.videoInfo =");
        int end = stringBuffer.indexOf("window.qualityConfig =");
        int target = stringBuffer.lastIndexOf(";", end);
        String jsonStr = stringBuffer.substring(start + "window.videoInfo =".length(), target);

        JSONObject jsonObj = (JSONObject) JSON.parse(jsonStr);

        JSONObject currentVideoInfo = (JSONObject) JSON.toJSON(jsonObj.get("currentVideoInfo"));

        JSONObject ksPlayJson = (JSONObject) JSON.parse(currentVideoInfo.get("ksPlayJson").toString());

        JSONObject adaptationSet = (JSONObject) JSON.toJSON(ksPlayJson.get("adaptationSet"));

        JSONArray representation = (JSONArray) adaptationSet.get("representation");

        //额外需求
        // 用户id
        JSONObject user = (JSONObject) JSON.toJSON(jsonObj.get("user"));

        // 分类
        JSONObject channel = (JSONObject) JSON.toJSON(jsonObj.get("channel"));
        String classes = channel.get("parentName").toString() + channel.get("name").toString();

        //整理m3u8文件列表
        for (Object item : representation) {
            JSONObject json = (JSONObject) JSON.toJSON(item);
            String m3u8_url = (String) json.get("url");
            m3u8_urls.add(m3u8_url);
            System.out.println(m3u8_url);
        }

        //整理tag列表。
        JSONArray tags = (JSONArray) jsonObj.get("tagList");
        ArrayList<String> tagList = new ArrayList<String>();
        if (tags != null) {
            for (Object tag : tags) {
                JSONObject t = (JSONObject) JSON.toJSON(tag);
                tagList.add(t.get("name").toString());
            }
        }

        //获取分p列表
        JSONArray videos = (JSONArray) jsonObj.get("videoList");
        List<JSONObject> videoList = new LinkedList<JSONObject>();
        for (Object video : videos) {
            JSONObject t = (JSONObject) JSON.toJSON(video);
            JSONObject obj = new JSONObject();
            obj.put("title", t.get("title").toString());
            obj.put("id", t.get("id").toString());
            videoList.add(obj);

        }

        VideoInfo videoInfo = new VideoInfo(jsonObj.get("title").toString(),
                jsonObj.get("coverUrl").toString(),
                currentVideoInfo.get("durationMillis").toString(),
                jsonObj.get("shareCount").toString(),
                jsonObj.get("danmakuCount").toString(),
                jsonObj.get("viewCount").toString(),
                jsonObj.get("bananaCount").toString(),
                jsonObj.get("likeCount").toString(),
                jsonObj.get("giftPeachCount").toString(),
                jsonObj.get("commentCount").toString(),
                jsonObj.get("createTimeMillis").toString(),
                jsonObj.get("stowCount").toString(),
                jsonObj.get("currentVideoId").toString(),
                user.get("name").toString(),
                classes,
                videoList,
                tagList,
                m3u8_urls,
                "F:/"

        );


        return videoInfo;
    }

    public static void saveDate(VideoInfo videoInfo) {
        StringBuilder tags = new StringBuilder();

        if (videoInfo.tagList != null && !videoInfo.tagList.isEmpty()) {
            for (String a : videoInfo.tagList) {
                tags.append(a);
            }
        } else {
            tags.append("----");
        }

        try {
            MySqlSessionFactory.getInstance().addSqlToAcfunInfo(videoInfo.title,
                    videoInfo.coverUrl, videoInfo.durationMillis, videoInfo.currentVideoId, videoInfo.viewCount, videoInfo.commentCount,
                    videoInfo.user, videoInfo.bananaCount, videoInfo.createTimeMillis, videoInfo.likeCount, videoInfo.giftPeachCount, videoInfo.stowCount, videoInfo.shareCount, videoInfo.danmakuCount, tags.toString(), videoInfo.classes, "F:/");
        } catch (SQLException e) {
            System.out.println("数据库读写出现错误！");
            e.printStackTrace();
        }
    }

    /**
     * @param fileContent 待文件目录
     * @param fileName    合并到的目标文件
     * @return boolean 合并成功？
     * desc 找到并合并ts文件
     * @author 吴晓伟
     */
    public static void mergeDownloadFiles(String fileContent, String fileName) {
        File file = new File(fileContent);
        File[] fileList = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(".ts");
            }
        });
        String[] files = new String[fileList.length];
        for (int i = 0; i < fileList.length; i++) {
            files[i] = fileList[i].getPath();
            System.out.println(files[i]);
        }
        String fullName = fileContent + "/" + fileName;
        if (fileContent.endsWith("/") || fileContent.endsWith("\\")) {
            fullName = fileContent + fileName;
        }
        boolean success = mergeFiles(files, fullName);
        if (!success) {
            System.out.println("视频合并失败！");
        }
        LoggingFactory.warning("视频合并完成;" + fullName);
        System.out.println("视频合并完成;" + fullName);
    }

    /**
     * @param fpaths     待文件列表
     * @param resultPath 合并到的目标文件
     * @return boolean 合并成功？
     * desc  合并视频文件
     * @author 吴晓伟
     */
    public static boolean mergeFiles(String[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1 || TextUtils.isEmpty(resultPath)) {
            return false;
        }
        if (fpaths.length == 1) {
            return new File(fpaths[0]).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.length];
        for (int i = 0; i < fpaths.length; i++) {
            files[i] = new File(fpaths[i]);
            if (TextUtils.isEmpty(fpaths[i]) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);
        if(resultFile.exists()){
            return true;
        }

        try {
            int bufSize = 1024;
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(resultFile));
            byte[] buffer = new byte[bufSize];

            for (int i = 0; i < fpaths.length; i++) {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(files[i]));
                int readcount;
                while ((readcount = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readcount);
                }
                inputStream.close();
            }
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < fpaths.length; i++) {
            files[i].delete();
        }

        return true;
    }
}
