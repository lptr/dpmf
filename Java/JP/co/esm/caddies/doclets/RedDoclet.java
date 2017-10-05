/*
 *   Copyright (C) Eiwa System Management 1998
 *  This program is free software.
 *
 *  This program is provided AS IS, with NO WARRANTY.
 */
package JP.co.esm.caddies.doclets;

import com.sun.javadoc.*;
import com.sun.tools.javadoc.*;
import com.sun.tools.doclets.*;
import com.sun.tools.doclets.standard.*;
import java.util.*;
import java.io.*;

/**
  * The RedDoclet class.
  * <br>
  * This is a documentatin generator.
  * This documentation generator utilizes javadoc to create
  * a '*.red' file importable into Rational Rose(TM) from '*.java' files.
  *
  * For compiling, you need JDK1.2beta3 or later.
  * @version 1.0 $Revision: 1.3 $ $Date: 1999/02/15 02:47:20 $
  * @author hiranabe, okamura
  */
public class RedDoclet {

    /**
      * command options. dest directory,
      */
    public static Configuration configuration;

    protected RootDoc root = null;

    protected static final String NL = System.getProperty("line.separator");
    protected static final String TAB = "\t";
    protected static final String TAB2 = "\t\t";
    protected static final String TAB3 = "\t\t\t";
    protected static final String TAB4 = TAB2 + TAB2;

    /**
     * The "start" method as required by Javadoc.
     *
     * @param RootDoc
     * @see com.sun.javadoc.RootDoc
     * @return boolean
     */
    public static boolean start(RootDoc root) throws IOException {
        try {
            //System.out.println("Setting command-line options...");
            configuration().setOptions(root);
            //System.out.println("done.");
            (new RedDoclet()).startGeneration(root);
        } catch (DocletAbortException exc) {
            return false; //message has already been displayed
        }
        return true;
    }

    /**
     * Generate Packages.red file, for the Package Listing given by the
     * user on the Command Line. Also generate Classes Files.
     *
     * @see com.sun.javadoc.RootDoc
     */
    protected void startGeneration(RootDoc root) throws DocletAbortException {
        this.root = root;
        PackageDoc[] packages = root.specifiedPackages();
        generatePackageFiles(packages);

        ClassDoc[] classes = root.specifiedClasses();
        generateClassFiles(classes);
    }

    /**
     * Return the configuration instance. Create if it doesn't exist.
     * Override this method to use a different
     * configuration.
     */
    public static Configuration configuration() {
        if (configuration == null) {
            configuration = new ConfigurationStandard();
        }
        return configuration;
    }

    /**
     * Generate Packages.red file, for the Package Listing given by the
     * user on the Command Line. Also generate Classes Files.
     *
     * @see com.sun.javadoc.RootDoc
     */
    protected void generatePackageFiles(PackageDoc[] packages) throws DocletAbortException {
        for(int i = 0; i < packages.length; i++) {
	    genPackageDocumentation(packages[i]);
        }
    }

    /**
     * Generate Class.red file, for the Class Listing given by the
     * user on the Command Line. Also generate Classes Files.
     *
     * @see com.sun.javadoc.RootDoc
     */
    protected void generateClassFiles(ClassDoc[] classes) {
        for(int i = 0; i < classes.length; i++) {
	    genClassDocumentation(classes[i]);
        }
    }

    /**
      * Generates Package Documentation.
      * Generate <package-name>.red.
      */
    void genPackageDocumentation(PackageDoc pkg)
	throws DocletAbortException {
	    String fileName = pkg.name() + ".red";
	    //root.printNotice("Generating " + fileName + "...");
	    System.out.println("- Generating " + fileName + "...");
	    PrintWriter printWriter;
	    try {
		printWriter = openFile(fileName);
	    } catch (IOException e) {
		root.printError("NL + Failed to generate " + pkg.name() + ".red." + NL);
		throw new DocletAbortException();
	    }

	    //ClassDoc[] interfaceDocs = pkg.interfaces();
	    ClassDoc[] allClassDocs = pkg.allClasses();

	    redStart(printWriter);
        //for (int i = 0; i < interfaceDocs.length; i++) {
        //    genClassDocumentationContents(printWriter, interfaceDocs[i]);
        //}

        for (int i = 0; i < allClassDocs.length; i++) {
            genClassDocumentationContents(printWriter, allClassDocs[i]);
        }
        redEnd(printWriter);
        printWriter.close();
        //root.printNotice("done." + NL);
        System.out.println("done.");
    }

