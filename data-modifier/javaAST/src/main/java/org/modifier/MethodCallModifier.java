package org.modifier;

import com.github.javaparser.ast.expr.MethodCallExpr;

public class MethodCallModifier extends MethodNameModifier{
    public MethodCallExpr replaceMC(MethodCallExpr mc) {
        if(hm.containsKey(mc.getNameAsString())){
            mc.setName("beena");
        }
        return mc;
    }
}
