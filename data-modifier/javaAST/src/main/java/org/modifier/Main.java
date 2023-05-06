package org.modifier;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.printer.YamlPrinter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static int cntr;
    private static String inputPath = "CodeT5/data/summarize/java";
    private static String outputPath = "javaCorruptedOutput/";

    private static void modifier(String code, String outFolder) throws IOException {
        CompilationUnit cu;
        try{
            cu = StaticJavaParser.parse(code);
            // Now comes the inspection code:
            YamlPrinter printer = new YamlPrinter(true);
//            System.out.println(printer.output(cu));
            ModifierVisitor<Void> methodNameVisitor = new MethodNameModifier();
            methodNameVisitor.visit(cu, null);
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
            mnm.hm.clear();
            vnm.hv.clear();
            String modifiedCode = cu.toString();
//            System.out.println(modifiedCode);
            int idx = modifiedCode.indexOf("{");

            FileOutputStream outputStream = null;
            try{
                outputStream = new FileOutputStream(outputPath + "/" + outFolder + "/file_"+Integer.toString(cntr)+".java");
                byte[] strToBytes = modifiedCode.getBytes();
                outputStream.write(strToBytes);
            }
            catch (IOException e){
                System.out.println((e.getMessage()));
            }
            outputStream.close();

            //add commented code
            addCommentedCode cc = new addCommentedCode(outFolder);
            int[] indexes;
            int ind;
            try {
               indexes = IntStream.range(0, modifiedCode.length())
                    .filter(i -> modifiedCode.charAt(i) == ';').toArray();
               ind = indexes[(int)Math.floor(Math.random() * indexes.length)];
            }
            catch (IndexOutOfBoundsException e){
                ind = modifiedCode.indexOf(";");
                System.out.println(e.getMessage());
            }
            String newModifiedCode = modifiedCode.substring(0, ind+1)
                    + cc.commentedCode
                    + modifiedCode.substring(ind + 1);
           outputStream = null;
            try{
                outputStream = new FileOutputStream(outputPath + "/commentedCode/" + outFolder + "/file_"+Integer.toString(cntr)+".java");
                byte[] strToBytes = newModifiedCode.getBytes();
                outputStream.write(strToBytes);
            }
            catch (IOException e){
                System.out.println((e.getMessage()));
            }
            outputStream.close();

        }
        catch(ParseProblemException pe){
            System.out.println("cntr "+Integer.toString(cntr));
            System.out.println(code);
            System.out.println(pe.getMessage());
        }
        cntr++;
    }

    public static void main(String[] args) throws Exception{

        List<File> filesInFolder = Files.walk(Paths.get(inputPath))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        System.out.println(filesInFolder);
        for(File filePath : filesInFolder){
            cntr = 1;
            System.out.println(filePath);
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Object obj = new JSONParser().parse(line);
                    JSONObject jsonObject = (JSONObject)obj;
                    String jCode = String.valueOf(jsonObject.get("code"));
                    String className = "class"+Integer.toString(cntr);
                    String code = "public class " + className + "{" +
                            jCode +
                            "}";
//                System.out.println(code);
                    String [] arr = String.valueOf(filePath).split("/", 0);
                    String outFolder = arr[arr.length-1].substring(0,arr[arr.length-1].indexOf("."));
                    modifier(code,outFolder);
//                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File directory=new File("javaCorruptedOutput/train");
        int fileCount=directory.list().length;
        System.out.println("Train File Count:"+fileCount);

        directory=new File("javaCorruptedOutput/test");
        fileCount=directory.list().length;
        System.out.println("Test File Count:"+fileCount);

       directory=new File("javaCorruptedOutput/valid");
        fileCount=directory.list().length;
        System.out.println("Valid File Count:"+fileCount);

        directory=new File("javaCorruptedOutput/commentedCode/train");
        fileCount=directory.list().length;
        System.out.println("Train File Count:"+fileCount);

        directory=new File("javaCorruptedOutput/commentedCode/test");
        fileCount=directory.list().length;
        System.out.println("Test File Count:"+fileCount);

        directory=new File("javaCorruptedOutput/commentedCode/valid");
        fileCount=directory.list().length;
        System.out.println("Valid File Count:"+fileCount);

    }
}