    /**
      * Generates Class Documentation.
      * Make a file <class-name>.red.
      */
    void genClassDocumentation(ClassDoc classDoc) {
        //* open file.
        String fileName = classDoc.name() + ".red";
        //root.printNotice("Generating " + fileName + "...");
        System.out.println("- Generating " + fileName + "...");
        PrintWriter printWriter;
        try {
            printWriter = openFile(fileName);
        } catch (IOException e) {
            root.printError(NL + "Failed to generate " + fileName);
            return;
        }

        //* generates contents.
        redStart(printWriter);
        genClassDocumentationContents(printWriter, classDoc);  // this is the body method.
        redEnd(printWriter);
        printWriter.close();
        //root.printNotice("done." + NL);
        System.out.println("done." + NL);
    }



    /**
      * Open File.
      */
    PrintWriter openFile(String fileName)
	                  throws IOException, UnsupportedEncodingException {

        //configuration
        String destFileName = configuration.destdirname + fileName;
        String docencoding = configuration.docencoding;
        //String docencoding = "SJIS";
        //String docencoding = null;

        Writer writer;
        FileOutputStream fos = new FileOutputStream(destFileName);
        if (docencoding == null) {
            writer = new OutputStreamWriter(fos);
        } else {
            writer = new OutputStreamWriter(fos, docencoding);
        }
        return new PrintWriter(writer);
    }

    /**
     * class category start document
     */
    protected void startClassCategory(PrintWriter printWriter, String packageName) {
        //System.out.println("RedDoclet-startClassCategory-");
        //* print Class_Category
        // opens 2 paren
        printWriter.println(
            "(object Class_Category \"" + packageName + "\"" + NL +
            TAB + "exportControl \"Public\"" + NL +
            TAB + "documentation \"\"" + NL +
            TAB + "logical_models (list unit_reference_list"
            );
    }

    /**
     * class category end document
     */
    protected void endClassCategory(PrintWriter printWriter, String packageName) {
        // close 2 paren
        printWriter.print(
            TAB + "))"
            );
    }

    /**
      * Generates the contents of the Class documentation.
      */
    void genClassDocumentationContents(PrintWriter printWriter, ClassDoc classDoc) {

        boolean isInterface = classDoc.isInterface();
        String packageName = classDoc.containingPackage().name();
        String className = classDoc.name();

        System.out.println("--- Checking " + className + "...");
        //System.out.println("---- isInterface: " + isInterface);

        //* print Class_Category
        // opens 2 paren
        startClassCategory(printWriter, packageName);

        //* print Class
        // opens 1 paren
        printWriter.println(
            "(object Class \"" + className + "\""
            );

        // generates all comments here
        //System.out.println("----- checking comments");
        printClassDescription(printWriter, classDoc);

        //* access modifier
        //	int mod = classDoc.modifiers();
        if (classDoc.isAbstract()|| isInterface) {
            printWriter.println(
                TAB + "abstract" + TAB + "TRUE"
                );
        }

        // not support interface
        //System.out.println("----- checking interfaces implemented");
        Type[] interfaceTypes = classDoc.interfaces();
        int interfaceNum = interfaceTypes.length;
        ClassDoc[] interfaces = null;

        //System.out.println("----- checking superclass");
        ClassDoc superClass = null;
        if (classDoc.superclass() != null) {
            superClass = classDoc.superclass().asClassDoc();
        }

        if (superClass != null || (interfaces != null && 0 < interfaceNum)) {
            printWriter.println(
                TAB + "superclasses (list inheritance_relationship_list"
                );

            //* first, superclasses
            if (superClass != null) {
                String superClassPackageName = superClass.containingPackage().toString();
                String superClassName;
                if (superClassPackageName.equals(packageName)) {
                    // only classname. e.g Color
                    superClassName = superClass.name();
                } else {
                    // full classname . e.g. java.awt.Color
                    //superClassName = superClass.qualifiedName();
                    // only classname. e.g Color
                    superClassName = superClass.name();
                }
                printWriter.println(
                    TAB + "(object Inheritance_Relationship" + NL +
                    TAB + "supplier \"" + superClassName + "\"" + ")"
                    );
            }

            if (interfaceNum != 0) {
                interfaces = new ClassDoc[interfaceNum];
                for (int i = 0; i < interfaceNum; i++) {
                    interfaces[i] = interfaceTypes[i].asClassDoc();
                    String superClassName = interfaces[i].typeName();
                    printWriter.println(
                                        TAB + "(object Inheritance_Relationship" + NL +
                                        TAB + "supplier \"" + superClassName + "\"" + ")"
                                        );
                }
            }

            //* close paren for superclasses.
            printWriter.println(
                TAB + ")"
                );
        }

        //Write FIELD's Information
        //System.out.println("----- checking fields");
        FieldDoc[] fields= classDoc.fields();
        printWriter.println(TAB + "class_attributes " + TAB
                            + "(list class_attribute_list");
        for (int i = 0; i < fields.length; i++) {
            genFieldDocumentation(printWriter, fields[i]);
        }
        printWriter.println(TAB + ")");

        //Write CONSTRUCTOR's Information
        //System.out.println("----- checking constructors");
        ConstructorDoc[] consts = classDoc.constructors();
        printWriter.println(TAB + "operations" + TAB + "(list Operations");
        for (int i = 0; i < consts.length; i++) {
            genConstructorDocumentation(printWriter, consts[i]);
        }
        printWriter.println(TAB + ")");

        //Write METHOD's Information
        //System.out.println("----- checking methods");
        MethodDoc[] methods = classDoc.methods();
        printWriter.println(TAB + "operations" + TAB + "(list Operations");
        for (int i = 0; i < methods.length; i++) {
            genMethodDocumentation(printWriter, methods[i]);
        }
        printWriter.println(TAB + ")");

        //* End class.
        endClassCategory(printWriter, packageName);

        // close 1 parens for class
        printWriter.println(
            ")"
            );

        //System.out.println("done.");
    }

