前言：
===
本组件基于spring框架开发的，将controller的json返回数据按照规则进行字典翻译。

使用方法：
==
1. 在controller的方法上使用EnableDict注解开启字典翻译功能，需要指定返回对象的Class以及返回对象的key/field名称  
2. 在返回对象的Class中使用Dict注解指定字典类型以及字典信息  
3. 若返回对象的Class中属性为封装类，可使用WrapperField注解予以标识，封装类中需要翻译的字段同样使用Dict注解予以标识  
    
4. 在使用过程中可自定义自定的获取Service，只需实现online.kakapapa.service.DictAspectService接口即可  
5. 本组件默认使用caffeine作为临时缓存，在使用过程中若需自定义实现online.kakapapa.customize.cache.DictCache即可  
