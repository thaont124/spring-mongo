package tina.demo.utils;


import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;

public class UpdateUtils {

    public static <T, U> void updateEntityFromDTO(T entity, U dto) {
        BeanWrapper src = new BeanWrapperImpl(dto);
        BeanWrapper target = new BeanWrapperImpl(entity);

        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = src.getPropertyValue(field.getName());
            if (value != null) {
                target.setPropertyValue(field.getName(), value);
            }
        }
    }
}