    /**
      * Generate Field documentatin
      * @param printWriter
      * @param fieldDoc
      */
    void genFieldDocumentation(PrintWriter printWriter, FieldDoc fieldDoc) {
        printWriter.print(TAB2 + "(object ");

        String varName = fieldDoc.name();
        printWriter.println("ClassAttribute \"" + varName + "\"");

        // FINAL or not.
        printWriter.print(TAB3 + "attributes " + TAB +"(list Attribute_Set");
        if (fieldDoc.isFinal() == true) {
            printWriter.println(NL + TAB3 +"    (object Attribute");
            printWriter.println(TAB4 + "tool      " + TAB +"\"java\"");
            printWriter.println(TAB4 + "name      " + TAB +"\"Final\"");
            printWriter.print(TAB4 + "value      " + TAB +"TRUE)");
        }
        printWriter.println(")");

        // COMMENTS
        printFieldDescription(printWriter, fieldDoc);
        printWriter.print(NL);

        // TYPE of the valiable
        //String varType = fieldDoc.type().qualifiedName();
        //String varType = fieldDoc.type().name();
        //String varType = fieldDoc.type().toString();
        String varType = fieldDoc.type().typeName(); //unquolified Name
        printWriter.println(TAB3 + "type      " + TAB +"\"" + varType + "\"");

        // PUBLIC or PROTECTED or ...
        if (fieldDoc.isPublic() == true) {
            printWriter.print(TAB3 + "exportControl" + TAB + "\"Public\"");
        } else if (fieldDoc.isProtected() == true) {
            printWriter.print(TAB3 + "exportControl" + TAB + "\"Protected\"");
        } else if (fieldDoc.isPrivate() == true) {
            printWriter.print(TAB3 + "exportControl" + TAB + "\"Private\"");
        } else if (fieldDoc.isPackagePrivate() == true) {
            //printWriter.print(TAB3 + "exportControl" + TAB + "\"PackagePrivate\"");
        }

        //* STATIC or not
        if (fieldDoc.isStatic() == true) {
            printWriter.print(NL +TAB3 + "static     " + TAB + "TRUE");
        }
        printWriter.println(")");
    }

