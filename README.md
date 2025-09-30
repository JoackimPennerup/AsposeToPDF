# AsposeToPDF

Ett enkelt kommandoradsverktyg för att konvertera olika dokumentformat till PDF med hjälp av Aspose Total for Java.

## Bygga

```bash
mvn -DskipTests package
```

Detta skapar både en körbar JAR (`target/asposetopdf-1.0.0-jar-with-dependencies.jar`) och förbereder underlaget för jpackage.

## Köra från JAR

```bash
java -jar target/asposetopdf-1.0.0-jar-with-dependencies.jar <indatafil> [utdatafil]
```

Om `utdatafil` inte anges skapas en PDF med samma namn som indatafilen men med ändelsen `.pdf`.

## Paketera med jpackage

För att skapa en plattformsanpassad app-image (som kan köras utan installerad JRE) används jpackage via Maven:

```bash
mvn -DskipTests package jpackage:jpackage
```

Detta producerar en app-image under `target/installer/AsposeToPdf`. Kopiera katalogen till målmaskinen och kör startskriptet inuti.

> **Obs!** jpackage kräver ett JDK (17 eller senare) med jpackage-kommandot installerat på byggmaskinen.

## Stödda format

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
* DWG (Model-layouten exporteras som standard och eventuella papperslayouter identifieras automatiskt)

Filtypen bestäms i första hand via filens magiska bytes. Om typen inte kan avgöras på detta sätt används filändelsen som reserv.

## Aspose-licens

Om du har en Aspose Total-licens kan den aktiveras genom att sätta miljövariabeln `ASPOSE_LICENSE_PATH` till sökvägen av licensfilen innan programmet startas.
