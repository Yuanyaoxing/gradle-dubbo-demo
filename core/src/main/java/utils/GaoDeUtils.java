package utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.others.Location;
import core.others.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import properties.GaoDeMapProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 高德地图
 *
 * @author sxy
 * @version 1.0
 * @date 2019/4/2 17:54
 **/
@Component
@Slf4j
@ConditionalOnClass({ObjectMapper.class,HttpUtils.class})
public class GaoDeUtils {
    private static ObjectMapper objectMapper;
    private static GaoDeMapProperties gaoDeMapProperties;
    private final static String URL = "http://restapi.amap.com/v3/geocode/geo";

    public GaoDeUtils(ObjectMapper objectMapper, GaoDeMapProperties gaoDeMapProperties) {
        GaoDeUtils.objectMapper = objectMapper;
        GaoDeUtils.gaoDeMapProperties = gaoDeMapProperties;
    }

    /**
     * 地址转经纬度
     */
    public static Location addressToLocation(String address) {
        Location locationBean = null;
        Map<String, Object> params = new HashMap<>(10);
        params.put("key", gaoDeMapProperties.getKey());
        params.put("address", address);
        try {
            String result = HttpUtils.get(URL, params);
            JsonNode jsonNode = objectMapper.readTree(result);
            if ("0".equals(jsonNode.findValue("status").textValue())) {
                String location = jsonNode.findValue("location").textValue();
                if (location != null && !"".equals(location)) {
                    locationBean = new Location().setLongitude(location.split(",")[0]).setLatitude(location.split(",")[1]);
                }
            }
        } catch (Exception e) {
            log.warn("地址转换经纬度失败:{}", e.getMessage());
            throw new ValidateException("地址转换经纬度失败:" + e.getMessage());
        }
        return locationBean;
    }
}
