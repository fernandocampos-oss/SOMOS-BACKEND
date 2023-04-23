package com.marcas.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BaseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> propertiesUpdate;
}
