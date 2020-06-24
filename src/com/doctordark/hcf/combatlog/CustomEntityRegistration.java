package com.doctordark.hcf.combatlog;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.server.v1_7_R4.EntityTypes;

public class CustomEntityRegistration
{
    public static void registerCustomEntities() {
        try {
            registerCustomEntity(LoggerEntity.class, "CraftSkeleton", 51);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void registerCustomEntity(final Class entityClass, final String name, final int id) {
        setFieldPrivateStaticMap("d", entityClass, name);
        setFieldPrivateStaticMap("f", entityClass, id);
    }
    
    public static void unregisterCustomEntities() {
    }
    
    public static void setFieldPrivateStaticMap(final String fieldName, final Object key, final Object value) {
        try {
            final Field field = EntityTypes.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            final Map map = (Map)field.get(null);
            map.put(key, value);
            field.set(null, map);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setField(final String fieldName, final Object key, final Object value) {
        try {
            final Field field = key.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(key, value);
            field.setAccessible(false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
