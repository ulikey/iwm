package com.iw.view.system.controller;

import com.iw.view.system.entity.USysUser;
import com.iw.view.system.service.IUSysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  前端控制器
 * @author likey
 * @since 2022-11-14
 */
@RestController
@RequestMapping("/system/uSysUser")
public class USysUserController {
    @Autowired
    private IUSysUserService  uSysUserService;
    @GetMapping("/info")
    public USysUser info() {
        return uSysUserService.getById(1);
    }
}