    /**
      * Generate Constructor Documetation.
      * @param printWriter
      * @param constructerDoc
      */
    void genConstructorDocumentation(PrintWriter printWriter, ConstructorDoc constructorDoc) {
        printWriter.print(TAB2 + "    (object ");

        // NAME
        String referenceName = constructorDoc.name();
        printWriter.println("Operation \"" + referenceName + "\"");

        // COMMENTS
        //printMethodDescription(printWriter, constructorDoc);
        printExecutableMemberDescription(printWriter, (ExecutableMemberDoc)constructorDoc);

        // RETURN TYPE
        String returnTypeName = "";
        printWriter.println(TAB3 + "result      " + TAB + "\"" + returnTypeName + "\"");

        // PUBLIC or PROTECTED or ..
        if (constructorDoc.isPublic() == true) {
            printWriter.println(TAB3 + "opExportControl" + TAB + "\"Public\"");
        } else if (constructorDoc.isProtected() == true) {
            printWriter.println(TAB3 + "opExportControl" + TAB + "\"Protected\"");
        } else if (constructorDoc.isPrivate() == true) {
            printWriter.println(TAB3 + "opExportControl" + TAB + "\"Private\"");
        }

        // concurrency
        printWriter.println(TAB3 + "concurrency " + TAB + "\"Sequential\"");

        // PARAMETERS
        printWriter.print(TAB3 + "parameters" + TAB + "(list Parameters");
        genParametersDocumentation(printWriter, (ExecutableMemberDoc)constructorDoc);
        printWriter.println(")");

        //printWriter.println(TAB3 + "logical_presentatioins   (list unit_reference_list)");
        //printWriter.print(NL + TAB3 + "uid    " + TAB + "0");
        printWriter.println(")");
        printWriter.println();
    }

    /**
      * Generate method documetation.
      * @param printWriter
      * @param methodDef
      */
    void genMethodDocumentation(PrintWriter printWriter, MethodDoc methodDoc) {
        printWriter.print(TAB2 + "    (object ");

        // NAME
        String referenceName = methodDoc.name();
        printWriter.println("Operation \"" + referenceName + "\"");

        // COMMENTS
        printExecutableMemberDescription(printWriter, (ExecutableMemberDoc) methodDoc);

        // RETURN TYPE
        Type returnType = methodDoc.returnType();
        String returnTypeName = "";
        if (returnType != null) {
            returnTypeName = returnType.typeName();
        } else {
            returnTypeName = "";
        }
        printWriter.println(TAB3 + "result      " + TAB + "\"" + returnTypeName + "\"");

        // PUBLIC or PROTECTED or ..
        if (methodDoc.isPublic() == true) {
            printWriter.println(TAB3 + "opExportControl" + TAB + "\"Public\"");
        } else if (methodDoc.isProtected() == true) {
            printWriter.println(TAB3 + "opExportControl" + TAB + "\"Protected\"");
        } else if (methodDoc.isPrivate() == true) {
            //printWriter.println(TAB3 + "opExportControl" + TAB + "\"Private\"");
        }

        // concurrency
        printWriter.println(TAB3 + "concurrency " + TAB + "\"Sequential\"");

        // PARAMETERS
        printWriter.print(TAB3 + "parameters" + TAB + "(list Parameters");
        genParametersDocumentation(printWriter, (ExecutableMemberDoc)methodDoc);
        printWriter.println(")");
        
        // ABSTRACT
        printWriter.print(TAB3 + "attributes " + TAB +"(list Attribute_Set");
        if (methodDoc.isAbstract() == true) {
            printWriter.println("");
            printWriter.println(TAB4 +"    (object Attribute");
            printWriter.println(TAB4 + TAB + "tool     " + "\"Java\"");
            printWriter.println(TAB4 + TAB + "name     " + "\"Abstract\"");
            printWriter.print(  TAB4 + TAB + "value    " + "TRUE)");
        }
        printWriter.print(")");  //attributes

        //printWriter.println(TAB3 + "logical_presentatioins   (list unit_reference_list)");
        //printWriter.print(NL + TAB3 + "uid    " + TAB + "0");
        printWriter.println(")");
        printWriter.println();
    }


    /**
      * Generate Parameter Documetation.
      * @param printWriter
      * @param excutableDoc
      */
    void genParametersDocumentation(PrintWriter printWriter, ExecutableMemberDoc executableMemberDoc) {

        Parameter[] parameters = executableMemberDoc.parameters();

        String paramType = "";
        String paramName = "";
        for (int i = 0; i < parameters.length; i++)  {
            // NAME
            paramName = parameters[i].name();

            // TYPE
            paramType = parameters[i].type().typeName();

            printWriter.println(NL + TAB4 + "(object Parameter \""
                                + paramName + "\"");
            printWriter.print(TAB4 + TAB + "type " + TAB + "\"" + paramType + "\")");
        }
    }

