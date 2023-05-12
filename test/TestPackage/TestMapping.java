/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.List;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class TestMapping {
    private static KYC assignAllItem(List<ItemDetails> listItem, JWT_Authenticate jwt) {
        KYC kyc = new KYC();
        for (ItemDetails details : listItem) {
            String field = details.getField();
            int type = details.getType();
            Object checkType = ItemDetails.checkType(type);
            Object value = null;
            if (checkType instanceof String) {
                value = (String) details.getValue();
            }
//                if( checkType instanceof Boolean){
//                    Boolean temp = (Boolean) details.getValue();
//                    temp.
//                }
            if (checkType instanceof Integer) {
                value = (Integer) details.getValue();
            }
            kyc = assignIntoKYC(kyc, field, value);
        }
        return kyc;
    }

    //Assign data into KYC Object
    private static KYC assignIntoKYC(KYC oldValue, String field, Object value) {
        Field[] fields = KYC.getHashMapFieldName();
        for (Field temp : fields) {
            if (temp.getName().contains(field)) {
                try {
                    oldValue.set(temp, value);
                    return oldValue;
                } catch (IllegalArgumentException ex) {
                    return null;
                } catch (IllegalAccessException ex) {
                    return null;
                }
            }
        }
        return oldValue;
    }
    
    public static void main(String[] arhs) throws JsonProcessingException{
        String jsonJWT = "{\n" +
"  \"liveness_threshold\": 30,\n" +
"  \"sub\": \"68b6c4c6-552d-4d9e-8e9a-a5cb43f7f70e\",\n" +
"  \"gender\": \"Nam\",\n" +
"  \"iss\": \"https://id.mobile-id.vn\",\n" +
"  \"liveness\": true,\n" +
"  \"liveness_confidence\": 93,\n" +
"  \"place_of_origin\": \"Trà Vinh\",\n" +
"  \"issuing_country\": \"Việt Nam\",\n" +
"  \"match_result\": true,\n" +
"  \"exp\": 1676860495,\n" +
"  \"iat\": 1676536495,\n" +
"  \"jti\": \"4dfcdc5a-2703-4dca-b3b7-33b8e9246531\",\n" +
"  \"email\": \"phungtsm70@wru.vn\",\n" +
"  \"document_type\": \"CITIZENCARD\",\n" +
"  \"transaction_id\": \"DEMO-230216153452-269685-141920\",\n" +
"  \"match_threshold\": 70,\n" +
"  \"document_number\": \"079099039851\",\n" +
"  \"email_verified\": false,\n" +
"  \"match_confidence\": 86,\n" +
"  \"phone_number_verified\": false,\n" +
"  \"place_of_residence\": \"235/33 Xô Viết Nghệ Tĩnh, Phường 15, Bình Thạnh, TP.Hồ Chí Minh\",\n" +
"  \"certificates_query_path\": \"/dtis/v1/e-identity/certificates\",\n" +
"  \"city_province\": \"HỒ CHÍ MINH\",\n" +
"  \"nationality\": \"Việt Nam\",\n" +
"  \"name\": \"Lương Bỉnh Khang\",\n" +
"  \"phone_number\": \"0844232144\",\n" +
"  \"assurance_level\": \"EXTENDED\"\n" +
"}";    
        String payload = "{\n" +
"    \"items\": [\n" +
"        {\n" +
"            \"field\": \"FullName\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"Huỳnh Lê Tường Vy\"\n" +
"        },\n" +
"        {\n" +
"            \"field\": \"BirthDate\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"31/12/2022\"\n" +
"        },\n" +
"        {\n" +
"            \"field\": \"Nationality\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"VietNam\"\n" +
"        },\n" +
"        {\n" +
"            \"field\": \"PersonalNumber\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"079200011188\"\n" +
"        },\n" +
"        {\n" +
"            \"field\": \"IssuanceDate\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"31/12/2022\"\n" +
"        },\n" +
"        {\n" +
"            \"field\": \"PlaceOfResidence\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"Quan11\"\n" +
"        },\n" +
"        {\n" +
"            \"field\": \"Nationality\",\n" +
"            \"type\": 1,\n" +
"            \"value\": \"VietNam\"\n" +
"        }\n" +
"    ],\n" +
"    \"file_data\": [\n" +
"        {\n" +
"            \"file_type\": 3,\n" +
"            \"value\": \"/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAGQASwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD2/HHFGM0vQCkzVjGsKQDmnnmm96BC4wKXFO6ik70DExSU6igA7U05NOpw60AREc0tPxSY5oAbSnpSnqKDxQA0DNKBgU6igBKa3rTsc0EcUAMozS+1OwOKAIzzSVLgU3FADcUn4U/FJgUANzRTgoxQODQADk5q1GflqDAqaLpQND6QdadijbSuMSkp2KTGKAEopaMZoASkxTse9GPegBmKKcQfWk2+9AFcnikBzQelJ3pkCn0pvelpO9AD80g4pR1obg0DDNGfak70oNAhRTsYpop1ABjmkNLRQA2lIyKAKXtQAlFFKeKBiZ7YoPOaQnFJuX1oEJ/F+FLmkDDeee1LkUALSHrRkUZFAxKKO9GeaACk70ZozQA+pITUO7ipIjlqARZooFFIoKQ9aWmnpQAUoGKaDmnc0AB60lIaBTAWjFFJSAp0tIOtITzTIFJoB5pvU06gBwNJSU6gBKUUhNKOgoAcCM4p3WmdRTh0oAWlxxTTnscUZIHJ6UALTSwyRTuiiopWwOCAfegBWlxwAWPoKa0jAZIUDuSelZ15rFnYxfvbmCBm43TSqvP4nk1ly+K/DyYSfU4bhlPIX5wD/ujNIDde7hKnDq2OpQlgD+VRSXtojJ5kqRlu0xCj8zWK3jbw+AUjupNuRyInRR35JXFQHxXpepsba21WydiMlXIJwPY0DOqzuBYYC+tLltuQQfcVyFqxXa9hcOIu6xyboz9FA4/D8q0bHU2lPykyMozImRuUeuB1FMRvJIrHGfmz0p7His17iN9kikg9fmGD0q5DJvUc7ie/rQBKpznjvSk47Ug4OO1LnNAxM5oBxRnH+NNZ+MetAD85zTkbDCoh0pVb5hSYGgOaWo0bK06kNC5pCaM0hp2GGc0Ugo70CFoo5FJ1oAU8CkzSYopjKuaTNNb0pKCB+c0ZpuaXqMUAL3paTIpC3pQA4UucUwH3pc5NADwefan5qMU4dKAH9aimcIDu4BB/Tmop7uOzt3lkYBEHU14x4/8AiZ5d9JaQ7girtCI3U5PJ9D0/Kk2B6Fq/jWDTbYTRhjD08+ReM/7K9W/l715j4g+K7XbBobq5VVKloraUxq2OxIG7B7815Hfaxe3rMZrmVlJOELkgfnVRZsHIXn3pAdnJ49l89pIrCEPkESSEs/8A311x1qAeM9ZmuQQImJyFQrtUZ/H+dcoZ3P8AEcemeKY0jdzxSA6m/wDFOsy7opTDGpAGyM8L34weDzWadbkabzJIuv8Adds/mSayA2eKG6daaA6+w8bapZOzR6jPLk8JMxPpznNdtoPxHS7vGa8jERYAO0btheoyByQPX149K8UL4PWljuXjYMpIIpgfWEGqRmKGaSQS2sy/LKeT1AyfQ9OeM9a3LZzCXRiBtVJQSf4WOP6V84eEvGMttCljK7CPIwFyQRnkdfevYdD1aC7t7WS2nfAQDnuScYPfHFAz0ASDHBB+nenMxK8cZqhatDJCZwXBzjDdOnpUlvKNu4ngdj2oAvICVpuM/wBKh+1xMNquM9akByuc0xCg9j1p47VC24rx296cMgdaALsXKipar27cVYqSkITSZpT0pKYwpR1pKUGgQtJS0hPNAhe1JRmjNMZnk0maM80HrQSG7NOHSoT1pyt2oAkzSA00nApuaAJM04HGKiBpc5NAycHNPBABOagRv0ryz4o/EX+yITpWlyZuzzK6kERjHGRzyc5A/P0oEQ/Ff4j22m2s+jadIZLyQYkYYxGOOnua+ep7h55DJK25ySeadeXUl1cSzyuXlkcu7sckk8k1VBGakCTcKaWIPFOXB7UMCO1AAspB+apCwbrUPBNNzg4FAE3T7tNLHFM30m7NACE5NJRRRcCzDO0JUqxDAgjHrXUaDqlsbpUupJsScnYwX5/qa46nAknFAHtaeKNHsUQW1zdwtyGMUi9exyG9qgf4tanZP5UU8Fwh6vsyR29q8gBb1qRJWXrzQM9cT4t3i8SFm9SEC1r6d8VbeWdA84iDdWmiyB9cGvFRIHXH5Gon3Kc+lCCx9S6X430y9wDJBJnjzLZ8j8RnP866i3u4J1zBMHHUAHJr43t9Rmt2HR1Bzz1H49RXoHhf4i3FnMkd7I00XTccB1GOAeOfrTuB9MWz5NW91cfoetw3cUckcy7WGQSRg+456V1SyFuuM0hol3UhNMz9aM0wY/PvSg54qPNLmkIkpKaGozTGh2aM+1NpaYXM0HFBJppPNG7igkOtKDimE+9JmgCUsKZ0OaYTxxTc5oAl3UbqizSlwoJNAHM+O/Fq+F/D0twrgXEv7uEZ53EHn8P8B3r5bv7+4vZXklkY72LHcxYknuSeTXrXxhng/tCJp7hnlRQqWuTsXOTu9z0FeNSkk84/CpbAibk0LjNJyaXbjmkCH76PN7HpUZpKAJXKsPl61GaSlAzQAlFP2H0qSOEE4alcrlZBRV4WvTA69Kkk050UFlKn0pcyK9mzNoqw9sy9jioWQr2ppolxaBXK1IH3DnrUNKM0ySwrFTU+d4xVRWqaJuaBgy4PH40qNggk4I6GhzyMd6jdCASOlMDvfBHjSbQ72GC5dnsy43At93JGSPb2r6U0y7hvLRZreQSREcOp6V8XJJjgn8a9n+Efj2aK8j0a8KlSAI2YnkZ6e55o8xpnv8bbkBPJ9fWn5qtBIrLleAefzqbIzQMeDSg9qj3YNOzQIdRSZzRQMdnNKOlNpd1O4rGSTjvSbvem0UyRSeaaW96UnimNQAu73pA1NooAeeR1rE8T+Ibbw1o02o3ILhCFRAOXY9B/n0rYLYFeDfGjXWudah0xJMxW8YZlHTed2c++MUmB57rms3Gs6lNfXUheaY5JOePQD2rGJ96c7l2JJOaZUgOBApxkzUdFABShSas29s0zAAGrv2JmkEcaEnv7VLkkaRptmYkTMRgVsWmjSypuwAB1Oa1tK0eCZupAU8k9/YVvGwitipjKzOOkanP54rGVXojqhQS1ZxtzpbW+GOMH1qey0aa5bKlFX1au0j8Nahfv50lpEijoHZhn07VrW2i6natzDGIj1EYO79eKydU0VFXORtvD0iEOE3sOA5b/AOt0q3Po8srBNoO0ZyeAc11osLyObfHZyAd2ZwT/AIVXbSdQnlPnbtrHnC4/mBWftNTdQSOJuNKdmaCJA7KOWU4C+5rHm0kq+0Hcx4Fetw6K0cflBSqk55PJPvWNrOiLbstwI2A53Yyevf8AlTjWJlQujySe1dOccE8VEi/Q+xrtNY0YQxoq4Zi2FbsR2/SualsirDAOCMjIrrhUujhqUbO5n9OBmnRN81KVxxUZba3Fap3OdqzJ35ANPj5GOoqASZ4qRSVOKACSP0pbed7a4jlRmR0YMrKcEEU9jwSOagLZqhH1h8OPE6eI/DFnI7E3ESCObPdhxn8cZ/E12m4V8u/CXxHLpPiqGzaQCC+IjYHGA3O0/nivp6NgyAnrSRSJByafUY607PvTAdS5PrTQeKXNAx2aM02lpCMYtg0bqZmjdVEj9wpCc0zPFGTQA6gkYpAfWmmgCK5uEggeSQ4VFyfpXyb4iv31LXLy7c5M0rP9Mk8flivpzxHIY9JuF4IMbZFfKdwG89t33snNSwKzdaSnN602kApq3a2sk7ZUEAdWI4A9TSWNnLe3KwxqeTyfSu607w6saxJtyDyxI+9WdSoom9Kk5u5nWOlyPCsUMbAt7ZZvoK6bTfCCRsslxlwPuwgnGfUnvXSWGmQwgFVAbHXFbEVuFIIFcEqzex6MKSRRsdGiijx5Yz3rXg06JF+4Mj2qWJeRVxF4rJyZvyorrbqoxin+SmB8ozVwKMcUCI7s4qdRrsUlgCtkACnsm45POKstHx0pgTBo1HYrPCD25HSqptlkQxygNnr71pleajeIlcZx+FAzgdd8OStDIto24Z3rGw5Rs5BU/wBD6muVudMW6sVuFhdSvDxkHcpHBB/z3r16SBdu1hznP1rndX0J5HN3ZnZNxuQ9HwO/ofetoVGjGdNNni15YtFnAwQehrLkTDYNel6tpIuAxEBjlAwyEDI9/pXBalZy2srLIhDA9exHrXbSqJ6Hn1qVtTM71OrcfSoD1pytgVuchYB5IHeoGG1vanBvmFOdQ3NAE+nXL2t9BPGcPHIrr9Qc19eeFtROq+G7G9PBliVz35IBIr46i4bmvpX4L3zXfg1o2k3GCYpgnoNoIoGj0sNTgaiDGpM0wsPzRmmA04NQFh4PFLmm5pQeKYzFppNDH0pM5FMgPrS54ppozxQApNDNjtSAZpxGRQBg+JblbfQ764kTdGkfzA9/85r5ZuH3yvKersT+Zr6U+JDlPBd4inBlKIP++gT+gr5luCd/tUsCInNPjTPJqM1PCMik9hrc6/whYCWUykYB56V3yQhHQnoBiub8IKPIUeuK7FAM4rz6srs9SgrRRft8ELxV9Pujis6BcdKvRAg+tcrOuJcjqzH19qqp2qwpIHSkmOxbU+1PBOOKgjOTVheDTEBHqKiI+b2qc81C2QelK4xp9qjbPX1p7ZxUDMaYxJBu4qu6YGOtTbvXmmO4ouBj6jpcd0nQ7uzrww/GvOfEWkhRJHIMjnZIRjB9D/KvVJ3Gw4OK5rVo1mjZXAIbIPH+e9aQm4synFNHhVxE0MzIwxg0wDCbvetzxLp/2bUG2/dIyPzrEY/u8Y716kJcyueNUhyyaEycD2pS+RUeeMU7qKoglTkivefgg7w2F/Gc7GkUj8q8IthlwK+ivhZp0lnogZiP3x8wH/gIxn8qGNHpwNOzUCMSoNSA5PWmIkBp26o8+9KDzQMlHWnUwHBp2aAuYlJ3oJHWmnrmqJHE8UmaTdxTQaAJQaXPPFMWngYoA4L4rvJ/wi7BDhQw3GvnCU5kNfTXxLtnm8H3eD8iBW6d9wH9TXzJIMOalgJUkJO4KO5xUdWrCIzXkSDuwqW9Co6s9M8JwFLME+grq04IrI0S1NvYoDnJAzWqMAEtwK82erPVhoi9E4rRgGQDXIHVt85CECNeM+tblhqsZGWzgfxHgUvYs0VZHQImatxwgnBBrFHiTS7dsSTIf+BCtKw8R6ZdH93MuT6uP8aXsmP2yNBYCD0OKk8rAqaOeGUZRwR9akypXjmpcbFc1yoFOaY0Zq38uelNZlHFTYfMVGhzURtxV12QL94AVWkvrSAZlnjXHqwqlAOdIrvb4Heqzx46U9/EukeYY/tKEjqdwwP1pr6tp0n+ruEb6MDV+yZPtkZt7lUzzWDcMJAVNbGq3yeUWj5A9DmuWTUI5ZyhIDE8c0vZtai9rFnMeJbHzVkJBLjla8/lGHIr2e5t1mRsjPvXlWvWX2G/aMdCSf1rqw8/snFioacxk05RTR1qZACRXWcBY0+Pzb2CMdXcKPzr6t8J2P2LRbWMjDJGqn3woGa+bfBGltqfii0iIJRH3n8OR+uK+qLNVjtUVegUAflQNFrOKcD7VFnNKCaYEwNOFRDrUoNMCSngjFRg808DikBiGmU88mm96okQ9KaowakIpAOaAHZxTgxpAuadgUhmJ4rtReeHryAk/vEA4+or5VvIvJupY/7rEY+hr67vo0kt2Dj5cc18kawQdXu9v3fNbH50mBRrd8LwedqycZxj+dYddL4NIGqr68Y/Os6j91mlJe+j1SBNkKjHas/XLiSK0MUZwXHOOta8Qyops1hHO4dxnFcEd7s9KSurI4mCOdWz5csn54q+mm6pdFQGkiX3NdVHDFAowg49RTo7pAcYGBVOtbYI0L7nKv4RupzvmuJGb15psfhia1YMLiQEHsCP6136XEZT+DPpnmo5WhfAdAM96ydaRvGhAzdKk1C1QBrlnUHjk9PeuutNRPkrl/rXLyRiBvkbKntVi3uOgFQ53KVPlOqF2GGQ1RSXRUZzWfbsSB6UXDkCp5ikiK91B1idNzDI45ritTtLu8ly0z4/hy2cV0V3NgkHmq8MaO3mSHCirVSxLpczOOk8L3UvMcr59ST/AI1JHoWtWoxHeNgHgMMj+ddqLu1QdUVR3JxS/bbZx8jxP/ukGrVafYiVCHc5NH1mCMxTROwAxwRg1lzSL9p3EGORWzzXaSXKO+3AI7iqdxYW043GMAnnhR/hVxq33MpUeXYrQSeZArZ6gV5/40h2XaSY+9/OvRo7cRptHQdK4Xx0oAiz13f41dH4zOtrTOIXGeakU8cVFT0OWX613HmHsfwX0oTfar9gN0cihSVHOQQea9tUACvNfg75S+EVKriRpG3n1wxxXpFCGSZpwNRg+tKDTAmB7VKOlQr1qRTxTAmU1IOlRLUgPFMDGpOgpxFMJ7UiQzR05pBS9qBjwaM0wUuaQDJ1MkTKAOeOa+Z/iD4efRtceVVIiny49vmIx+lfTNef/EfTtNvrMJePsmyNr7sYGT/OplJRV2VCDm7RPncjgGt/wiT/AG5Evr/jVy/8JsqlrM71C7sbs8e1ReDbdj4ljRgQVUnB/Cs3OM4uxv7GdOaueuRL+7H0p5bApUACAUjx/ISM158nY9KCuYmrapHaRZYk+wrEsrfVvEU7iFvItl4yDgk/5NXrrTHuJJHZW4YjmtLSS0EiBW2kcU4SSKqU3JWTPL5Jnsnvre8a6e5BVYmEpGznnPPcdK6vwva6nqn9pXmmTXIsrYKYoriTO7jkGuy1jwppus5u7q3Uz5GZFcru+uDUtrP/AGTpn9m2Xk28OCpwMn9Sa3lVi1axzQw9RSvcxrHVHuoWVlO9DtcHsa07WVg43ZApkenTfaRfrKGIAV02gB1Axnp1xWhfpGk8aRDGBzXHJWPQ6am/pKCaLNGqIkMQyeTVbRpTHFwabrcxkRPrUCS1OeuVeSUlckZrMuEvL66TT7TPmv8AeY9EGep/I/lXU2saLbOxXJAJ+tZ32NbRWuRJcebK29trjAPsMe9aU97sJXasjyzxRYXOh69e2l4DdB0KwtK5O3J4Yfkfzqfw3pEviLV5jbQNawRwjPlvwGGBn6nrXoeorZa4qR6hH5pU8M4wfzFaFlHZ6VpTW1kiwoSTtTHP1Jya7FXjaxwfVZ3vc4KK9v8AS7prS/zKEOBLnOa6W0uBPGGDZBqldWLXtx93jtirthpcts4DfdrlnJN3R2RhyxsXFXcprzz4hoY5bf0Oa9N8oIK4L4hWjTmxVBlmcr+lbUJe8cmIi+VpHm1OQZOfSuig8JXjlg67cHqemKovpj27qNpbLY9jXZ7WNzheGqJXPZfg15x8OTb1xGsmY29ck5/lXqanIrhvhxZiz8OQqudpGcH35P65ruFPpWi1VzFprcfTlPFMp60wJBUq1EKkXpQBMtSDpUYqQdKYGSaiPWpG6008UXENFFIepoouAuaN2aTFBAFIBR715t4qX+0NdjgIziQ8eyg16TjivPLpwPE08zrlvLkC57HdXLi3aCPRy6KdR3KNppMbyOytgDjHrXL2UCQeP5woA/cA/j8tdtpcbPAzk/KSTmuZ1JoI/F9s8MLLwUklP8ZOMD9DXNSl0OzEQvqdOnOKtxpntVWPtWjbgHFZT3CmRmxEoxgVSm0T5ty8H15rpYYgQABU/wBm5xipRtc5L+xJmHM2B/u5pY9HhhYE5dh6+tdW9uFGSKiazUtnH409hqTMiGA4xjAqCeE72fvW60QRKpzooj+tS7tibIdOU9MGjVYyIgau6cnze1S6tEPJxijlGmZVmhKKfUUtxblT7GrenhTGAe1XZLdZBwOaEmgOfOnxSclefWpo9GjK9etaotGB+7VuK1wgqg5jHi0uOLnHPrSSW2FJx0re+zcZPFULxQisMVLQtznZxzXNa/biW80wE9Jyxz2ARjXT3PLEVxfiW5uo9UtpLZA0cHL5GRlsj+X862paI56iu7HSJZ+fblm+UH1rPu9Bj+xCVY92w54ra06dbuzLbQCRyBU8bqNJnVx8yjv+NRzM6qcUbfhUKNLVV6YU49MiuiWuc8Lo6WjZGBtXH610SnivToNumjw8UkqrSJR0p69KjHSpF6Vqc5IKlA4qNalHSgB61IOlMXrUg6U7DsZB60wmnnrTMUiRMUUppKAA0nWg0gGKAHV5/wCKrR7TUfNTgMSfwbP9a9AFYfinT/tum7lHzIf0/wD14rDEQ5oHXg6vs6q8zndPlWLSUAGRyM1l3NnHKJrg43IVcfhzV7TUL20kJ6jt6VDLCEDxudpIrzloz2JxumiaJsgVp2jcc1i2rfulHtWpbNjFVNHPB20N60foTxWpEoesS3kCgVsWkmQPWskbEzQgjkA1A0fUAVcJGMk1XlYANkYx0qhIzLw7Vxnk1izThn25/Wp9Yu2J8qI/vG4FZ9pZEXCkkn1zQkU0bWnbs8ZqxfAumCSatWNtiMHjFWpIFeMrgZx1qrBdI42K8NvqHlN0c8V0tsd6rznNc3q2nsboYwCOhB71paLdHy/KkPzrwalobN7yqcEwQNvFOjIK1JigkjkOFxjisS/b71atzLha569mznmkBlXBwxPYVzd7GJNPnc/ed9w/MVtahKUt3I6kVnTS28tvDbRfM4wGOO9abIzjrO5e8PbhYM5HByBV/wAszbYFHDEFh6j0qtCTZWIUjB6gDvW14etGd2uJVzjof9r/AOtRThzySNKlRUoORu2UAtrdEAAOOcetXVNQDrU6d69ZRsrI8CUnJ3ZMvSpF6VGvapUFAiRalXpUajmpF6UDRKOtOpo606mMyj1php7UxulIgbRSigmgBtGKKKAHA8U1groyuMqRgilFBbigEcXf6dLp940kPQ9CehFUXCvIz3ECuxGApFd7NGk0ZWRQVPasW50RSd0MgX2YZFcNTDO94np0sYuW0zlEysjAoEOc7R0ANXYWpdRspbS5DSMreYOCPaoYz2rGUWtGaxmpO8TWgkyQM1r2rkACsGBsEVr2zZArBnQnobAIddrE4qtdyFYWAHOKUSYXjmjygynJ6+tCGcpdnZOjOcZB5rNuPE+n2V0IJrqONyMjcwFdDremC6hwrFSOhFcSfDYN0ZLkLO2CBkGtI26g7nc2uvRm2G2RCD0INOk1pYomleZVGOpIrlU8Izrbq9vL5YI5XGRUy+GrhowZ384YOBjFOwXIF8ZWl/qj2sTNIQcFlX5fzrY0uQz3bFBwDWC3h55LobWEQ6HC812Gh6bHYx4HzE9TSbGbsUm1QDUxfCk1XbAAqKVzsqAK95P8p5rnrqT5iKv3sxA61izMSTzRFXZEnZFG/BmVYgcbjimRm4fCuqFl/iArU07TF1K4+eRkC88Ct230C1t2yxaT2biutUZS2OX6xCGj3MbTtLmvZRLL2PJPAA9veuuhRIIlijUKi8AUiqqqAowB2FOHSuulRVNeZxV8RKq9dh69anQ1AOtTJitjnLC9qmQVClTL0pDJB1qVRxTFFSL0pAh4704Ug4paZRkt1qMmpmWotlIgZk0tPCgU1loAbmkyc0u33pNtACqTSk8UbRSY9qAGnpUEnNWSvFQshNAGDr0Be1WT+4f51goec119/b+faSx+q8Vxyja5U9jg1x4la3O7CS0sX4GGa1bZwO9Y8R6VdhcjmuGSO+JtRsDUzzKicms6GbjrR5hlk25704q+g5y5ULcziTheazgEicu+K0ZXhtwS5APWuO1LXg8xji27c8mrcbbBTTnudQNXjSAhYywI4wadBqRaIq0ewE8ZPIrjRdvuXkj0wakF4xyxkbd9aVmdPs4nUvGHLFT8x6GrtmzRKEk/OuNs/EghuVimwVJxu9K7GG8tprdWVgcgU7dznmnHYumYAHmq0k3B5qpPPt6HNV2uTjrUSVghK5FdvuY1my1PLNvbFV2JZgAOvFXCJFRm94fgC2zynqx4+la+ar2EHkWUSD0z+NWMGvVgrRR41SV5MXd2pQ3amhTTgpzVkDl6VOnWocY6VKmaALKVYSoIxU6CkMmWpB0piinqpoGOzTgeKbil5oGZrcmmE4p5601qRAykz2pT0ptACGgcCg0uDTYADk0UYopABGRio24qSo2xQBCRmuR1e2+zX7FR8snzD+tdgRWTrln51m0wHzxDP4d6yqx5omtGfLIwEPAxVlHIWqEL9qsK56V5rR6sZFzzwi5JAycUxr3yQSD83pVcqkwUOoO1twz2NTCEPUXsa2vuctrF9qV5M0cGAuOSTiqFr4du5k3S3OCf7vWuqvrDecqPmqtHBNCRnIA6VpGSsb02kynH4Yukj5upBjuSKki8LzNGQLuQt9R/hWt9oJUb5OfUmo2ukRiFnVj/ALBBp8x186OW1DwtIm5hdOHAyCTTNPOrW58hZlkTttJyK6OZXuBtG45pbSxMJ96JS0OWrZvQmtZ51QxzAZz3qyz8USkJk96rNPn61infcwaS2FJ5Jqxptubq+TA+VTuaqobNdLo1oILTzSPnk5/CuyjDmZxV58sTRHAxS0lLXonmDhS00dacBk0APAqVBUY6VMg9KBliMVYQVBFVhaAJR1p4GDTF608dKkaHgClwPSkWnVQzJI71GxqVqhcVJA1qQDtS4pKAFxS0g6UoOaAFpGpaaetADaaV4qTtTTQBERg1T1RtumXBx/D/AIVobazNdbZpE59do/UVMtmVBXkkcSj4arCvmqrr3FEcmDzxXlvU9e1i4HxVmKXkVRWQN3GasQgnoTUNFqRfyJOcU9YdwxUKKQelX4F5GanU03KU2lecpzgCq9toUSZzySeuK6Dcir2NMWSPPSrjexGhnGyWJcA1CyhRwK0pnVs4IrNmHzGo1LKVwfaqDkhjWlKAFOazJW/ebRxmrijOUrGlpNsbu4C/wj5m+ldgFAG1RgAYrM0C3SPTlmA+eTOT7ZrT716lGHLE8qvU5pBjHFLtNKBS4rYwEHpT1GDTMVIuKAHrU0fWoVqdOtAywlTL1qJO1TKOKB3JV605elNHSnjpSAeKWiimK5lsKhYVOfSomHFJCGYGKacU89KYRyKGACl6UDikBpAO7UhFHWigBD0pMU49KFGaAG9a5/xNdosC2gI3N8x9h2rpAowSeAOteSR6lNquuatdMcolyYIx6KpP+NYV52ib4eHNNMvYBBqrOChyKuKKjlj3jpXnXPWtoVIZsnOa1beYYHSsCZGik3LwasW94pADEg96q1zOzR1Ecq4ycGrayjHFc5HdKB1qxHdg4y1LlFz9DYMwDFc0Bu45/Gs1rkEZFILsrj39apId7l+ST0NUppucjjFRSXYIJZuc1Rmu15O6pcR81h89zkNz3qm0mTULSlz14pR0zVbC1ep6Do6ldJtxn+Gr2KraUA2k2zLyCuKtYr1IP3UeTP4mB9qXtRjFGasgWhaKcgoAkWp0FQr0qdKBkyVYXpUCdqnSgCTAp1IKdSHccOtPwKYvHNPoAyjUZ5FSN1qOmSMPQ0ynt3pg60gDPtSUppKQADTqbtJp4jbFACYp8aE8CnJEWIq9FDtHNA7FZ4T9llA67D/KvEdCBjFwh4b7RIX+ua9/RBz9K8MuLRtP8VavaEY23buvurEkGuTEPQ7cHa5pKOKcUBHvT4RlalMdcJ6CMa6iBxWfJH7c1tzxEis+SLk00waM55pYxwePelXVSv3xjFTSxZqrJAG4IFWmZuBOdZXGQ3Won1s5ABJI6YFVjp4PtSi0WMdKq6FyMf8A2hcyH5e55yKsRK8n+sbPsKgVcHgVdtkJNLmDkJ1QAcUvSpdu1aifAXJqVqaW0Ou8C3bXemXMTkYgmKgZ9zXSFcV5b8NtTMPjTVLBz8smHXP4f4163LH19RXo0X7tjya0feuVDRinY5pp6mtzAUCpFAqNalWgB4qdKiUVKtAEydqnSoE7VOtAyUYzTx0pg+9Th1pAhwp4IxTaKCjKJyTTKXvTliZj0xSIIWHNAjdvugmr8dopwScmrAjCjAAH0ouOxmi1Yn5uKlS0z0BPvVsJuYD86sebDEmByfYUmykirFaqp+YUk0SjgU57yMSBV70rDe2PWkNobDF7VaKdAKdHHtFS7OBQIbGmCK8o+ItmLLxfa3mMR3UOwnHVlJ/xFevKuGrjviJo/wDaOhtLGha4t/3keB6EZH5fyrGrG8TfDy5ZnEQA7QatKvH1qjpNyLq0jbjJAP6VphTtrzj1irNCCvvWXNHgnitx1+XkVnzoMnNFwsZTw5HSqz25/umtkQZXIpjw4HIphYwmRl4waj8tmPPFa0lvk1G1tgZxRcLFFIvmAArQgh2jOKSC3BkGeOa0liAXFDYrFGRDjmqkoO3FacsfBqjOu1GbsBRcdjnvDMptfipbbCMS7Ub9P8K+hJU2yDjhq+Z9LuynxC06f1uo1P4kCvp+4UeVE+Owrvo9DzMSrMy7iMxt04NVjWtLF5qbT+dZskTIxHNdSZyNDUqYdfwqJeBUi9aBEy9KmXpUCnipo6YEqjpUy9KjHQVMooAeOtSDrTF96eKQ0OFOpMUtAyGOyXrtqb7PxwBipDOqZRMM2OfaoHZn+8fyqbjsBj2E4NL8u3JIqu0RJ4phgY9aVxhJOGGE6Hv61DO+yIkVOtvzk81XvQPKYD0pDSIrIfabkHqF5NbcUHOTWX4agJguHYfx7R/P+tbrfLwKOgpbjQAKlUcc0xBnk1MBTJGqvrUF/EJICCAR3zVodaR0DowPQ1LQJ2dzwKGJtI8QXunyDaiyMYx/sk8fpXRJgqOaPiFpr2+o2+qKPlP7qQj8SP61VsZy8KnOa82qrSPapS5ok8gYdqo3CbjWqwyvrVaSNSTWZoZQcxPgnipzIjLST24JJHNVWSUfdFMESOyD0qtJICMCnGOQ/ep8VuMjIoALSFm5xWgYiF5qaGEJGMChxmkOxQmTg1haxN5Vuyj0robg7VNclrEhkZvQU0JnN6LD5ni7S8jObyLP/fQr6rlXfYIcfwAj8q+ZfD1uf+Eq0s/9PMZ6/wC0K+nEO7Toj/sD+Vd1B6nnYpFCPJBBpk0AY5xyKlx5b57VYCBxkV1HGZ32Ze4qNrdk5xxVwsVdo3GCp4I9Kadx4HSncLXKYGBUimpTEp9qYybKdyWrEqHpmplPFVVPTmplbimIsg5p46dagVqlU8UDJRjHWlqMNTtxpAIqBRS45p+3FIO9Z3NRuKCvFLj5qd2oEREYFUL37pFXZDxVGbEhwaQzT0hBBpysf4mLVaGTye5pkMZW3hToAgyKmjGTz2prYhkirxT8U1pFUdaqXWqWdmubm5hhH/TRwv8AOmmTZsunrS4rlrrxzodqSBfrK392JS36gYqunxAspG/dW8zj3IFLmQ+SRa8XaQNT0e4hx8zDj65yP5V5RpU5i+RuoNeu22uW2rHyhG0bsuQG5z7V5XrVqtj4kuY0G1SwYAehArkxMU1dHo4ST+Fm3Gd6ZFNkX5cVHYuGTn0q26AjiuI7jOaPJqKSAgZFXWQZ96QrkYxmi40ZrRmrFvC3Wp1h3N0q2sewUXAh2bVzUDc1am9BVfYeSRTSCxm3x2xH61yd2N0hHc10urkEKwZgVOcA9f8AP9K5uQZY5FXFESHeHoM+JtNx2uE/mK+jY0/4lkI/2BXgvhW3M3iOzCjJV930xzX0HFEPskaHsoFdmH7nnYx6pGVMhwDT7bpUs8ZQ4NRp8p9q6jkC6gLL52OgwfpVXyyOhrWjwylTgg9RWWCbe6aGTp2PqKBoQMR1ANTxyQMCrxk0jopORURj7jrRcZaaxhkXdGQPpVaS0ki/2h6ipUYg5BINW0lVhtkGPfsaLiaMxetSq3FWprIP8yYP0qm6NGeR0qrkWJAwp+4VAGz3p4PHUUwuWs5pOhozQOTWJqB6Uh6U49KY3QUAV5T71VxlxVuRc1Eqc9KANbeFjXP90Vg6j4qgsXMMKCecdVBwF+pp2rSTyW3lxsUG3HB5rnLPSvnyR3oEkOvNb1fUEISQ26HtDkH8+tcveadcyuWmkll93Ysf1r0ODTVC/dqV9KRhyoqLM0TR5aNOw3T9KtQWTJ90kGu1utBBbKACq39klOq0iilpEzx3UaOxTnhx1FXvFHh9L5v7WjIDxr+9iUYBxnBz6U02Zj6jHvVxL+WAIGUMFPze6+lS1dWY03F8yOLspGLAH9OlbEbZXmukOlabfruESqH+7IgAP41mX2jT2Pz8vEP4vauSdJrVHfTrxloZzx7hkCkWLnpViJN5wKsRWzB+gIrKx0JoqC3KnNO4x2q9cKqR9OapPgJRYa1KsgBNROPlOTUjnmocF22qCzegFUlcUnbc5/U1O4k9O1YjLlzXZXfh/U74fu7Vh7sQKhtPA9806/aZI0TPIVsn8KtQZjKpHuS/DewL6vcXjj93DHjOOhJH9Afzr0GfxvpVu5hRnlZOCUU4/Osq6tI9M0j+zbRfLLH97t9PQ469a4y/urez3pGPNnHAUDgH3NdkI8isefVaqSuz0VvGWizJumnMA9ZEIAq/DNFcRrLBIssTcq6nINeEzx3V7L5kxzjooPyj8K0tDlutKvFkt5GQ9CAcA/Ud60UmZOCPbEcqabqEXnwiaP78fJx3FYWl61JPGPPQZ/vLW3BdRyn92+T3BFaXM2mipHLuQHvUwPy06a2w5dBweSPT6U2OkUSotPIyMUi4xSg80AJHPJCfVfQ1KdlwpAIDehqJhkUwr3ouKxFLA8TnHSmBuKsBySEbr2JoZE3cjmquQ0S4oHBpQQRRiszQWmtzSk4oHJoAjKZNAQVLtpcUAVZYd4qOO3CVdIzTSvFAEagKKlVh0pu3imkEc0ASsgYVXkt1PapwaXg0WKMyW0Vu1U5bEgnA4rf2A07yVx04qXEq5yIS5syTB0PVSMg1pRapcSQBXhQ44bOea1pbNGGcc1HDp43OMdRRyg5GG1qsu54ECSddvY/Ss03vlkhuCO1dkNPA6/yrE13QPtsfmw4S4X+I9GHv6Vz1KLeqOmhiLO0jn5rwyt96omeWYhIkaRvRRmodL0y7vLmSJgYUibEjuMY9vrXRylrKzMWnoYVUElzyx/PpWNOi5bnXWxMYaR1ZnW2nJbx/aNWlWNMZEW7DGtC21/SrdStrauOeDjr+tc8LCe6nMsrMznkk1qWmlkc4/SuuNNRVkcFSpKbu2aa62ly+2a1k29ijc/qaSbUDGP8AQoMP/fl5I+mKkhsdoHHNTfZgOMVdjLQ5m7gu7sbZHOO4Xis4aEM5K12v2TPOKetmOpFFmFzjRooVeFpv9lbGB2V2pshjOKiaxGemaaQrmVYRGMDjFaqgnmnpahMYFTrFxVIQyO4lQ4EjY9KmiYk59aiMdSICKZJaHSnLzUadKk4pXAUnHFNzQTSCgCKRQaQO4GM1MVzTNhpgf//Z\"\n" +
"        }\n" +
"    ]\n" +
"}";
        
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = new ProcessWorkflowActivity_JSNObject();
        try {
            data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        JWT_Authenticate jwt = new JWT_Authenticate();
        try {
            jwt = mapper.readValue(jsonJWT, JWT_Authenticate.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        KYC object = new KYC();
        object = assignAllItem(data.getItem(), jwt);
        System.out.println("Object:"+mapper.writeValueAsString(object));
        System.out.println("JWT:"+mapper.writeValueAsString(jwt));
        try {
            object = mapper.readValue(mapper.writeValueAsString(jwt), KYC.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        object = assignAllItem(data.getItem(), jwt);
        System.out.println("KYC:"+mapper.writeValueAsString(object));
    }
}
