jsonserver
==========

A simple webserver that accepts JSON and [Smile](http://wiki.fasterxml.com/SmileFormatSpec)
payloads and prints it pretty-formatted to STDOUT.

## Requirements

Requires Java 8.

## Usage

Just build and execute the JAR file. First parameter is the listen port.

```
$ mvn package
$ java -jar target/jsonserver-*.jar 3000
```
