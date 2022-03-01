# Mule Applications Validation Plugin


## Plugin Configuration

Each project must have the following plugin configuration in the **pom.xml** file. This plugin configuration expect the validation to be triggered explicitly

```xml
<plugin>
  <groupId>org.neointegration</groupId>
  <artifactId>validate-mule-maven-plugin</artifactId>
  <version>1.0.0</version>
  <configuration>
    <ruleFileLoc>src/test/resources</ruleFileLoc>
    <ruleFileName>app-rule.json</ruleFileName>
    <reportPath>target/validationReport</reportPath>
    <reportFileName>report.html</reportFileName>
  </configuration>
</plugin>
```

### Configurable properties
| Name         | Required   | Description | Default Value  |
|--------------|:----------:|-------------|:---------------|
| `ruleFileLoc`  | Yes      | Path to the folder containing the validation rules.|`src/test/resources`|
| `ruleFileName` | Yes       | Rule File name | `app-rule.json` |
| `reportPath`   | No       | Location where a HTML report will be saved. Relative file location | `target/validationReport` |
| `reportFileName`| No      | Name of the report file. |`report.html`|

## How to trigger the validation
- By default the plugin does not gets triggered as part of the build process, it needs to be explicitly triggered using following maven command:
```
mvn validate-mule:validate 
```
- If you rather like to trigger the source code validation as part of the build process, make the following changes in the plugin configuration (it links the validation to the build process using execution config ):
```xml
<plugin>
    <groupId>org.neointegration</groupId>
    <artifactId>validate-mule-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <projectDir>/Users/mhaque/AnypointStudio/belron/belron-api-template-v1</projectDir>
        <ruleFileLoc>src/test/resources</ruleFileLoc>
        <ruleFileName>app-rule.json</ruleFileName>
        <reportPath>target/validationReport</reportPath>
        <reportFileName>report.html</reportFileName>
    </configuration>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>validate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## User Guide

### Build your own
 1. Clone the [repo](https://github.com/Neo-Integrations/mule-validation-plugin)
 2. Change to the project directory - `cd mule-validation-plugin`
 3. To install the connector to the local maven repo, run `mvn clean install`.
 4. Then include the below plugin configuration in your `pom.xml`:
 ```xml
 <build>
 ...
     <plugins>
     ...
         <plugin>
              <groupId>org.neointegration</groupId>
              <artifactId>validate-mule-maven-plugin</artifactId>
              <version>1.0.0</version>
              <configuration>
                <ruleFileLoc>src/test/resources</ruleFileLoc>
                <ruleFileName>app-rule.json</ruleFileName>
                <reportPath>target/validationReport</reportPath>
                <reportFileName>report.html</reportFileName>
              </configuration>
         </plugin>
     ...
     </plugins>
 ...
 </build>
```
5. If you would like to deploy the connector to a maven repo, please include the distribution management in the pom.xml and publish to the maven artifact.

### Use the binary directly from maven repo
1. First add following maven repository in your pom.xml's repository section. Below is an example of my own maven repo, replace the details with your organisation maven repo URL.
```xml
 <repositories>
     ...
     <repository>
          <id>maven-public</id>
          <url>https://pkgs.dev.azure.com/NeoIntegration/MuleSoft/_packaging/mvn-public/maven/v1</url>
          <releases>
             <enabled>true</enabled>
          </releases>
          <snapshots>
             <enabled>true</enabled>
          </snapshots>
     </repository>
     ...
 </repositories>
```
2. Add the server authentication details in your `$M2_HOME/settings.xml`. Replace `[PERSONAL_ACCESS_TOKEN]` with the actual password. Please [contact](mailto:aminul1983@gmail.com) me if you would like to get a token.
 ```xml
<servers>
...
    <server>
      <id>maven-public</id>
      <username>NeoIntegration</username>
      <password>[PERSONAL_ACCESS_TOKEN]</password>
    </server>
