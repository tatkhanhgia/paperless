/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.lang3.ClassUtils;
import vn.mobileid.id.general.annotation.AnnotationORM;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;

/**
 *
 * @author GiaTK
 */
class CreateConnection {

    public static String responseVariableName = "pRESPONSE_CODE";

    //Thực thi store và trả dữ liệu về dạng row
    public static DatabaseResponse executeStoreProcedure(
            String nameStore,
            HashMap<String, Object> data,
            HashMap<String, Integer> outParameters,
            String nameFunction
    ) throws Exception {
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = "";
        try {
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(nameStore);
            if (data != null) {
                for (String key : data.keySet()) {
                    cals.setObject(key, data.get(key));
                }
            }
            if (outParameters != null) {
                for (String out : outParameters.keySet()) {
                    cals.registerOutParameter(out, outParameters.get(out));
                }
            }

            debugString += "[SQL] " + cals.toString();

            cals.execute();
            try {
                int mysqlResult = Integer.parseInt(cals.getString(responseVariableName));
                debugString += "\n\tResponse Code return from Server: " + mysqlResult;
                response.setStatus(mysqlResult == 1 ? PaperlessConstant.CODE_SUCCESS : mysqlResult);
            } catch (Exception ex) {
            }

            rs = cals.getResultSet();
            response.setDebugString(debugString);
            List<HashMap<String, Object>> rows = new ArrayList<>();
            if (rs != null) {
                ResultSetMetaData a = rs.getMetaData();
                while (rs.next()) {
                    HashMap<String, Object> datas = new HashMap<>();
                    for (int i = 1; i <= a.getColumnCount(); i++) {
                        datas.put(a.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(datas);
                }
            }
            if (outParameters != null) {
                int i = outParameters.size() - 1;
                HashMap<String, Object> datas = new HashMap<>();
                for (String key : outParameters.keySet()) {
                    datas.put(key, cals.getObject(cals.getParameterMetaData().getParameterCount() - i));
                    i--;
                }
                rows.add(datas);
            }
            response.setRows(rows);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while " + nameFunction, e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        return response;
    }

    //Thực thi store và trả dữ liệu về mapping với Object class truyền vào
    public static DatabaseResponse executeStoreProcedure(
            Class classType,
            String nameStore,
            HashMap<String, Object> data,
            HashMap<String, Integer> outParameters,
            String nameFunction
    ) throws Exception {
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = "";
        try {
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(nameStore);
            if (data != null) {
                for (String key : data.keySet()) {
                    cals.setObject(key, data.get(key));
                }
            }
            if (outParameters != null) {
                for (String out : outParameters.keySet()) {
                    cals.registerOutParameter(out, outParameters.get(out));
                }
            }

            debugString += "[SQL] " + cals.toString();

            cals.execute();
            try {
                int mysqlResult = Integer.parseInt(cals.getString(responseVariableName));
                debugString += "\n\tResponse Code return from Server: " + mysqlResult;
                response.setStatus(mysqlResult == 1 ? PaperlessConstant.CODE_SUCCESS : mysqlResult);
            } catch (Exception ex) {
            }

            rs = cals.getResultSet();
            response.setDebugString(debugString);
            List<HashMap<String, Object>> rows = new ArrayList<>();
            if (rs != null) {
                ResultSetMetaData a = rs.getMetaData();
                while (rs.next()) {
                    HashMap<String, Object> datas = new HashMap<>();
                    for (int i = 1; i <= a.getColumnCount(); i++) {
                        datas.put(a.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(datas);
                }
            }
            if (outParameters != null) {
                int i = outParameters.size() - 1;
                HashMap<String, Object> datas = new HashMap<>();
                for (String key : outParameters.keySet()) {
                    datas.put(key, cals.getObject(cals.getParameterMetaData().getParameterCount() - i));
                    i--;
                }
                rows.add(datas);
            }
            response.setRows(rows);

            //
            if (rows.size() == 1) {
                response.setObject(cast(classType, rows.get(0)));
            } else {
                List<Object> objects = new ArrayList<>();
                for (int i = 0; i < rows.size(); i++) {
                    Object result = classType.newInstance();
                    Class<?> clazz = classType;
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        AnnotationORM temp = field.getDeclaredAnnotation(AnnotationORM.class);
                        String nameInDb = Optional.ofNullable(temp).map(AnnotationORM::columnName).orElse(null);
                        if (nameInDb != null && rows.get(i).get(nameInDb) != null) {
                            Object datas = cast(field, rows.get(i).get(nameInDb));
                            field.set(result, datas);
                        }
                    }
                    objects.add(result);
                }
                response.setObject(objects);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while " + nameFunction, e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        return response;
    }

    //Cast từ Objects get từ DB về dạng rows thành Class
    private static Object cast(Class classType, HashMap<String, Object> row) throws Exception {
        Object result = classType.newInstance();
        Class<?> clazz = classType;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            AnnotationORM temp = field.getDeclaredAnnotation(AnnotationORM.class);
            String nameInDb = Optional.ofNullable(temp).map(AnnotationORM::columnName).orElse(null);
            if (nameInDb != null && row.get(nameInDb) != null) {
                Object datas = cast(field, row.get(nameInDb));
                field.set(result, datas);
            }
        }
        return result;
    }

    //Mapping dữ liệu giữa Field của Object và Data trong DB
    private static Object cast(Field field, Object data) throws InstantiationException, IllegalAccessException {
        if (field.getType().isPrimitive() && ClassUtils.isPrimitiveOrWrapper(data.getClass())) {
            if (field.getType() == data.getClass()) {
                return data;
            } else {
                if (data instanceof Long && field.getType().getTypeName().contains("int")) {
                    return ((Long) data).intValue();
                }
                return data;
            }
        }
        if (field.getType() == String.class) {
            if (data instanceof String) {
                return data;
            } else {
                if (ClassUtils.isPrimitiveOrWrapper(data.getClass())) {
                    return String.valueOf(data);
                }
            }
        }
        if (field.getType() == Date.class) {
            if (data instanceof LocalDateTime) {
                LocalDateTime local = (LocalDateTime) data;
                return new Date(local.toInstant(ZoneOffset.UTC).getEpochSecond());
            }
            if (data instanceof Date) {
                return data;
            } else {
                if (ClassUtils.isPrimitiveOrWrapper(data.getClass()) && data instanceof Long) {
                    Date temp = new Date((long) data);
                    return temp;
                }
                if (ClassUtils.isPrimitiveOrWrapper(data.getClass()) && data instanceof Integer) {
                    Date temp = new Date((int) data);
                    return temp;
                }
            }
        }
        if (field.getType().isArray() && field.getType().getTypeName().contains("byte")) {
            if (data instanceof byte[]) {
                return (byte[]) data;
            }
            return null;
        }
        if(field.getType().isEnum()){
            Class<?> clazz = field.getType();
            Method[] methods = clazz.getMethods();
            for(Method method :methods){
                if(method.getReturnType() == clazz){
                    try {
                        return method.invoke(clazz, data);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(CreateConnection.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(CreateConnection.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }                    
                }
            }
        }
        if (!getWrapperTypes().contains(field.getClass())) {
            Class<?> clazz = field.getClass();
            Object obj = clazz.newInstance();
            Field[] field_ = clazz.getDeclaredFields();
            for (Field field__ : field_) {
                field__.set(obj, cast(field__, data));
            }
            return obj;
        }
        return null;
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }

    //Convert Object thành List khi cần
    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[]) obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>) obj);
        } else {
            list = Lists.newArrayList(obj);
        }
        return list;
    }
}
