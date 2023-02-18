package com.iw.view.web.user;

import com.iw.common.core.domain.R;
import com.iw.common.core.domain.login.LoginUser;
import com.iw.common.core.domain.login.SysUser;
import com.iw.common.security.annotation.InnerAuth;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;

@RestController
@RequestMapping("/user")
public class UserApi {

    @InnerAuth
    @GetMapping("/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username")String username) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1001L);
        sysUser.setUserName("admin");
        sysUser.setNickName("nick chen");
        // 默认初始化的密码
        sysUser.setPassword("$2a$10$0WO7BnHnudeXCO3r8.8uG.06f7e28FMClV.Yaw9Bb2E2mViPdYuEW");
        LoginUser loginUser = new LoginUser();
        loginUser.setSysUser(sysUser);
        loginUser.setRoles(new HashSet<String>(){{add("admin-role"); add("monitor-role"); add("employee-role");}});
        loginUser.setPermissions(new HashSet<String>(){{add("system:user:list"); add("system:user:edit"); add("system:user:del");}});
        return R.ok(loginUser);
    }
}
