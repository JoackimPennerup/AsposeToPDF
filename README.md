# AsposeToPDF

A simple command-line tool for converting various document formats to PDF using Aspose Total for Java.

## Build

```bash
mvn -DskipTests package
```

This creates both an executable JAR (`target/asposetopdf-1.0.0-jar-with-dependencies.jar`) and prepares the artifacts for jpackage.

> **Tip:** Aspose publishes its dependencies in the public Maven repository described at <https://releases.aspose.com/total/java/>. `pom.xml` is already configured to use the repository `https://releases.aspose.com/java/repo/`, so you do not need any license information to download the libraries.

## Run from JAR

```bash
java -jar target/asposetopdf-1.0.0-jar-with-dependencies.jar <inputFile> [outputFile]
```

If `outputFile` is not provided, a PDF with the same name as the input file but with the `.pdf` extension is created.

## Package with jpackage

To create a platform-specific app image (which can run without an installed JRE), use jpackage via Maven:

```bash
mvn -DskipTests package jpackage:jpackage
```

This produces an app image under `target/installer/AsposeToPdf`. Copy the directory to the target machine and run the startup script inside it.

> **Note:** jpackage requires a JDK (17 or later) with the jpackage command available on the build machine.

## Supported formats

* PDF
* JPG/JPEG
* PNG
* TIFF/TIF
* DOCX
* XLSX
* PPTX
* EML
* MSG
* Visio (VSDX)
* DWG (the model layout is exported by default and any paper layouts are detected automatically)

The file type is determined primarily via the file's magic bytes. If the type cannot be determined in this way, the file extension is used as a fallback.

## Aspose license

If you have an Aspose Total license, it can be activated by setting the environment variable `ASPOSE_LICENSE_PATH` to the path of the license file before starting the program.