...
</servers>
 ```
3. Thats it, you can start validating your Mule 4 application using following command:
```
mvn validate-mule:validate 
```

## Validation Rules

- A rule can have any of the following three severity: 
  - `Major`: Any rule with this severity will fail the build if the validation result is negative.
  - `Minor`:  Any rule with this severity will NOT fail the build even if the validation result is negative.
- The rules are categorized either as 
  - `XML` code validation: all the Mule code is written in xml
  - `RAMl` or `OASv3` specification validation.
  - `JSON` file validation: This mainly targeted for the `mule-artifact.json` validation but it can be used to validate any JSON file.
- The rule for `xml` and `raml` are structured differently due to the nature of their specification and parsing constrains.

### XML Validation Rule structure
```json
[
  {
      "ruleId" : "RULE_001",
      "description" : "Rule Description",
      "severity": "MAJOR",
      "nodeSpecification": {
        "nodeReference": "http:listener-connection",
        "xPathNodeReference": false,
        "xPathReturnType": "NODESET",
        "attributeName": "port",
        "attributeValue": "${http.private.port}",
        "attributeOperation": "EQUALS"
      },
      "aggregatationOps": {
          "nodeCardinality": "ONLY_ONE"
      },
      "location": {
        "path": "src/main/mule",
        "inlcudeFileNamePattern": "(.*).xml",
        "excludeFileNamePattern": "pom.xml"
      }
  }
]
```

| Name          | Description                                                                                                                                                                                  | Required | Example |
|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|----------|
| ruleId | Unique identifier of the rule.                                                                                                                                                               | yes | RULE_001 |
| description | Brief description of the rule.                                                                                                                                                               | yes | Credentials and resources should be managed with application properties |
| path | Relative location of files which required to validated by this rule                                                                                                                          | yes | `src/main/mule` |
| severity | Severity of the rule. Possible values: CRITICAL, MAJOR, MINOR                                                                                                                                | Yes | MINOR |
| nodeReference | When `xPathNodeReference == false`, this will point to the name of an XML tag. When `xPathNodeReference=true`, it can take an `XPATH` expression to be validated against the selected files. | yes | `property-placeholder` |
| xPathNodeReference | If the value is `true` then `nodeReference` has an `XPATH` expression                                                                                                                        | no | true |
| xPathReturnType | When the `xPathNodeReference` is `true`, this can determine the output type of the XPATH expression. Supported values are `BOOLEAN`, `NODESET`                                               | no | `BOOLEAN` |
| attributeName | Name of an attribute from the extracted nodes                                                                                                                                                | no | `name`  |
| attributeValue | Expected value from the node `attributeName`                                                                                                                                                 | no |  |
| attributeOperation | This operation signify what operation to apply to match the extracted value with the `attributeValue`. possible values are `EQUALS`, `MATCHES`, `NOT_MATCHES`, `CONTAINS`, `COUNT`           | no | `EQUALS` |
| nodeCardinality | This can apply some aggregation operation on the selected dataset. Possible values are ONLY_ONE, `AT_LEAST_ONE`, `MORE_THAN_ONE`, `NONE`, `LESS_THAN`, `MORE_THAN`                           | no | `ONELY_ONE`|
| limit                  | This is related to `nodeCardinality` = `LESS_THAN` or `MORE_THAN` or `ALL`. It checks if for an rule all the sucess result count is less than / greater than / equal to `limit`              | no | `10`                                                                    |
| inlcudeFileNamePattern | Specifies an regex expression to match all the files which will be included for this rule validation                                                                                         | yes | `(.*).xml`|
| excludeFileNamePattern | You can provide a regex expression to exclude any files from the selection                                                                                                                   | no | `pom.xml`|

### RAML Validation rule structure
```json
[
  {
    "ruleId" : "RULE_0017-01-10",
    "description" : "Standard RAML validation",
    "severity": "MAJOR",
    "ramlRule": {
      "validateTitle" : true,
      "validateDescription" : true,
      "validateDocumentation" : true,
      "validateSecuritySchemes" : true,
      "validateOAuth2Scheme" : true,
      "validateClientIdEnforcementScheme" : true,
      "validateResponseBody" : true,
      "validateRequestBody" : true,
      "validateTraceability" : true,
      "validateErrorResponse" : true
    },
    "location": {
      "path": "src/main/resources/api",
      "inlcudeFileNamePattern": "(.*).raml",
      "excludeFileNamePattern": "pom.xml"
    }
  },
  {
    "ruleId" : "RULE_0018",
    "description" : "Check if RAML has baseUri",
    "severity": "MAJOR",
    "ramlRule": {
      "jexlExpression": "obj.servers().get(0).url().value() != null",
      "jexlExpressionMatchingOperation": "==",
      "jexlExpressionExpectedValue": "true"
    },
    "location": {
      "path": "src/main/resources/api",
      "inlcudeFileNamePattern": "(.*).raml",
      "excludeFileNamePattern": "pom.xml"
    }
  }
]
```


| Name          | Description                                                                                                                                                                                                                                                                                                                | Required | Example                 |
|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|-------------------------|
| ruleId | Unique identifier of the rule.                                                                                                                                                                                                                                                                                             | yes      | RULE_001                |
| description | Brief description of the rule.                                                                                                                                                                                                                                                                                             | yes      | 'dsdsds'                |
| path | Relative location of files which required to validated by this rule                                                                                                                                                                                                                                                        | yes      | `src/main/mule`         |
| severity | Severity of the rule. Possible values: `CRITICAL`, `MAJOR`, `MINOR`                                                                                                                                                                                                                                                        | Yes      | MINOR                   |
| validateTitle | If set to true, it will validate if each of the API specification contains a title                                                                                                                                                                                                                                         | no       | true                    |
| validateDescription | Each API specification must contain a good description of the API                                                                                                                                                                                                                                                          | no       | true                    |
| validateDocumentation | Each API must contain a brief dcoumentation using the RAML documentationtag                                                                                                                                                                                                                                                | no       | true                    |
| validateSecuritySchemes | Each API must specify at least one security schem using RAML securitySchemes.                                                                                                                                                                                                                                              | no       | true                    |
| validateOAuth2Scheme | Once okta is in scope, there must be a OAuth2 security schema applied to all experience api                                                                                                                                                                                                                                | no       | true                    |
| validateClientIdEnforcementScheme | Each API must have a mandatory Client_Id enforcement trait                                                                                                                                                                                                                                                                 | no       | true                    |
| validateResponseBody | Every endpoints must have an explicit resoponse body using at least an declared type or example.                                                                                                                                                                                                                           | no       | true                    |
| validateRequestBody | Each `POST`/ `PATCH` / `PUT` endpoints must have an explicit request body using at least an declared type or example.                                                                                                                                                                                                      | no       | true                    |
| validateTraceability | Every API must have a tracebility traits which enforces X-CORRELATION-ID header                                                                                                                                                                                                                                            | no       | true                    |
| validateErrorResponse | Each endpoints must have error response defined                                                                                                                                                                                                                                                                            | no       | true                    |
| jexlExpression | If JEXL expression is provided, none of the avove validations are applicable in a partuclar rule. You will need to learn [`RAML` parser](https://github.com/raml-org/webapi-parser/tree/v0.5.0) and [JEXL expression Language](https://commons.apache.org/proper/commons-jexl/) to work effectively work on this expression | no       | "obj.name.value()"      |
| jexlExpressionMatchingOperation | What operation to use to match the extracted JEXL value with the expected value. It supports all the condtional operator supported by JEXL. Foe example, '==' is equal.                                                                                                                                                    | no       | "=="                    |
| jexlExpressionExpectedValue | Expected value                                                                                                                                                                                                                                                                                                             | no       | "expected string value" |
| inlcudeFileNamePattern | Specifies an regex expression to match the main `RAML` specification file which will be included for this rule validation                                                                                                                                                                                                  | yes      | `api.raml`              |
| excludeFileNamePattern | You can provide a regex expression to exclude any files from the selection                                                                                                                                                                                                                                                 | no       | `pom.xml`               |


### JSON Validation Rule structure
```json
[
  {
    "ruleId" : "RULE_022-3",
    "description" : "anypoint.platform.client_id must be declared as secureProperties in mule-artifact.json",
    "severity": "MAJOR",
    "nodeSpecification": {
      "nodeReference": "$.secureProperties",
      "attributeValue": "anypoint.platform.client_secret",
      "attributeOperation": "CONTAINS"
    },
    "location": {
      "path": "",
      "inlcudeFileNamePattern": "mule-artifact.json"
    }
  }
]
```

| Name                   | Description                                                                                                                                                                                                                                                                                | Required | Example                                                                 |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|-------------------------------------------------------------------------|
| ruleId                 | Unique identifier of the rule.                                                                                                                                                                                                                                                             | yes | RULE_001                                                                |
| description            | Brief description of the rule.                                                                                                                                                                                                                                                             | yes | Credentials and resources should be managed with application properties |
| path                   | Relative location of files which required to validated by this rule                                                                                                                                                                                                                        | yes | ``                                                                      |
| severity               | Severity of the rule. Possible values: CRITICAL, MAJOR, MINOR                                                                                                                                                                                                                              | Yes | MINOR                                                                   |
| nodeReference          | I have used [json-path](https://github.com/json-path/JsonPath) library mimic an xml xpath type of expression to match elements from a JSON document. Please follow the `json-path` guide to write an expression to match your need. This field only accept a valid `json-path` expression. | yes | `$.secureProperties`                                                    |
| attributeValue         | Expected value to be matched against the  `nodeReference` expression value applying the `attributeOperation`                                                                                                                                                                               | no | `anypoint.platform.client_secret`                                       |
| attributeOperation     | This operation signify what operation to apply to match the extracted value with the `attributeValue`. possible values are `EQUALS`, `MATCHES`, `CONTAINS`, `COUNT`                                                                                                                        | no | `EQUALS`                                                                |
| nodeCardinality        | This can apply some aggregation operation on the selected dataset. Possible values are ONLY_ONE, `AT_LEAST_ONE`, `MORE_THAN_ONE`, `NONE`, `LESS_THAN`, `MORE_THAN`                                                                                                                         | no | `ONELY_ONE`                                                             |
| limit                  | This is related to `nodeCardinality` = `LESS_THAN` or `MORE_THAN` or `ALL`. It checks if for an rule all the sucess result count is less than / greater than / equal to `limit`                                                                                                             | no | `10`                                                                    |
| inlcudeFileNamePattern | Specifies an regex expression to match all the files which will be included for this rule validation                                                                                                                                                                                       | yes | `(.*).xml`                                                              |
| excludeFileNamePattern | You can provide a regex expression to exclude any files from the selection                                                                                                                                                                                                                 | no | `pom.xml`                                                               |


## An Example Console Report
```
************************ Static Code Validation Results Start **********************************************
------------------------------------------------------------------------------------------------------------
Rule ID          Status   Rule Description                                                      
------------------------------------------------------------------------------------------------------------
RULE_001         PASSED   HTTP Listner Config must exists with port point to ${http.private.port}

