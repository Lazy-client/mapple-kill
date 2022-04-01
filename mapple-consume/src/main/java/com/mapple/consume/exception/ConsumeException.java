package com.mapple.consume.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author zsc
 * @version 1.0
 * @date 2021/10/4 16:29
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeException extends RuntimeException {
    private Integer code;
    private String msg;
}
