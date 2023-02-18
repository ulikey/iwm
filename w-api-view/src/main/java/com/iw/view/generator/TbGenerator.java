package com.iw.view.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * 生成mybatis文件
 * @Author: likey
 */
public class TbGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://39.105.228.32:3306/db-iw",
                        "likey",
                        "likeyLi73+!")
                .globalConfig(builder -> {
                    builder.author("likey")     // 设置作者
                            .fileOverride()     // 覆盖已生成文件
                            .outputDir("/Users/likey/Desktop/work_picc/w-mail/w-api-view/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.iw.view") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "/Users/likey/Desktop/work_picc/w-mail/w-api-view/src/main/resources/com/iw/view/system/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("u_sys_user") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                // .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

}
