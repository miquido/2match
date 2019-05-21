![build_info](https://travis-ci.org/miquido/2match.svg?branch=master)

# README.md
## 2match-mobile

---

## **Vision**

For medium and small sized apps there is usually no budget to invest in a professional tool for managing translations. Sending files with strings to translators and putting them back in the app or using a spreadsheet to manually copy&paste translations just before a release is highly inappropriate. That is why we coded a small tool for managing translations using a shared online spreadsheet, which allows multiple people to input translations which then, with a little bit of configuration, are downloaded and placed in the correct resource directories of your app.

---

## **Features**

* generating a spreadsheet file for apps with existing translations for both single and plural strings and multiple languages for Android, iOS and Web (see "User Guide") - it simplifies 2match usage - just generate a spreadsheet and copy&paste its content to share in an online spreadsheet 
* generating translations files from a shared online spreadsheet for both single and plural strings and multiple languages for Android, iOS and Web (see "User Guide") - just run the script and after few seconds all strings are in the correct resource directories
* displaying warnings when translations for some languages are missing
handling placeholders
* single string format for both Android and iOS - 2match replaces them with platform specific ones. Use "%s" for string format and "%d" for number format
* replace map - for now a non-breaking space character is introduced - use "[_]" after words that cannot be at the end of a line - more replacement symbols can be easily added in the code (see Android and Ios classes)
* more profits: testers can fix typos directly in a spreadsheet, non-developers can change text messages which appear in the app

---

## Download
2match release .jar and samples (configuration, input spreadsheet format) are located in the `2march-release` directory.

---
## User guide

## **Input format**
#### Converting from a spreadsheet to string files
A spreadsheet in a format presented in the `strings_spreadsheet_SAMPLE.ods` is required. All fields that are **bolded** must be present in the input spreadsheet under the same name and order. 2match generates 2 strings files into each values directory: `strings.xml` or `Localizable.strings` and `strings_plural.xml` or `Localizable.stringsdict`. The easiest way to start is to either copy the provided sample spreadsheet or generate it using 2match in the *toSpreadsheet* mode.

#### Converting from strings files to spreadsheet
To convert existing strings to a spreadsheet there is only one requirement: plural strings must be kept in a separate file than single strings. Single strings file has to be named `strings.xml` or `Localizable.strings` and plural strings must be kept in `strings_plural.xml` or `Localizable.stringsdict` file.


## **Getting started**

To get started, make sure you have:

* downloaded 2match release .jar 
* downloaded a sample configuration file for your platform

For your convenience, it's best to put 2match jar witch the configuration file in your project repository. That way all developers have the same configuration. To launch it, open terminal and type: 
```bash
java -jar 2match.jar -c configuration.file
```

## Mode
2match supports 2 modes: *fromSpreadsheet* and *toSpreadsheet*. Default mode is *fromSpreadsheet*. To change the mode, please use:
```bash
java -jar 2match.jar --fromSpreadsheet -c configuration.file (can be omitted - it's default mode)
```
or
```bash
java -jar 2match.jar --toSpreadsheet -c configuration.file
```

## Verbose
By default all errors, warning and logs are turned off. To enable see them, please set *verbose* flag.
```bash
java -jar 2match.jar -v -c configuration.file

```


## **Generate strings from an online spreadsheet**
Suggested workflow:

* add 2match and config file to repository
* input a minimal configuration for generating strings based on a spreadsheet, e.g.:
```
PLATFORM=android
RES_DIR_PATH=app/src/main/res
INPUT_SPREADSHEET_XLSX_DOWNLOAD_URL=spreadsheets/d/1bKvJUHpAuWC7L4BfjsGXCo433qFVCgu4bBeP7O2ijME/export?format=xlsx
BASE_LANGUAGE_CODE=en
```
* when you want to add a new string, open your GDoc, input a string and translations, and launch the script that you have created during "Getting started" to update strings files in your app

## **Generate spreadsheet from strings files**
Suggested workflow:

* add 2match and config file to repository
* input minimal configuration for generating spreadsheet from existing strings files, e.g.:
```
PLATFORM=android
RES_DIR_PATH=path/to/res/dir
OUTPUT_SPREADSHEET_FILE_PATH=~/Desktop/strings_spreadsheet.xlsx
```
Xlsx file contains all your strings along with a header row - just ready to be copy-pasted into an online shared spreadsheet

---

## **How to build**
[Gradle build tool](https://gradle.org/) is used to build the project. Easiest way of building 2match is to use Gradle Wrapper from the repository.

**To build release (fat jar + config sample + sample spreadsheet file) please use provided Gradle task:**
```
./gradlew buildFatJarWithSampleConfig
(for shorter version just gradlew bFJWSC)
```
You will find artifacts in the *2match-release* directory.


**To execute unit tests:**
```
./gradlew test
```
For building from IDE the best experience comes with IntelliJ IDE. Just import the project and select Gradle model with Gradle Wrapper enabled.

## **Information about strings files for non-developers**
On both Android and iOS the localisation feature works in the same way. There is a directory with base strings and language specific directories with the same strings, but translated. In runtime, the operating system takes strings from language specific directory according to the phone's language or from the base directory when specific language translations are not present. Both platforms distinguish between single strings and plurals, and have their own format of storing string files, but the way of working is the same. For plural strings both platform use the same qualifiers to determine which string should be displayed.

|value|description|  
|---|---|
|zero|When the language requires a special treatment of the number zero (as in Arabic).|  
|one|When the language requires a special treatment of numbers like one (as with the number 1 in English and most other languages; in Russian, any number ending in 1 but not ending in 11 is in this class).|  
|two|When the language requires a special treatment of numbers like two (as with 2 in Welsh, or 102 in Slovenian).|  
|few|When the language requires a special treatment of "small" numbers (as with 2, 3, and 4 in Czech; or numbers ending 2, 3, or 4 but not 12, 13, or 14 in Polish).|  
|many|When the language requires special treatment of "large" numbers (as with numbers ending 11-99 in Maltese).|  
|other|When the language does not require any special treatment of the given quantity (as with all numbers in Chinese, or 42 in English).|  

More examples can be found [here](https://www.unicode.org/cldr/charts/34/supplemental/language_plural_rules.html).

### Android
String resources are kept in simple [XML](https://www.w3schools.com/xml/xml_whatis.asp) files. XML files are kept in a language specific directory, e.g. values-pl (contains Polish resources), values-en (contains English resources).

#### Single strings
Single strings file contains simple list of strings in XML format, ex.
```xml
<string name="cancel" formatted="true" translatable="true">Cancel</string>
<string name="skip" formatted="true" translatable="false">Skip</string>
```
*name* - name of the string; not visible for the app user; used by a developer to reference string (serves as an ID of the string);

*formatted* - states whether the string has some placeholder to be replaced; e.g. "Log in" string is formatted, "%d rooms left in %s hotel" is not formatted, because before displaying it to the user, the %d and %s need to be replaced in the code with real values → "10 rooms left in Bell Air hotel"

*translatable* - states whether the string is translatable to other languages; some strings should not be translated, e.g. contact address

*value* - the actual string displayed to the user; always between `<string ...>` and `</string>`

#### Plural strings
Plural strings file contains a list of string variants according to plural qualifier. 
```xml
<plurals name="show_offers_count_format">
<item quantity="zero">No offers</item>
<item quantity="one">Show %d offer</item>
<item quantity="two">Show %d offers</item>
<item quantity="few">Show %d offers</item>
<item quantity="many">Show %d offers</item>
<item quantity="other">Show %d offers</item>
</plurals>
```
*name* - name of the string; not visible for the app user; used by a developer to reference the string (serves as an ID of the string);

*plural qualifiers* - zero, one, two, few, many, other - define which string variant should be used; not all quantity strings are used in all languages 

*value for qualifier* - the actual string displayed to the user; always between `<item ...>` and `</item>`; Android OS takes specific variant based on the passed number;

#### Resources
[read about XML from w3c](https://www.w3schools.com/xml/xml_whatis.asp)  
[read about Android string resources from Android developer portal](https://developer.android.com/guide/topics/resources/string-resource)  
[read about Android plural string resources and plural qualifiers meaning from Android developer portal](https://developer.android.com/guide/topics/resources/string-resource#Plurals)

### iOS
String files are kept in a language specific directory, e.g. `pl.lproj` (contains Polish resources), `en.lproj` (contains English resources). iOS uses two different formats for single and plural string resources.

#### Single strings
Single strings resources are kept in a simple properties-like (key-value) file.

```properties
"common.cancel" = "Cancel";
"common.ok" = "OK";
"common.skip" = "Skip";
```

format: "key" = "value"; key and value are always between `" "`; notice the `;` at the end of a line; 

*left hand value* - name of the string; not visible for the app user; used by a developer to reference the string (serves as an ID of the string);

*right hand value* - the actual string displayed to the user;

#### Plural strings
Plural string resources are kept in [plist files](https://en.wikipedia.org/wiki/Property_list). They define string variants for plural qualifiers.
```xml
<dict>
    <key>show offers %d</key> ==> name of the string; not visible for the app user; used by a developer to reference the string (serves as an ID of the string);
    <dict>
        <key>NSStringLocalizedFormatKey</key>
        <string>%#@value@</string>
        <key>value</key>
        <dict>
            <key>NSStringFormatSpecTypeKey</key>
            <string>NSStringPluralRuleType</string>
            <key>NSStringFormatValueTypeKey</key>
            <string>d</string>
            <key>zero</key> ==> actual strings visible to users for string qualifiers start here
            <string>No offers</string>
            <key>one</key>
            <string>Show %d offer</string>
            <key>two</key>
            <string>Show %d offers</string>
            <key>few</key>
            <string>Show %d offers</string>
            <key>many</key>
            <string>Show %d offers</string>
            <key>other</key>
            <string>Show %d offers</string>
        </dict>
    </dict>
    ... some more strings
</dict>
```

*plural qualifiers* - zero, one, two, few, many, other - define which string variant should be used; not all quantity strings are used in all languages 

*value for qualifier* - the actual string displayed to the user; iOS takes specific variant based on the passed number;

#### Resources
[read about plist format from Apple developer portal](https://en.wikipedia.org/wiki/Property_list)  
[read about plural strings resources and plural qualifiers from Apple developer portal](https://developer.apple.com/library/archive/documentation/MacOSX/Conceptual/BPInternational/LocalizingYourApp/LocalizingYourApp.html#//apple_ref/doc/uid/10000171i-CH5-SW10)

### Web
On Web platforms there is no single method of storing translations defined. We decided to use JSON files for both single and plural translations, keeping a directory and file structure for each language (each language directory has two files - one with single and one with plural strings).

#### Single strings
```json
[
  {
    "key": "cancel",
    "value": "Cancel"
  },
  {
    "key": "ok",
    "value": "OK"
  },
  {
    "key": "skip",
    "value": "Skip"
  },
  {
    "key": "yes",
    "value": "Yes"
  },
  {
    "key": "no",
    "value": "No"
  },
  {
    "key": "touch_id",
    "value": "Touch ID"
  },
  {
    "key": "face_id",
    "value": "Face ID"
  }
]
```

#### Plural strings
```json
[
  {
    "key": "No offers",
    "pluralsMap": {
      "zero": "No offers",
      "one": "Show %d offer",
      "two": "Show %d offers",
      "few": "Show %d offers",
      "many": "Show %d offers",
      "other": "Show %d offers"
    }
  },
  {
    "key": "No rooms",
    "pluralsMap": {
      "zero": "No rooms",
      "one": "Last room!",
      "two": "Last %d rooms left",
      "few": "Last %d rooms left",
      "many": "Last %d rooms left",
      "other": "Last %d rooms left"
    }
  }
]
```