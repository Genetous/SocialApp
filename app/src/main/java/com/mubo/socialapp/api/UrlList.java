package com.mubo.socialapp.api;

public class UrlList {
    static String ip="http://192.168.0.25";
    public static String token_client=ip+":5008/client";
    public static String token_auth=ip+":5008/auth";
    public static String token_verify=ip+":5008/verify";
    public static String token_logout=ip+":5008/logout";
    public static String main_url=ip+":5004/";
    public static String upload_main_url=ip+":5002/";
    public static String create_unique_collection=main_url+"add/unique/collection";
    public static String add_collection=main_url+"add/collection";
    public static String update_collection=main_url+"update/collection";
    public static String delete_collection=main_url+"delete/collection";
    public static String add_relation=main_url+"add/relation";
    public static String get_collection=main_url+"get/collection";
    public static String select_relation=main_url+"select/relation";
    public static String get_relations=main_url+"get/relations";
    public static String search=main_url+"search";
    public static String upload_object=upload_main_url+"upload/file";
}
