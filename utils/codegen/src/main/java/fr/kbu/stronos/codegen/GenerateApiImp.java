package fr.kbu.stronos.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.reflections.Reflections;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO : Documentation GenerateImplementations.java
 *
 * @author : Kevin Buntrock
 */
@SuppressWarnings("rawtypes")
public class GenerateApiImp {

  private static final String INDENT = "   ";

  private static boolean erased = false;

  private static boolean isRequestMapping(Class clazz) {
    Annotation[] annotations = clazz.getAnnotations();
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().getCanonicalName()
          .equals("org.springframework.web.bind.annotation.RequestMapping")) {
        return true;
      }
    }
    return false;
  }

  public static void main(final String[] args) throws IOException, ClassNotFoundException {

    final String directory = args[0];
    Class[] classArray = new Reflections("fr.kbu.stronos.api.web")
        .getTypesAnnotatedWith(RequestMapping.class).toArray(new Class[0]);

    System.out.println("############ STARTING CODE GENERATION");

    System.out.println("############ Found " + classArray.length + "webservice(s).");

    for (int i = 0; i < classArray.length; i++) {

      Class clazz = classArray[i];
      System.out.println("############ Got Class : " + clazz.getCanonicalName());

      if (!isRequestMapping(clazz)) {
        continue;
      }

      String interfaceName = clazz.getSimpleName();
      String className = clazz.getSimpleName().substring(1);
      String packagePath = clazz.getPackageName().replace(".", "\\");

      System.out.println(
          "############ Generate File : " + directory + "\\" + packagePath + "\\" + className);

      File directoryFile = new File(directory + "\\" + packagePath);

      if (!erased) {
        FileUtils.deleteDirectory(directoryFile);
        erased = true;
      }
      directoryFile.mkdirs();

      File classFile = new File(directoryFile, className + ".java");

      FileWriter fileWriter = new FileWriter(classFile);

      PrintWriter printWriter = new PrintWriter(fileWriter);

      printWriter.println("package " + classArray[i].getPackageName() + ";");
      printWriter.println("");
      Method[] methods = clazz.getMethods();

      // IMPORT SECTION
      printWriter.println("import org.springframework.web.bind.annotation.RestController;");
      /*
       * for (int j = 0; j < methods.length; j++) { Method method = methods[j];
       * printWriter.println("import "+method.getReturnType().getCanonicalName()+";"); }
       */
      printWriter.println("");

      //
      printWriter.println("@RestController");
      printWriter.println("public class " + className + " implements " + interfaceName + " {");
      printWriter.println("");


      for (int j = 0; j < methods.length; j++) {
        Method method = methods[j];
        printWriter.println(INDENT + "@Override");

        printWriter.print(INDENT + "public " + method.getGenericReturnType().getTypeName());
        // parametres
        printWriter.print(" " + method.getName() + "(");
        int k = 0;
        for (Type t : method.getGenericParameterTypes()) {
          if (k > 0) {
            printWriter.print(", ");
          }
          printWriter.print(t.getTypeName() + " " + method.getParameters()[k].getName());
          k++;
        }
        printWriter.println(") {");

        printWriter.println(INDENT + INDENT + "throw new RuntimeException(\"Not Implemented\");");
        /*
         * if (!method.getReturnType().getSimpleName().equals("void")) { printWriter.println(INDENT
         * + "return null;"); }
         */
        printWriter.println(INDENT + "}");
        printWriter.println("");
      }


      printWriter.println("}");

      printWriter.close();
    }
  }
}
