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
A spreadsheet in a format presented in the `strings_spreadsheet_SAMPLE.ods` is required. All fields that are **bolded** must be present in the input spreadsheet under the same name and order. Using this spreadsheet, 2match generates: 
* Android: Two files in the values directory — `strings.xml` for single strings and `strings_plural.xml` for plural strings.
* Web: Two files in the lang-CODE directory — `strings.json` for single strings and `strings_plural.json` for plural strings.
* iOS: A single file — `Localizable.xcstrings`.

The easiest way to start is to either copy the provided sample spreadsheet or generate it using 2match in the *toSpreadsheet* mode.

#### Converting from strings files to spreadsheet
To convert existing string files into a spreadsheet:
* For Android and Web, plural strings must be in separate files from single strings:
  * Single strings: `strings.xml` (Android) or `strings.json` (Web). 
  * Plural strings: `strings_plural.xml` (Android) or `strings_plural.json` (Web).
* For iOS, all strings — single and plural — must be in a single file named `Localizable.xcstrings`

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
String files are kept in Xcode string catalog (`.xcstrings`) files.

#### Single strings
Single strings resources are kept as json structure.

```json
{
  "sourceLanguage": "en",
  "strings": {
    "common.cancel": {
      "localizations": {
        "en": {
          "stringUnit": {
            "state": "translated",
            "value": "Cancel"
          }
        },
        "pl": {
          "stringUnit": {
            "state": "translated",
            "value": "Anuluj"
          }
        }
      }
    },
    "common.ok": {
      "localizations": {
        "en": {
          "stringUnit": {
            "state": "translated",
            "value": "OK"
          }
        },
        "pl": {
          "stringUnit": {
            "state": "translated",
            "value": "OK"
          }
        }
      }
    },
    "common.skip": {
      "localizations": {
        "en": {
          "stringUnit": {
            "state": "translated",
            "value": "Skip"
          }
        },
        "pl": {
          "stringUnit": {
            "state": "translated",
            "value": "Pomiń"
          }
        }
      }
    }
  },
  "version": "1.0"
}
```

#### Plural strings
Plural string are kept as json structure, together with single strings.
```json
{
  "sourceLanguage": "en",
  "strings": {
    // Some translations ... 
    "show offers %d": {
      "localizations": {
        "en": {
          "stringUnit": {
            "state": "translated",
            "value": "%#@value@"
          },
          "substitutions": {
            "value": {
              "argNum": 1,
              "formatSpecifier": "lld",
              "variations": {
                "plural": {
                  "zero": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "No offers"
                    }
                  },
                  "one": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Show %d offer"
                    }
                  },
                  "two": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Show %d offers"
                    }
                  },
                  "few": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Show %d offers"
                    }
                  },
                  "many": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Show %d offers"
                    }
                  },
                  "other": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Show %d offers"
                    }
                  }
                }
              }
            }
          }
        },
        "pl": {
          "stringUnit": {
            "state": "translated",
            "value": "%#@value@"
          },
          "substitutions": {
            "value": {
              "argNum": 1,
              "formatSpecifier": "lld",
              "variations": {
                "plural": {
                  "zero": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Brak ofert"
                    }
                  },
                  "one": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Pokaż %d ofertę"
                    }
                  },
                  "two": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Pokaż %d offerty"
                    }
                  },
                  "few": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Pokaż %d offerty"
                    }
                  },
                  "many": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Pokaż %d offert"
                    }
                  },
                  "other": {
                    "stringUnit": {
                      "state": "translated",
                      "value": "Pokaż %d offert"
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    // Some translations ... 
  },
  "version": "1.0"
}
```

*plural qualifiers* - zero, one, two, few, many, other - define which string variant should be used; not all quantity strings are used in all languages 

*value for qualifier* - the actual string displayed to the user; iOS takes specific variant based on the passed number;

The Xcode string catalog editor does not natively support pluralization without a numerical placeholder. To address this, plurals are wrapped in a substitution structure, allowing placeholders to be omitted from the displayed strings. This approach enables the creation of plurals like one: `hour` and other: `hours` without requiring a visible number in the output.

#### Resources
[read about xcode string catalog on Apple developer portal](https://developer.apple.com/documentation/xcode/localizing-and-varying-text-with-a-string-catalog)  
[read about plural strings without numerical placeholders](https://forums.developer.apple.com/forums/thread/737329)

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
