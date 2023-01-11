package restful.sdk.API;

import RestfulFactory.Model.DocumentDigests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Base64;
import java.util.List;
import java.time.*;
import java.util.BitSet;

public class Utils {

    public static String getPKCS1Signature(String data, String key, String passkey) throws Throwable {
        MakeSignature mks = new MakeSignature(data, key, passkey);
        return mks.getSignature();
    }

    private static final LocalDateTime Jan1st1970 = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
//    private static final LocalDateTime Jan1st1970 = LocalDateTime.of(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc.);

//    public static long CurrentTimeMillis() throws Throwable {
//        return (long) (DateTime.getUtcNow(). - Jan1st1970).TotalMilliseconds;
//    }
//
//    private static long nanoTime() throws Throwable {
//        long nano = 10000L * Stopwatch.GetTimestamp();
//        nano /= TimeSpan;
//        nano *= 100L;
//        return nano;
//    }
    public static String base64Encode(Object o) {
        return base64Encode(toJson(o));
    }

    public static String base64Encode(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }
    
    public static byte[] base64Decode(String s) {
        return Base64.getDecoder().decode(s);
    }
    
    public static String base64Encode(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    public static byte[] base64Decode(byte[] b) {
        return Base64.getMimeDecoder().decode(b);
    }
    
//    public static String Base64Encode(String plainText) throws Throwable {
//        byte[] plainTextBytes = Encoding.getUTF8().GetBytes(plainText);
//        return Convert.ToBase64String(plainTextBytes);
//    }
//
//    public static String Base64Encode(byte[] rawData) throws Throwable {
//        //Console.WriteLine(Encoding.Default.GetString(rawData));
//        String data = Convert.ToBase64String(rawData);
//        return data;
//        //return System.Text.Encoding.UTF8.GetBytes(data);
//    }
//
//    public static byte[] Base64Decode(String base64EncodedData) throws Throwable {
//        byte[] base64EncodedBytes = Convert.FromBase64String(base64EncodedData);
//        //Console.WriteLine(Encoding.Default.GetString(base64EncodedBytes));
//        return base64EncodedBytes;
//    }

    public static String ByteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba) {
            hex.append(String.format("{0:x2}", b));
        }
        return hex.toString();
    }

    public static String computeVC(DocumentDigests doc) throws Throwable
    {
        List<byte[]> hashes = doc.getHashes();
        int hshLen = hashes.get(0).length;
        BitSet bits = BitSet.valueOf(new byte[hshLen]);
        for(byte[] h : hashes)
        {
            bits.xor(BitSet.valueOf(h));
        }

        byte[] ret = new byte[(bits.length() - 1) / 8 + 1];
//        bits.CopyTo(ret, 0);
        byte[] _final = getSHA(ret);

        byte[] vc = new byte[4];
        vc[0] = _final[0];
        vc[1] = _final[1];
        vc[2] = _final[hshLen - 2];
        vc[3] = _final[hshLen - 1];
        //Console.WriteLine(Utils.ByteArrayToString(final));
        return String.format("{0:X2}{1:X2}-{2:X2}{3:X2}", vc[0], vc[1], vc[2], vc[3]);

    }
    
    public static byte[] getSHA(byte[] input) throws NoSuchAlgorithmException
    { 
        // Static getInstance method is called with hashing SHA 
        MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
        // digest() method called 
        // to calculate message digest of an input 
        // and return array of byte
        return md.digest(input); 
    }
    
//    public static String computeVC(List<byte[]> hashesList) throws NoSuchAlgorithmException {
//
//        byte[][] hashes = new byte[hashesList.size()][];
//        for (int i = 0; i < hashesList.size(); i++) {
//            hashes[i] = hashesList.get(i);
//        }
//        if (hashes == null || hashes.length == 0) {
//            throw new RuntimeException("The input is null or empty");
//        }
//        //single hash
//        byte[] vcData = new byte[hashes[0].length];
//        System.arraycopy(hashes[0], 0, vcData, 0, vcData.length);
//
//        if (hashes.length > 1) {
//            padding(hashes);
//
//            for (int ii = 1; ii < hashes.length; ii++) {
//                if (hashes[ii].length > vcData.length) {
//                    byte[] tmp = new byte[hashes[ii].length];
//                    System.arraycopy(vcData, 0, tmp, 0, vcData.length);
//                    for (int ttt = vcData.length; ttt < hashes[ii].length; ttt++) {
//                        tmp[ttt] = (byte) 0xFF;
//                    }
//                    vcData = new byte[tmp.length];
//                    System.arraycopy(tmp, 0, vcData, 0, tmp.length);
//                }
//                for (int idx = 0; idx < hashes[ii].length; idx++) {
//                    vcData[idx] |= hashes[ii][idx];
//                }
//            }
//        }
//
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        md.update(vcData);
//        byte[] vc = md.digest();
//        short first = (short) (vc[0] << 8 | vc[1] & 0x00FF);
//        short last = (short) (vc[vc.length - 2] << 8 | vc[vc.length - 1] & 0x00FF);
//        return String.format("%04X-%04X", first, last);
//    }

