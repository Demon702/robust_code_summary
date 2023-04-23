package org.modifier;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import java.util.HashMap;
import java.util.Map;

public class MethodNameModifier extends ModifierVisitor<Void> {
    private int cntr = 1;
    private String newName, oldName;
    public static Map<String, String> hm = new HashMap<String, String>();

    @Override
    public MethodDeclaration visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);
//        System.out.println("Method Name Printed: " + md.getName());
        if(!md.getNameAsString() .equals("main")) {
            oldName = md.getNameAsString();
            newName = "func_"+Integer.toString(cntr);
            hm.put(oldName, newName);
            md.setName(newName);
            cntr++;
        }
//        System.out.println("cntr " + cntr);
//        System.out.println("Method Name Printed: " + md.getName());
        return md;
    }

}

