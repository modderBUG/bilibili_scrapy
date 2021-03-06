# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy

class VideoInfoItem(scrapy.Item):
    rank = scrapy.Field()
    img_url = scrapy.Field()
    name = scrapy.Field()
    href = scrapy.Field()
    view_counts = scrapy.Field()
    review = scrapy.Field()
    author = scrapy.Field()
    score = scrapy.Field()

    pub_time = scrapy.Field()
    like = scrapy.Field()
    coins = scrapy.Field()
    favorite = scrapy.Field()
    forward = scrapy.Field()
    barrage = scrapy.Field()
    tags = scrapy.Field()
    classes = scrapy.Field()

    file_content=scrapy.Field()
    bvid = scrapy.Field()
    avid=scrapy.Field()
    cid=scrapy.Field()
    view=scrapy.Field()
    reply=scrapy.Field()
    part=scrapy.Field()
    pages=scrapy.Field()
    pages_list=scrapy.Field()
    title=scrapy.Field()

class UserVideoInfoItem(scrapy.Item):

    title=scrapy.Field()
    img_url = scrapy.Field()
    author = scrapy.Field()
    pub_time = scrapy.Field()
    like = scrapy.Field()
    coins = scrapy.Field()
    favorite = scrapy.Field()
    forward = scrapy.Field()
    barrage = scrapy.Field()
    tags = scrapy.Field()
    classes = scrapy.Field()

    file_content=scrapy.Field()
    bvid = scrapy.Field()
    avid=scrapy.Field()
    cid=scrapy.Field()
    view=scrapy.Field()
    reply=scrapy.Field()
    part=scrapy.Field()
    
    pages=scrapy.Field()
    pages_list=scrapy.Field()
    
class BilibiliItem(scrapy.Item):
    rank = scrapy.Field()
    img_url = scrapy.Field()
    name = scrapy.Field()
    href = scrapy.Field()
    view_counts = scrapy.Field()
    review = scrapy.Field()
    author = scrapy.Field()
    score = scrapy.Field()

    pub_time = scrapy.Field()
    like = scrapy.Field()
    coins = scrapy.Field()
    favorite = scrapy.Field()
    forward = scrapy.Field()
    barrage = scrapy.Field()
    tags = scrapy.Field()
    classes = scrapy.Field()

    file_content=scrapy.Field()
    bvid = scrapy.Field()
    avid=scrapy.Field()
    cid=scrapy.Field()
