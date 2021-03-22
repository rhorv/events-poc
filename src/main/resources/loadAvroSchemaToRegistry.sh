#!/bin/bash
# Should add error handling

while getopts s:f: flag
do
    case "${flag}" in
        s) subject=${OPTARG};;
        f) schemafile=${OPTARG};;
    esac
done

# Loads the schema in the file specified to the subject specified.
jq '. | {schema: tojson}' $schemafile  | \
    curl -X POST http://localhost:8081/subjects/${subject}-value/versions \
         -H "Content-Type: application/vnd.schemaregistry.v1+json" \
         -d @-

# Get the subjects currently registered in schema registry
# curl --silent -X GET http://localhost:8081/subjects | jq

# Get a list of the versions avaiable for a specific subject
# curl --silent -X GET http://localhost:8081/subjects/edaAvroGenericEvent-value/versions | jq

# Get a specific version of a subject
# curl --silent -X GET http://localhost:8081/subjects/edaAvroGenericEvent-value/versions/1 | jq

# Delete a specific version of a subject, note this is actually a soft delete
# curl --silent -X DELETE http://localhost:8081/subjects/edaAvroGenericEvent-value/versions/1 | jq

# Get the top level compatability setting on schema registry
# curl --silent -X GET http://localhost:8081/config | jq

# Checks the compatability of the specified schema against the latest version in the registry
# jq '. | {schema: tojson}' $schemafile  | \
#    curl -X POST http://localhost:8081/compatability/subjects/${subject}-value/versions/latest \
#         -H "Content-Type: application/vnd.schemaregistry.v1+json" \
#         -d @-

# Set the compatability of the specified schema in the registry
# curl -X PUT -H "Content-Type: application/vnd.schemaregistry.v1+json" --data '{"compatibility": "FULL"}' http://localhost:8081/config/edaAvroGenericEvent-value

# List schema types currently registeered - May include JSON, AVRO, PROTOBUF
# curl --silent -X GET http://localhost:8081/schemas/types | jq