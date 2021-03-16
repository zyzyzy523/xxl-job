package com.xxl.job.admin.core.util;



import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *     获取当前租户id
 * </p>
 * @author bin.xie
 * @date 20190729
 */
public final class LoginInformationUtil {

    /**
     * 获取当前租户信息
     *
     * @return Long
     */
    public static Long getTenantId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String tenantId = request.getHeader("HCF-Tenant");
                if (StringUtils.hasText(tenantId)) {
                    return Long.parseLong(tenantId);
                }
            }
        }catch (Exception e){
            return 0L;
        }
        return 0L;
    }

}
 