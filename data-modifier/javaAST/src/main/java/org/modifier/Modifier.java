package org.modifier;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.printer.YamlPrinter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Modifier{
    private static String FILE_PATH = "src/main/java/java/code/sample/fibonacci.java";
    private static int cntr = 1;
    private static String path = "src/main/java/java/code/sample";

    public static void main(String[] args) throws Exception{

        List<File> filesInFolder = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        for(File FILE_PATH : filesInFolder){
            // Parse the code you want to inspect:
            CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
            // Now comes the inspection code:
            YamlPrinter printer = new YamlPrinter(true);
//        System.out.println(printer.output(cu));
            ModifierVisitor<Void> methodNameVisitor = new MethodNameModifier();
            methodNameVisitor.visit(cu, null);
//        System.out.println(printer.output(cu));

            MethodNameModifier mnm = new MethodNameModifier();
            cu.walk(MethodCallExpr.class, e -> {
                if (!(mnm.hm.containsKey(e.getNameAsString()))) {
                    return;
                }
                // Rename method
                e.setName(mnm.hm.get(e.getNameAsString()));
            });

            ModifierVisitor<Void> variableNameVisitor = new VariableNameModifier();
            variableNameVisitor.visit(cu, null);

            VariableNameModifier vnm = new VariableNameModifier();
            cu.walk(NameExpr.class, e -> {
                if (!vnm.hv.containsKey(e.getNameAsString())) {
                    return;
                }
                // Rename method
                e.setName(vnm.hv.get(e.getNameAsString()));
            });

            cu.walk(Parameter.class, e -> {
                if (!vnm.hv.containsKey(e.getNameAsString())) {
                    return;
                }
                // Rename method
                e.setName(vnm.hv.get(e.getNameAsString()));
            });


            String modifiedCode = cu.toString();
            FileOutputStream outputStream = null;
            try{
                outputStream = new FileOutputStream("file_"+Integer.toString(cntr)+".java");
                byte[] strToBytes = modifiedCode.getBytes();
                outputStream.write(strToBytes);
                cntr++;
            }
            catch (IOException e){
                System.out.println((e.getMessage()));
            }
            outputStream.close();

        }

    }
}
