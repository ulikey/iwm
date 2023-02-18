package com.iw.common.core.domain.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件信息
 */
@Data
public class SysFile implements Serializable {
    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件地址
     */
    private String url;
}