    /**
      * Start of the red file.
      */
    protected void redStart(PrintWriter printWriter) {
        // header.
        printWriter.println(
            "(object Petal" + NL + TAB + "version 37)" + NL
            );

        // here, opens 3 parens.
        printWriter.println(
            "(object Design \"<Top Level>\"" + NL +
            TAB + "root_category (object Class_Category \"<Top Level>\"" + NL +
            TAB2 + "exportControl \"Public\"" + NL +
            TAB2 + "global TRUE" + NL +
            TAB2 + "subsystem \"<Top Level>\"" + NL +
            TAB2 + "logical_models (list unit_reference_list"
            );
    }

    /**
      * End of the red file.
      */
    protected static void redEnd(PrintWriter printWriter)  {
        // here closes 3 parens.
        printWriter.println(")))");   // object, root category, logical_model... corresponds with redStart.
        //printWriter.println("))");   //object, logical_model... corresponds with redStart.
        printWriter.close();
    }

    /**
      * Handles multiline documentation in RED way.
      * @param trim if true, removes one beginning white space.(hack for starting comment)
      */
    private static void handleMultiLine(PrintWriter printWriter, String lines, boolean trim) {
        BufferedReader reader = new BufferedReader(new StringReader(lines));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    if (trim && Character.isWhitespace(line.charAt(0))) {
                        line = line.substring(1);
                    }
                    printWriter.print("|");
                    printWriter.println(removeHTMLTags(line));
                }
            }
        } catch (EOFException e) {
        } catch (IOException e) {
            System.out.println("IOException occurred when reading.");
        }
    }

    /**
      * Removes <html-tags>
      */
    private static String removeHTMLTags(String string) {
        int start = 0;
        int end = 0;
        int next_start = 0;
        int i = 0;
        StringBuffer out = new StringBuffer();

        for (i = 0; ; i++) {
            start = string.indexOf('<', next_start);
            if (start < 0)
                break;
            end = string.indexOf('>', start);
            if (end < 0)
                break;
            // start end exist.
            out.append(string.substring(next_start,  start));
            next_start = end + 1;
        }
        out.append(string.substring(next_start));

        return out.toString();
    }

    /**
     * Print Executable Member Description.
     * @param prinWriter
     * @param method
     */
    protected void printExecutableMemberDescription(
        PrintWriter printWriter, ExecutableMemberDoc method) {
        Tag[] deprs = method.tags("deprecated");
        String comment = method.commentText();
        ParamTag[] params = method.paramTags();
        Tag[] returns = method.tags("return");
        ThrowsTag[] thrown = method.throwsTags();
        SeeTag[] sees = method.seeTags();
        // later: Type overriding = method.overridingClass();

        if (deprs.length > 0
            || !comment.equals("")
            || params.length > 0
            || returns.length > 0
            || thrown.length > 0
            || sees.length > 0) {
            printWriter.println(TAB + "documentation");
        }
        handleDeprecatedTag(printWriter, deprs);
        handleComment(printWriter, comment);
        handleParamsTags(printWriter, params);
        handleReturnTag(printWriter, returns);
        handleThrowsTags(printWriter, thrown);
        handleSeeTags(printWriter, sees);
        // handleOverriding(overriding, method);
    }

    /**
     * Print Field Description.
     * @param prinWriter
     * @param field
     */
    protected void printFieldDescription(
        PrintWriter printWriter, FieldDoc field) {
        Tag[] deprs = field.tags("deprecated");
        String comment = field.commentText();
        SeeTag[] sees = field.seeTags();

        if (deprs.length > 0
            || !comment.equals("")
            || sees.length > 0) {
            printWriter.println(TAB + "documentation");
        }

        handleDeprecatedTag(printWriter, deprs);
        handleComment(printWriter, comment);
        handleSeeTags(printWriter, sees);
    }

    /**
     * Print Class Description.
     * @param prinWriter
     * @param classdoc
     */
    protected void printClassDescription(
        PrintWriter printWriter, ClassDoc classdoc) {
        Tag[] deprs = classdoc.tags("deprecated");
        Tag[] sinces = classdoc.tags("since");
        Tag[] authors = classdoc.tags("author");
        Tag[] versions = classdoc.tags("version");

        String comment = classdoc.commentText();
        SeeTag[] sees = classdoc.seeTags();
        if (deprs.length > 0
            || sinces.length > 0
            || authors.length > 0
            || versions.length > 0
            || !comment.equals("")
            || sees.length > 0) {
            printWriter.println(TAB + "documentation");
        }
        handleComment(printWriter, comment);
        handleDeprecatedTag(printWriter, deprs);
        handleSinceTag(printWriter, sinces);
        handleSeeTags(printWriter, sees);
        handleAuthorTags(printWriter, authors);
        handleVersionTag(printWriter, versions);
    }

    /**
      * Comment
      */
    protected void handleComment(PrintWriter printWriter, String comment) {
        // System.out.println("handleComment: " + comment);
        // javadoc removes all white spaces upto the beginning of the comment.
        // so added one " " to the beginning of the comment.
        handleMultiLine(printWriter, comment, true);
    }

    /**
      * Author
      */
    protected void handleAuthorTags(PrintWriter printWriter, Tag[] authors) {
        for (int i = 0; i < authors.length; i++) {
            //handleMultiLine(printWriter, NL + "Author: " + authors[i].text(), false);
            handleMultiLine(printWriter, NL + "Author: " + authors[i].text(), false);
        }
    }

    /**
      * Since
      */
    protected void handleSinceTag(PrintWriter printWriter, Tag[] sinces) {
        if (sinces.length > 0)
            handleMultiLine(printWriter, NL + "Since: " + sinces[0], false);
    }

    /**
      * version
      */
    protected void handleVersionTag(PrintWriter printWriter, Tag[] version) {
        if (version.length > 0) {
            //handleMultiLine(printWriter, NL + "Version: " + version[0], false);
            handleMultiLine(printWriter, "Version: " + version[0].text(), false);
        }
    }

    /**
      * deprecation
      */
    protected void handleDeprecatedTag(PrintWriter printWriter, Tag[] depre) {
        if (depre.length > 0)
            handleMultiLine(printWriter, NL + "Deprecated: " + depre[0].text(), false);
    }

    /**
      * return
      */
    protected void handleReturnTag(PrintWriter printWriter, Tag[] ret) {
        if (ret.length > 0)
            handleMultiLine(printWriter, NL + "Return: ", false);

        if (ret.length > 0)
            handleMultiLine(printWriter, "       " + ret[0].text(), false);
    }

    /**
      * See also
      */
    protected void handleSeeTags(PrintWriter printWriter, SeeTag[] sees) {
        if (sees.length > 0)
            handleMultiLine(printWriter, "See Also: ", false);
        
        for (int i = 0; i < sees.length; ++i) {
            SeeTag see = sees[i];
            //handleMultiLine(printWriter, "       " + " - " +
            //                    see.label(), false);

            ClassDoc refClass = see.referencedClass();
            MemberDoc refMem = see.referencedMember();
            String refMemName = see.referencedMemberName();

            if (refClass == null) {
                handleMultiLine(printWriter, "       " + " - " +
                                see.text(), false);
            } else if (refMemName == null || refMem == null) {
                // class reference
                handleMultiLine(printWriter, "       " +
                                refClass.name() + " - " +
                                see.text(), false);
            } else {
                // member reference
                handleMultiLine(printWriter, "       " +
                                refClass.name() + "#" + refMemName + " - " +
                                see.text(), false);
            }
        }
    }

    /**
      * param
      */
    protected void handleParamsTags(PrintWriter printWriter, ParamTag[] params) {
        if (params.length > 0)
            handleMultiLine(printWriter, NL + "Arguments:", false);

        for (int i = 0; i < params.length; i++) {
            handleMultiLine(printWriter, "       " +
                            params[i].parameterName() + " - " +
                            params[i].parameterComment(), false);
        }
    }

    /**
      * exception
      */
    protected void handleThrowsTags(PrintWriter printWriter, ThrowsTag[] thrown) {
        if (thrown.length > 0)
            handleMultiLine(printWriter, NL + "Exceptions:", false);

        if (thrown.length > 0) {
            for (int i = 0; i < thrown.length; i++) {
                handleMultiLine(printWriter, "       " +
                                thrown[i].exceptionName() + " - " +
                                thrown[i].exceptionComment(), false);
            }

        }
    }
}