//    public static String getPKCS1Signature(String data, String relyingPartyKeyStore, String relyingPartyKeyStorePassword) throws Exception {
//        KeyStore keystore = KeyStore.getInstance("PKCS12");
//        InputStream is = new FileInputStream(relyingPartyKeyStore);
//        keystore.load(is, relyingPartyKeyStorePassword.toCharArray());
//
//        Enumeration<String> e = keystore.aliases();
//        PrivateKey key = null;
//        String aliasName = "";
//        while (e.hasMoreElements()) {
//            aliasName = e.nextElement();
//            key = (PrivateKey) keystore.getKey(aliasName, relyingPartyKeyStorePassword.toCharArray());
//            if (key != null) {
//                break;
//            }
//        }
//
//        Signature sig = Signature.getInstance("SHA1withRSA");
//        sig.initSign(key);
//        sig.update(data.getBytes());
//        return DatatypeConverter.printBase64Binary(sig.sign());
//    }
    public static final Gson gsTmp = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .registerTypeHierarchyAdapter(byte[][].class, new ByteArray2DimensionsToBase64TypeAdapter())
            .registerTypeHierarchyAdapter(boolean.class, new IntToBooleanTypeAdapter())
            //.registerTypeHierarchyAdapter(Class<T>.class, CustomDeserializer<T>)
            //.disableInnerClassSerialization()
            //.serializeNulls()
            //.registerTypeAdapterFactory(new ReflectiveTypeAdapterFactory(constructorConstructor, fieldNamingPolicy, Excluder.DEFAULT, jsonAdapterFactory))
            //.setPrettyPrinting()
            .create();

    public static String toJson(Object o) {
        return gsTmp.toJson(o);
    }

    public static class IntToBooleanTypeAdapter implements JsonDeserializer<Boolean> {

        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            String in = json.getAsString();

            return in != null && (in.equals("true") || in.equals("1"));
//            try {
//                return json.getAsBoolean();
//            } catch (Exception ex) {
//                int in = json.getAsInt();
//                return in != 0;
//            }
        }
    }

    // Using Android's base64 libraries. This can be replaced with any base64 library.
    public static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.getMimeDecoder().decode(json.getAsString());  //Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src)); //JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }

    public static class ByteArray2DimensionsToBase64TypeAdapter implements JsonSerializer<byte[][]>, JsonDeserializer<byte[][]> {

        @Override
        public byte[][] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray == null || jsonArray.size() == 0) {
                return null;
            }
            byte[][] response = new byte[jsonArray.size()][];
            for (int i = 0; i < jsonArray.size(); i++) {
                response[i] = Base64.getMimeDecoder().decode(jsonArray.get(i).getAsString());
            }
            return response;
        }

        @Override
        public JsonElement serialize(byte[][] src, Type typeOfSrc, JsonSerializationContext context) {

            //String[] array = new String[src.length];
            //StringBuilder response = new StringBuilder();
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < src.length; i++) {
                jsonArray.add(new JsonPrimitive(Base64.getEncoder().encodeToString(src[i])));
                //response.append(Base64.getEncoder().encodeToString(src[i])).append(",");
            }

            return jsonArray;//new JsonPrimitive(jsonArray.getAsString()); //jsonArray.getAsJsonPrimitive();
        }
    }

    public static byte[][] padding(byte[][] hashes) {
        int max = findMaxLen(hashes);
        byte[][] rsp = new byte[hashes.length][];

        for (int idx = 0; idx < hashes.length; idx++) {
            int len = hashes[idx].length;
            if (len < max) {
                byte[] tmp = new byte[len];
                System.arraycopy(hashes[idx], 0, tmp, 0, len);
                hashes[idx] = new byte[max];
                System.arraycopy(tmp, 0, hashes[idx], 0, len);
                for (int ii = len; ii < max; ii++) {
                    hashes[idx][ii] = (byte) 0xFF;
                }
            }
        }
        return rsp;
    }

    private static int findMaxLen(byte[][] hashes) {
        int max = 0;
        for (byte[] hh : hashes) {
            if (max < hh.length) {
                max = hh.length;
            }
        }
        return max;
    }
}
