package com.ql.util.express.example;

import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.*;
import com.ql.util.express.instruction.op.OperatorBase;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
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

    public Long functionSimpleRule(String key)
    {
        return simpleMap.get(key).getTimes();
    }

    @Test
    public void testrule1() throws Exception {
        String simpleRuleKey = "repost";
        String simpleRuleValue = "{\"parameters\":[\"log_user_id\"],\"periodLength\":1,\"timeUnit\":\"MINUTES\",\"times\":50, \"uri\":\"\"}";
        acceptSimpleRule(simpleRuleKey, simpleRuleValue);
        ExpressRunner runner = new ExpressRunner();
//        runner.addFunctionOfServiceMethod("rule", this,"functionSimpleRule",new Class[]{String.class},null);
        String exp = "repost>10";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("repost", 100);
        Object execute = runner.execute(exp, context, null, false, false);
        System.out.println(execute);
    }

    @Test
    public void testrule() throws Exception {
        String simpleRuleKey = "repost";
        String simpleRuleValue = "{\"parameters\":[\"log_user_id\"],\"periodLength\":1,\"timeUnit\":\"MINUTES\",\"times\":50, \"uri\":\"\"}";
        acceptSimpleRule(simpleRuleKey, simpleRuleValue);
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("rule", this,"functionSimpleRule",new Class[]{String.class},null);
        String exp = "rule(\"repost\")>10";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
//        context.put("key",simpleRuleKey);
        Object execute = runner.execute(exp, context, null, false, false);
        System.out.println(execute);
    }

    @Test
    public void test1() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("abc", singleton,"functionABC",new Class[]{Long.class,Integer.class,String.class},null);
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a",1L);
        context.put("b",2);
        context.put("c","3");
        runner.execute(exp, context, null, false, false);
    }

    @Test
    public void test2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("abc", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                Long paramA = Long.valueOf(list[0].toString());
                Integer paramB = Integer.valueOf(list[1].toString());
                String paramC = list[2].toString();
                singleton.functionABC(paramA,paramB,paramC);
                return null;
            }
        });
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a","1");
        context.put("b","2");
        context.put("c","3");
        runner.execute(exp, context, null, false, false);
    }
    
    @Test
    public void test3() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("abc", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                Long paramA = Long.valueOf(list[0].toString());
                Integer paramB = Integer.valueOf(list[1].toString());
                String paramC = list[2].toString();
                singleton.functionABC(paramA, paramB, paramC);
                return null;
            }
        });
        
        OperatorBase function = runner.getFunciton("abc");
        System.out.println("function = " + ToStringBuilder.reflectionToString(function, ToStringStyle.MULTI_LINE_STYLE));
        
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a", "1");
        context.put("b", "2");
        context.put("c", "3");
        
        InstructionSet instructionSet = runner.getInstructionSetFromLocalCache(exp);
        String[] outFunctionNames = runner.getOutFunctionNames(exp);
        String[] outVarNames = runner.getOutVarNames(exp);
        System.out.println("before execute instructionSet = " + instructionSet);
        System.out.println("outFunctionNames = " + ToStringBuilder.reflectionToString(outFunctionNames, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println("outVarNames = " + ToStringBuilder.reflectionToString(outVarNames, ToStringStyle.MULTI_LINE_STYLE));
    
        runner.execute(exp, context, null, false, false);
        
        instructionSet = runner.getInstructionSetFromLocalCache(exp);
        outFunctionNames = runner.getOutFunctionNames(exp);
        outVarNames = runner.getOutVarNames(exp);
        System.out.println("after execute instructionSet = " + instructionSet);
        System.out.println("outFunctionNames = " + ToStringBuilder.reflectionToString(outFunctionNames, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println("outVarNames = " + ToStringBuilder.reflectionToString(outVarNames, ToStringStyle.MULTI_LINE_STYLE));
    }
}
