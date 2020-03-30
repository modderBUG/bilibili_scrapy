package com.xiaowei.crawl.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User：modderBUG
 * Date：2020/3/3012:27
 * Version:1.0
 * Desc:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoInfo {
    public String title;
    public String shareCount;      //分享
    public String danmakuCount;         //弹幕
    public String viewCount;            //访问
    public String bananaCount;
    public String likeCount;
    public String giftPeachCount;
    public String commentCount;
    public String createTime;
    public String stowCount;

    public List<JSONObject> videoList;                //分P列表
    public List<String> tagList;                  //列表

    public LinkedList<String> urls;                  //列表

}
