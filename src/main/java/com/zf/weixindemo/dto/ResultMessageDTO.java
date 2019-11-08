package com.zf.weixindemo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
public class ResultMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String desc;

    private Object message;

    public ResultMessageDTO() {
    }

    public ResultMessageDTO(Integer code, String desc, Object message) {
        this.code = code;
        this.desc = desc;
        this.message = message;
    }

}