RULE_002         PASSED   Flow name must be snakecase with '-flow' suffix                       

RULE_003         PASSED   Subflow name must be snakecase with '-subflow' suffix                 

RULE_004         PASSED   main-flow must exists in interface.xml                                

RULE_005         FAILED   Application should have use APIKit to auto-generate the implementation interface
                          Error: File global.xml has 1 config nodes                             
                          Error: File interface.xml has 0 config nodes                          
                          Error: File search-orders.xml has 0 config nodes                      
                          Error: File logger.xml has 0 config nodes                             
                          Error: File health.xml has 0 config nodes                             
                          Error: File error-handling.xml has 0 config nodes                     
                          Error: File search-product.xml has 0 config nodes                     

RULE_006         PASSED   Configuration files shouldn't have so many flows                      

RULE_007         PASSED   Configuration files shouldn't have so many subflows                   

RULE_008         PASSED   Mule Credentials Vault shouldn't use a hardcoded encryption key       

RULE_009         PASSED   Application resources should be managed with application properties   

RULE_010         PASSED   Application Secrets must be managed using secure properties           

RULE_011         FAILED   AutoDiscovery should be use to register the app in API manager        
                          Error: File global.xml has 1 autodiscovery nodes                      
                          Error: File interface.xml has 0 autodiscovery nodes                   
                          Error: File search-orders.xml has 0 autodiscovery nodes               
                          Error: File logger.xml has 0 autodiscovery nodes                      
                          Error: File health.xml has 0 autodiscovery nodes                      
                          Error: File error-handling.xml has 0 autodiscovery nodes              
                          Error: File search-product.xml has 0 autodiscovery nodes              

