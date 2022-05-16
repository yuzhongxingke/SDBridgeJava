package com.housenkui.sdbridgejava;
import java.util.AbstractMap;
public interface Handler {
    void handler(AbstractMap<String,Object> map, Callback callback);
}
