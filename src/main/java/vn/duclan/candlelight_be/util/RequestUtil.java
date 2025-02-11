package vn.duclan.candlelight_be.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestUtil {
    public static String getIpAddr(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            var remoteAddr = request.getRemoteAddr();
            if (remoteAddr == null) {
                remoteAddr = "127.0.0.1";
            }

            return remoteAddr;
        }
        log.info("X-Forwarded-Header: {}", xForwardedForHeader);

        return xForwardedForHeader.split(",")[0].trim();
    }

}