RULE_012         PASSED   globalErrorHandler Handler must be defined in error-handling.xml      

RULE_013         PASSED   Global error handler must be enforced in the main-flow flow           

RULE_014         PASSED   Every API must use parent pom with groupId 75dbd05f-1b06-4cf7-b3be-b8bfbc18afc9

RULE_015-a       FAILED   Data transformations should be stored in external .dwl files - Payload
                          Warning: The rule failed in the file [search-orders.xml] as it could not find any attribute with the name [resource]
                          Warning: The rule failed in the file [search-product.xml] as it could not find any attribute with the name [resource]

RULE_015-b       FAILED   Data transformations should be stored in external .dwl files - Variable
                          Warning: The rule failed in the file [interface.xml] as it could not find any attribute with the name [resource]

RULE_016         PASSED   AutoDiscovery API ID shouldn't use a hardcoded value                  

RULE_0017-01-10  FAILED   Standard RAML validation                                              
                          Error: API Specification description has not been defined             
                          Error: API Specification does not contain a documentation content tag 
                          Error: API Specification does not contain a security schema           
                          Error: OAuth2 Schema has not been specified                           
                          Error: ClientId Enforcement traits does not exists                    
                          Error: Traceability traits does not exists                            

RULE_0018        PASSED   Check if RAML has baseUri                                             

RULE_0019        PASSED   There must be a file name logger.xml                                  

RULE_0020        PASSED   There must be a file name error-handling.xml                          

RULE_0021        PASSED   There must be a file name health.xml                                  

RULE_022         PASSED   mule.key must be declared as secureProperties in mule-artifact.json   

RULE_023         PASSED   anypoint.platform.client_id must be declared as secureProperties in mule-artifact.json

RULE_024         PASSED   anypoint.platform.client_id must be declared as secureProperties in mule-artifact.json

------------------------------------------------------------------------------------------------------------

---------------------
|  Passed |     20 | 
|  Failed |      5 | 
---------------------

************************ Static Code Validation Results End ************************************************
```

## Reference
- [json-path for JSON file](https://github.com/json-path/JsonPath)
- [Java Expression Language for RAML file](https://commons.apache.org/proper/commons-jexl/)
- [XPATH for XML file](https://www.w3schools.com/xml/xpath_intro.asp) 

## Contribute
Any feedback and suggestion is welcome. Please reach out to me at [aminul1983@gmail.com](mailto:aminul1983@gmail.com)
