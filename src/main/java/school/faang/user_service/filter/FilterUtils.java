package school.faang.user_service.filter;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;

@NoArgsConstructor
public final class FilterUtils {
    public static boolean matchByDto(Object target, Object filter) {
        BeanWrapper dtoBw = new BeanWrapperImpl(filter);
        BeanWrapper targetBw = new BeanWrapperImpl(target);

        Integer expMin = (Integer) dtoBw.getPropertyValue("experienceMin");
        Integer expMax = (Integer) dtoBw.getPropertyValue("experienceMax");

        Object targetExp = targetBw.getPropertyValue("experience");
        if (!(targetExp instanceof Integer actualExp)) {
            return false;
        }
        if (expMin != null && actualExp < expMin) {
            return false;
        }
        if (expMax != null && actualExp > expMax) {
            return false;
        }

        for (PropertyDescriptor dtoPd : dtoBw.getPropertyDescriptors()) {
            String name = dtoPd.getName();
            if ("class".equals(name)
                    || "experienceMin".equals(name)
                    || "experienceMax".equals(name)) {
                continue;
            }
            Object dtoPv = dtoBw.getPropertyValue(name);
            if (dtoPv == null) {
                continue;
            }

            Object targetPv;
            try {
                targetPv = targetBw.getPropertyValue(name);
            } catch (Exception e) {
                return false;
            }
            if (dtoPv instanceof String && targetPv instanceof String) {
                String f = ((String) dtoPv).toLowerCase();
                String t = ((String) targetPv).toLowerCase();
                if (!t.contains(f)) {
                    return false;
                }
            } else if (!dtoPv.equals(targetPv)) {
                return false;
            }
        }
        return true;
    }
}
