package com.ql.util.express.example;

import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.*;
import com.ql.util.express.instruction.op.OperatorBase;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianqiao on 17/6/4.
 */
public class RuleTest {
    
    
    private static RuleTest singleton = new RuleTest();

    private Map<String, LimitAndMonitorRule> simpleMap = new HashMap<String, LimitAndMonitorRule>();
    private Map<String, String> conbinationMap = new HashMap<String, String>();

    public void acceptSimpleRule(String key, String value){
        JSONObject valueObj = JSONObject.parseObject(value);
        simpleMap.put(key, LimitAndMonitorRule.parse(valueObj));
    }

    public void acceptCombinationRule(String key, String value){
        conbinationMap.put(key, value);

    }

    public void functionABC(Long a,Integer b,String c)
    {
        System.out.println(true && false);
    }

    public Long count(String key)
    {
        return simpleMap.get(key).getTimes();
    }
    public boolean warn(String key, Integer trigger)
    {
        return simpleMap.get(key).getTimes()< trigger;
    }

    @Test
    public void testrule() throws Exception {
        String simpleRuleKey = "repost";
        String simpleRuleValue = "{\"parameters\":[\"log_user_id\"],\"periodLength\":1,\"timeUnit\":\"MINUTES\",\"times\":50, \"uri\":\"\"}";
        acceptSimpleRule(simpleRuleKey, simpleRuleValue);
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("rule", this,"functionSimpleRule",new Class[]{String.class},null);
        String exp = "rule(key)>10";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("key",simpleRuleKey);
        Object execute = runner.execute(exp, context, null, false, false);
        System.out.println(execute);
    }

    ExpressRunner runner;
    @Before
    public void init() throws Exception {

        String repostRuleKey = "repost";
        String repostRuleValue = "{\"parameters\":[\"log_user_id\"],\"periodLength\":1,\"timeUnit\":\"MINUTES\",\"times\":50, \"uri\":\"\"}";
        acceptSimpleRule(repostRuleKey, repostRuleValue);
        runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("count", this,"count",new Class[]{String.class},null);
        runner.addFunctionOfServiceMethod("warn", this,"warn",new Class[]{String.class, Integer.class},null);
    }

    @Test
    public void testRule3() throws Exception{
        String exp = "count(\"repost\")>10 && warn(\"repost\", times)";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("times", 10);
        Object execute = runner.execute(exp, context, null, false, false);
        System.out.println(execute);
    }

}
