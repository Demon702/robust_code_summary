package org.modifier;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import java.util.HashMap;
import java.util.Map;

public class VariableNameModifier extends ModifierVisitor<Void> {
    private int cntr = 1;
    private String newName, oldName;
    public static Map<String, String> hv = new HashMap<String, String>();

    @Override
    public VariableDeclarator visit(VariableDeclarator md, Void arg) {
        super.visit(md, arg);
//        System.out.println("Variable Old Name Printed: " + md.getName());
        oldName = md.getNameAsString();
        if(hv.containsKey(oldName))
            newName = hv.get(oldName);
        else
            newName = "var_"+Integer.toString(cntr);
        hv.put(oldName, newName);
        md.setName(newName);
        cntr++;
//        System.out.println("cntr " + cntr);
//        System.out.println("Variable New Name Printed: " + md.getName());
        return md;
    }
}
