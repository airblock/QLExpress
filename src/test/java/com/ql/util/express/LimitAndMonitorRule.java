package com.ql.util.express;

import com.alibaba.fastjson.JSONObject;
import lombok.*;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitAndMonitorRule {

    /**
     * uri是匹配的第一步
     */
    @Setter
    private String uri;
    /**
     * parameters元素的集合
     */
    private TreeSet<String> parameters;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;
    /**
     * 监控的时间间隔, 同时也是触发后用户需要等待的时间
     */
    private Long periodLength;
    /**
     * 达到警告的访问次数
     */
    private Long times;
    /**
     * md5 code
     * 暂时的 rule ID
     */
    private String md5;

    /**
     * 统计数据分划个数
     * 默认4
     */
    private Integer parts;

    public static LimitAndMonitorRule parse(JSONObject jsonObject) {
        return LimitAndMonitorRule.builder().uri(jsonObject.getString("uri"))
                .parameters(new TreeSet(jsonObject.getJSONArray("parameters").toJavaList(String.class)))
                .timeUnit(TimeUnit.valueOf(jsonObject.getString("timeUnit")))
                .periodLength(jsonObject.getLong("periodLength"))
                .times(jsonObject.getLong("times"))
                .parts(1)
                .build();
    }

}
