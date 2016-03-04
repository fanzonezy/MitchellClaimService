# MitchellClaimService

Part One: Basic System Architecture 

(1) Interanl Representation:
I used a user defined class MitchellClaimType to represent a claim. The definition of MitchellClaimType is based on other sub-type defined by xsd file: such as LossInfoType, VehicleInfoType, etc. What I did here is actually convert a XML schema to a bunch of JavaBean classes. All those classes are is in MitchellClaimServiceUtils.

(2) ORM model and Table Design
In the previous block gram, type (1), (2), (3) is defined by ORM model. ORM model classes are defined in MitchellClaimORMModels. 
Obviously, we can squeeze all claim information in a single table. Here, we simply used three tables: BasicInfo, LossInfo, VehicleInfo. What is worth mention here is that the ORM model class is the programmable representation of the table.

(3) How the system work

Part Two: Implementation and Components:
ORM: OrmLite
DataBase driver: sqlite
XML marshaler and unmarshaler: jaxb

The work flow of implementation is: 
(1) Define the behavior of APIs (May also made test cases, if working in TDD mode).
(2) Design data base schema(ORM classes).
(3) Define internal representation of claim XML(which is done automatically through JAXB).
(4) implment API.

Part Three: API Specification:
(1) createMtchellClaim:

input: String
output: None
throws: IllegalArgumentException, SQLException

spec: if seccessful, a claim will be persisted to back store. if the input is invalid, an IllegalArgumentException will be raised. if one of the write option to date base is failed, a SQLException will be raised. This API implement by transaction. 

(2) readClaimByClaimNumber:

input: string 
output: MitchellClaimType

spec: if seccessful, this API will return a MitchellClaimType instance which is uniquely determined by claim number.If the input claim number is invalid or the claim number is not in data base, an null will be returned. 

(2) readClaimByLossDate
input: (1) start date: Date (2) end date: Date

spec: if seccessful, a list of MitchellClaimType instance will be returned. if there is no claim with a loss date within the range a empty list will be return. 



