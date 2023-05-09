package org.modifier;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.printer.YamlPrinter;
import org.json.simple.JSONArray;
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
    private static String outputPath = "javaCorruptedCodeOutput/";

    private static String modifier(String code) throws IOException {
        CompilationUnit cu;
        String modifiedCode = null;
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
            modifiedCode = cu.toString();
//            System.out.println(cntr);
        }
        catch(ParseProblemException pe){
            System.out.println("cntr "+Integer.toString(cntr));
            System.out.println(code);
            System.out.println(pe.getMessage());
        }
        cntr++;
        return modifiedCode;
    }

    private static String writeCommentedCode(String code, String mode, String commentedCode) throws IOException {
        //add commented code
//        addCommentedCode cc = new addCommentedCode(mode);
        int[] indexes;
        int ind;
        try {
            indexes = IntStream.range(0, code.length())
                    .filter(i -> code.charAt(i) == ';').toArray();
            ind = indexes[(int)Math.floor(Math.random() * indexes.length)];
        }
        catch (IndexOutOfBoundsException e){
            ind = code.indexOf(";");
            System.out.println(e.getMessage());
        }
        String newModifiedCode = code.substring(0, ind+1)
                + "/* " + commentedCode + " */"
                + code.substring(ind + 1);

        return newModifiedCode;

    }

    public static void main(String[] args) throws Exception{

        List<File> filesInFolder = Files.walk(Paths.get(inputPath))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
//        System.out.println(filesInFolder);
        for(File filePath : filesInFolder){
            cntr = 1;
            System.out.println(filePath);
            String mode;
            String [] arr = String.valueOf(filePath).split("/", 0);
            mode = arr[arr.length-1].substring(0,arr[arr.length-1].indexOf("."));

            List<String> allLines = Files.readAllLines(filePath.toPath());
            Files.createDirectories(Paths.get(outputPath + "nameChanged" ));
            Files.createDirectories(Paths.get(outputPath + "commented"));
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                long lineCount = (long) Files.lines(filePath.toPath()).count();
                while ((line = br.readLine()) != null) {
                    Object obj = new JSONParser().parse(line);
                    JSONObject jsonObject = (JSONObject)obj;
                    String jCode = String.valueOf(jsonObject.get("code"));
                    String className = "class"+Integer.toString(cntr);
                    String code = "public class " + className + "{" +
                            jCode +
                            "}";
//                System.out.println(code);
                    jsonObject.remove("code_tokens");
                    String modifiedCode = modifier(code);
                    jsonObject.put("code", modifiedCode);
                    jsonObject.put("original_string", modifiedCode);

                    if(modifiedCode!=null){
                        try(FileWriter file = new FileWriter(outputPath + "nameChanged/" + mode + "_data.jsonl", true);
                            BufferedWriter bw = new BufferedWriter(file)){
                            bw.write(jsonObject.toJSONString());
                            bw.newLine();
                        }
                    }

//                    file.write(jsonObject.toJSONString());

                    int lineNo = (int)Math.floor(Math.random() * lineCount);
                    obj = new JSONParser().parse(allLines.get(lineNo));
                    jsonObject = (JSONObject)obj;
                    jCode = String.valueOf(jsonObject.get("code"));
                    String commentedCode = writeCommentedCode(code, mode, jCode);
                    jsonObject.put("code", commentedCode);
                    jsonObject.put("original_string", commentedCode);

                    if(commentedCode!=null){
                        try(FileWriter file = new FileWriter(outputPath + "commented/" + mode + "_data.jsonl", true);
                            BufferedWriter bw = new BufferedWriter(file)){
                            bw.write(jsonObject.toJSONString());
                            bw.newLine();
                        }
                    }

                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

