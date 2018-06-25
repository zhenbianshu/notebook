package com.zhenbianshu.controller;

import com.zhenbianshu.model.AuthParam;
import com.zhenbianshu.model.Whitelist;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by zbs on 2018/3/20
 */

@RestController
@RequestMapping("/")
public class IndexController {

    @Whitelist
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public Object index(AuthParam authParam) {

        return "test";
    }
}
