## Description
This API provides an interface to work with Motorist data from Informatica MDM via a set of RESTful services used for Motorist CRUD operations.

## Dependency
This API has a dependency on the underlying Informatica MDM system and the Informatica Motorist API.

## Endpoint
- `GET: {/motorists}` : List the motorist records which match the given search parameters. Below are the current search options.
    - First Name, Last Name (Fuzzy on name), Email Address
    - First Name, Last Name (Fuzzy on name), Phone Number
    - First Name, Last Name (Fuzzy on name), Phone Number, Email Address
    - First Name, Last Name (Fuzzy on name), Address Line 1 (fuzzy on address), City, Country
    - Party Identification Value, Party Identification Type.
- `POST: {/motorists}`: Creates a motorist record in Informatica MDM
- `GET: {/motorists/(motoristId)}` : Retrieve single motorist record from Informatica MDM.
- `PATCH: {/motorists/(motoristId)}`: Updates the motorist record in Informatica MDM
- `DELETE: {/motorists/(motoristId)}`: Deletes the motorist record in Informatica MDM.


