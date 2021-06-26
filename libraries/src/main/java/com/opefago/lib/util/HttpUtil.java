package com.opefago.lib.util;

public class HttpUtil {
    public static boolean validStatus(int status) {
        return 200 <= status && status <300;
    }
}
