NSR-JSON
---
A library provides easy and customizable ways to read data from JSON files or Objects has JSON format.

## Quick Start Guide
* Reading data from a JSON file
``` java
JSONReader json = new JSON("filePath")
              .read();
        
Object data = json.get("key");
```
* Reading data from a JSON object
``` java
JSONReader json = new JSON(jsonObject)
              .read();
        
Object data = json.get("key");
```
---
## Features
* Serial keys
``` java
Object data = json.get("key1.key2[0]");
```

* Fetch data in many types
``` java
// String
String s = json.getString("key");

//Integer
Integer in = json.getInteger("key");

// Double
Double dbl = json.getDouble("key");

// and more...
```

* Custom parsing
``` java
// For easy costing types
String str = json.getAs("key", String.class);

// For more complex parsing
var obj = json.getAs("key", obj -> {
    // do some parsing to the obj and return the value
    return String.valueOf(obj);
});
```

* Custom Objects
  * The Custom class must have a constructor without any arguments
  * The keys in the JSON file must exactly match the class fields names
``` java
// Fetch all or some of the Person class fields
var obj = json.getCustomObject("key", Person.class);
```

* Variables
  * We can define some variables in the JSON file then refer to them
  * NOTE: the key of the variables should be as shown below
``` json
{
  "variables": {
    "v1": "v1",
    "v2": "v2",
    "v3": "v3"
  },
  "string": "first var is ${v1}, second is ${v2}, then third is ${v3}"
}   
```

``` java
String str = json.getString("string");
// expected output: first var is v1, second is v2, then third is v3
```
---
## Configuration
Optional we can add a JSON file with *nsr_config* or *config* name under `src/main/resources/`
```json
{
  "variables": {
    "test-number": "001"
  },
  "environments": [
    "local",
    "live"
  ],
  "date-config": {
    "data-format": "yyyy-MM-dd HH:mm:ss",
    "timezone": "UTC"
  }
}
```
* Support global variables *Note: local variables has higher property*
* Support environments list by adding `key@environment`
* Support date configuration for `getDate` method