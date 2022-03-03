package org.neointegration.mule.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.Status;
import org.apache.commons.jexl3.*;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class PluginUtil {

    private static JexlEngine jexl = new JexlBuilder().create();
    private static String ramlFilePath = null;

    public static List<Rule> loadRuleFile(String path, String ruleFileName) throws IOException {
        List<Rule> rules = null;

        validateRuleAgainstSchema(path + File.separator + ruleFileName,
                "/rule-schema.json");

        ObjectMapper mapper = new ObjectMapper();
        rules = Arrays.asList(mapper.readValue(Paths.get(path + File.separator + ruleFileName).toFile(), Rule[].class));

        return rules;
    }


    public static boolean isNullOrEmpty(String str) {
        if(str == null) return true;
        if("".equals(str)) return true;

        return false;
    }
    public static boolean isNotNullAndEmpty(String str) {
        if(str != null && !"".equals(str)) return true;
        return false;
    }
    public static boolean isNotNull(Object obj) {
        if(obj != null) return true;
        return false;
    }
    public static boolean isNull(Object obj) {
        if(obj == null) return true;
        return false;
    }
    public static boolean isNullOrEmpty(Object obj) {
        if(obj == null) return true;

        if(obj instanceof String) {
            return "".equals(obj);
        } else if(obj instanceof List) {
            return ((List)obj).size() == 0;
        } else if(obj instanceof Map) {
            return ((Map)obj).size() == 0;
        } else if(obj instanceof Set) {
            return ((Set)obj).size() == 0;
        } else if(obj.getClass().isArray()) {
            Class<?> componentType;
            componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                if (boolean.class.isAssignableFrom(componentType)) {
                    return ((boolean[]) obj).length == 0;
                }

                else if (byte.class.isAssignableFrom(componentType)) {
                    return ((byte[]) obj).length == 0;
                }

                else if (char.class.isAssignableFrom(componentType)) {
                    return ((char[]) obj).length == 0;
                }

                else if (double.class.isAssignableFrom(componentType)) {
                    return ((double[]) obj).length == 0;
                }

                else if (float.class.isAssignableFrom(componentType)) {
                    return ((float[]) obj).length == 0;
                }

                else if (int.class.isAssignableFrom(componentType)) {
                    return ((int[]) obj).length == 0;
                }

                else if (long.class.isAssignableFrom(componentType)) {
                    return ((long[]) obj).length == 0;
                }

                else if (short.class.isAssignableFrom(componentType)) {
                    return ((short[]) obj).length == 0;
                }
            } else {
                return ((Object[])obj).length == 0;
            }
        }
        return false;
    }

    static Result createResultObject(File xmlFile,
                                     Rule rule,
                                     NodeList nodes,
                                     boolean passed) {
        Result result = new Result();
        result.setFileName(xmlFile.getName());

        if(nodes == null) result.setNumberOfNode(1);
        else result.setNumberOfNode(nodes.getLength());

        if(passed) result.setStatus(Status.PASSED);
        else result.setStatus(Status.FAILED);

        return result;
    }

    static Object eval(Object obj, String... jexlExpressions) {
        Object previous = obj;
        try {
            for(String jexlExp : jexlExpressions) {
                JexlExpression e = jexl.createExpression(jexlExp);
                JexlContext jc = new MapContext();
                jc.set("obj", previous);
                previous = e.evaluate(jc);
            }
        } catch (Exception ignore) {
            previous = null;
        }

        return previous;
    }


    public static String getName(String path) {
        if(PluginUtil.isNullOrEmpty(path)) return null;
        List<Character> array = new ArrayList<Character>();
        StringBuilder sb = new StringBuilder();
        char[] paths = path.toCharArray();
        for(int index = (paths.length - 1); index > -1; index--) {
            if(paths[index] == '/' || paths[index] == '\\') {
                break;
            }
            array.add(paths[index]);
        }
        for(int index = (array.size() - 1); index > -1; index--) {
            sb.append(array.get(index));
        }
        return sb.toString();
    }

    public static File getRAMLFile(String projectDir) {
        try {
            // Cache the root RAML file name per run.
            if (PluginUtil.isNotNull(ramlFilePath)) return new File(ramlFilePath);
            File ramlPath = new File(projectDir + File.separator + "src/main/resources/api");
            if (ramlPath.exists()) {
                File[] files = ramlPath.listFiles();
                for (File file : files) {
                    if(file.isDirectory()) continue;
                    try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String str = null;
                        while ((str = reader.readLine()) != null) {
                            if (PluginUtil.isNotNullAndEmpty(str)) {
                                if (str.trim().equals("#%RAML 1.0")) {
                                    ramlFilePath = file.getAbsolutePath();
                                    return file;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch(Exception ignored) {}
        return null;
    }

    public static void validateRuleAgainstSchema(String ruleFilePath, String ruleSchemaFileName) throws FileNotFoundException {
        InputStream schemaFileStream = JSONValidator.class.getResourceAsStream(ruleSchemaFileName);
        File ruleFile = new File(ruleFilePath);

        JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaFileStream));
        JSONArray jsonArray = new JSONArray(new JSONTokener(new FileReader(ruleFile)));

        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonArray);
    }
}
